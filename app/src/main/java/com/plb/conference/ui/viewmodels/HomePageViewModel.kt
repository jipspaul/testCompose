package com.example.yourapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plb.conference.data.UserPreferencesRepository
import com.plb.conference.ui.activities.UserApi
import com.plb.conference.ui.activities.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomePageViewModel(
    private val userApi: UserApi,
    private val userPreferencesRepository: UserPreferencesRepository,
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

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearAuthToken()
            _uiState.value = _uiState.value.copy(isLoggedOut = true)
        }
    }
}

data class HomePageUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userInfo: UserInfo? = null,
    val isLoggedOut: Boolean = false
)