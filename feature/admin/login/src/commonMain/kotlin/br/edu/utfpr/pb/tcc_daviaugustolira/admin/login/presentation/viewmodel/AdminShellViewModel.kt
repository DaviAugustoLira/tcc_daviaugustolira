package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.LogoutUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObserveMapsUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObserveSessionUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminShellState(
    val adminEmail: String? = null,
    val maps: List<IndoorMap> = emptyList(),
    val isLoadingMaps: Boolean = true,
    val mapsError: String? = null,
)

sealed interface AdminShellIntent {
    data object Logout : AdminShellIntent
}

/**
 * A redireção pós-logout não é feita aqui — o AdminRouteGuard reage à transição de
 * AdminSessionState para Unauthenticated e navega, evitando duplicar lógica de
 * navegação de sessão (ver design.md do change admin-auth-shell).
 */
class AdminShellViewModel(
    private val logoutUseCase: LogoutUseCase,
    observeSession: ObserveSessionUseCase,
    observeMaps: ObserveMapsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(AdminShellState())
    val state: StateFlow<AdminShellState> = _state.asStateFlow()

    init {
        observeSession()
            .onEach { sessionState ->
                val email = (sessionState as? AdminSessionState.Authenticated)?.session?.email
                _state.update { it.copy(adminEmail = email) }
            }.launchIn(viewModelScope)

        observeMaps()
            .onEach { maps ->
                _state.update { it.copy(maps = maps, isLoadingMaps = false, mapsError = null) }
            }.catch { error ->
                _state.update { it.copy(isLoadingMaps = false, mapsError = error.message) }
            }.launchIn(viewModelScope)
    }

    fun onIntent(intent: AdminShellIntent) {
        when (intent) {
            AdminShellIntent.Logout -> viewModelScope.launch { logoutUseCase() }
        }
    }
}
