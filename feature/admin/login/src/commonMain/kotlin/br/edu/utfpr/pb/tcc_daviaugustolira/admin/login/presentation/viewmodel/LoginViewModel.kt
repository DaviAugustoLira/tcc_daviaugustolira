package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.LoginUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminAuthError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.LoginOutcome
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val error: AdminAuthError? = null,
)

sealed interface LoginIntent {
    data class EmailChanged(
        val value: String,
    ) : LoginIntent

    data class PasswordChanged(
        val value: String,
    ) : LoginIntent

    data object Submit : LoginIntent
}

sealed interface LoginEffect {
    data object NavigateToAdminHome : LoginEffect
}

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<LoginEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<LoginEffect> = _effects.asSharedFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> _state.update { it.copy(email = intent.value, error = null) }
            is LoginIntent.PasswordChanged -> _state.update { it.copy(password = intent.value, error = null) }
            LoginIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val current = _state.value
        if (current.isSubmitting) return

        _state.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            when (val outcome = loginUseCase(current.email, current.password)) {
                is LoginOutcome.Success -> {
                    _state.update { it.copy(isSubmitting = false) }
                    _effects.emit(LoginEffect.NavigateToAdminHome)
                }
                is LoginOutcome.Failure -> {
                    _state.update { it.copy(isSubmitting = false, error = outcome.error) }
                }
            }
        }
    }
}
