package com.plb.conference

import LoginViewModel
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plb.conference.services.AuthApi
import com.plb.conference.ui.screens.LoginScreen
import com.plb.conference.ui.theme.ConferenceTheme
import kotlin.math.log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConferenceTheme{
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(NetworkModule.authApi)
                )

                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        Log.d("Test", "logged")
                    }
                )
            }
        }
    }
}

class LoginViewModelFactory(
    private val authApi: AuthApi
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}