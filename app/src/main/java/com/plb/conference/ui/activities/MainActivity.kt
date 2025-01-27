package com.plb.conference.ui.activities

import NetworkModule
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yourapp.ui.screens.login.LoginViewModel
import com.plb.conference.data.UserPreferencesRepository
import com.plb.conference.services.AuthApi
import com.plb.conference.ui.screens.LoginScreen
import com.plb.conference.ui.theme.ConferenceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConferenceTheme  {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(NetworkModule.authApi, UserPreferencesRepository(this))
                )

                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = { token ->
                        // Navigate to HomePage with token
                        val intent = Intent(this, HomePageActivity::class.java).apply {
                            putExtra("token", token)
                        }
                        startActivity(intent)
                        finish() // Close login activity
                    }
                )
            }
        }
    }
}

class LoginViewModelFactory(
    private val authApi: AuthApi,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authApi,userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}