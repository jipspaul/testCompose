package com.plb.conference.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import java.time.LocalDateTime

data class Meeting(
    val id: String,
    val title: String,
    val start_time: LocalDateTime,
    val end_time: LocalDateTime,
    val room_name: String,
    val hosts: List<String>,
    val status: String
)

interface MeetingsApi {
    @GET("meetings/")
    suspend fun getMeetings(
        @Header("Authorization") token: String
    ): Response<List<Meeting>>
}

data class MeetingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val meetings: List<Meeting> = emptyList()
)

class MeetingsViewModel(
    private val meetingsApi: MeetingsApi,
    private val token: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(MeetingsUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadMeetings()
    }

    private fun loadMeetings() {
        viewModelScope.launch {
            try {
                val response = meetingsApi.getMeetings("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = MeetingsUiState(meetings = response.body()!!)
                } else {
                    _uiState.value = MeetingsUiState(error = "Failed to load meetings")
                }
            } catch (e: Exception) {
                _uiState.value = MeetingsUiState(error = e.message ?: "Unknown error")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingsScreen(
    viewModel: MeetingsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Meetings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                uiState.meetings.isEmpty() -> {
                    Text(
                        text = "No meetings scheduled",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.meetings, key = { it.id }) { meeting ->
                            MeetingCard(meeting = meeting)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingCard(meeting: Meeting) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = meeting.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Room: ${meeting.room_name}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Status: ${meeting.status}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Time: ${meeting.start_time.toLocalTime()} - ${meeting.end_time.toLocalTime()}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (meeting.hosts.isNotEmpty()) {
                Text(
                    text = "Hosts: ${meeting.hosts.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}