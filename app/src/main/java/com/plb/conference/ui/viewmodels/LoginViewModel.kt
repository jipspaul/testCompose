import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plb.conference.services.AuthApi
import com.plb.conference.ui.models.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authApi: AuthApi
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private var _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private var _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            try {
                val response = authApi.login(_email.value, _password.value)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = LoginUiState(isSuccess = true)
                } else {
                    _uiState.value = LoginUiState(error = "Invalid credentials")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState(error = e.message ?: "Unknown error")
            }
        }
    }
}