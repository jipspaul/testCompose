package com.plb.conference.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plb.conference.ui.theme.ConferenceTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

data class UserInfo(
    val id: String,
    val email: String,
    val full_name: String,
    val company: String?,
    val is_active: Boolean
)

// API Interface
interface UserApi {
    @GET("users/me")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): Response<UserInfo>
}

// UI State
data class HomePageUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userInfo: UserInfo? = null
)

// ViewModel
class HomePageViewModel(
    private val userApi: UserApi,
    private val token: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomePageUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            try {
                val response = userApi.getUserInfo("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = HomePageUiState(userInfo = response.body())
                } else {
                    _uiState.value = HomePageUiState(error = "Failed to load user info")
                }
            } catch (e: Exception) {
                _uiState.value = HomePageUiState(error = e.message ?: "Unknown error")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen(
    viewModel: HomePageViewModel,
    onNavigateToMeetings: () -> Unit,
    onNavigateToRooms: () -> Unit,
    onNavigateToHosts: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reception App") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Text("Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    when {
                        uiState.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                        uiState.error != null -> {
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        uiState.userInfo != null -> {
                            Text(
                                text = "Welcome, ${uiState.userInfo!!.full_name}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Email: ${uiState.userInfo!!.email}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (!uiState.userInfo!!.company.isNullOrEmpty()) {
                                Text(
                                    text = "Company: ${uiState.userInfo!!.company}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }

            // Navigation Buttons
            ElevatedButton(
                onClick = onNavigateToMeetings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("My Meetings")
            }

            ElevatedButton(
                onClick = onNavigateToRooms,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Conference Rooms")
            }

            ElevatedButton(
                onClick = onNavigateToHosts,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Available Hosts")
            }
        }
    }
}

class HomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get token from intent
        val token = intent.getStringExtra("token") ?: ""

        setContent {
            ConferenceTheme {
                val viewModel: HomePageViewModel = viewModel(
                    factory = HomePageViewModelFactory(NetworkModule.userApi, token)
                )

                HomePageScreen(
                    viewModel = viewModel,
                    onNavigateToMeetings = {
                        // Start MeetingsActivity
                        // startActivity(Intent(this, MeetingsActivity::class.java))
                    },
                    onNavigateToRooms = {
                        // Start RoomsActivity
                        // startActivity(Intent(this, RoomsActivity::class.java))
                    },
                    onNavigateToHosts = {
                        // Start HostsActivity
                        // startActivity(Intent(this, HostsActivity::class.java))
                    },
                    onLogout = {
                        // Clear token and go back to login
                        finish()
                    }
                )
            }
        }
    }
}

class HomePageViewModelFactory(
    private val userApi: UserApi,
    private val token: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomePageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomePageViewModel(userApi, token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}