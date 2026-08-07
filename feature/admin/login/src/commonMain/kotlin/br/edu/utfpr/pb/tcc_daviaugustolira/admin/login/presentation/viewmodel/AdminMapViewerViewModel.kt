package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.CreatePointUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObservePointsUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointOutcome
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.Point
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PendingPoint(
    val x: Int,
    val y: Int,
)

data class AdminMapViewerState(
    val mapId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val points: List<Point> = emptyList(),
    val isPickModeActive: Boolean = false,
    val pendingPoint: PendingPoint? = null,
    val pointName: String = "",
    val pointDescription: String = "",
    val isSubmittingPoint: Boolean = false,
    val error: CreatePointError? = null,
)

sealed interface AdminMapViewerIntent {
    data object TogglePickMode : AdminMapViewerIntent

    data class TapAt(
        val x: Int,
        val y: Int,
    ) : AdminMapViewerIntent

    data class ManualCoordinateEntered(
        val x: Int,
        val y: Int,
    ) : AdminMapViewerIntent

    data class PointNameChanged(
        val value: String,
    ) : AdminMapViewerIntent

    data class PointDescriptionChanged(
        val value: String,
    ) : AdminMapViewerIntent

    data object ConfirmPoint : AdminMapViewerIntent

    data object CancelPendingPoint : AdminMapViewerIntent
}

/** Evento único (não faz parte do `State`) para anúncio de sucesso — ver design.md, Decisão 7. */
sealed interface AdminMapViewerEffect {
    data class PointCreated(
        val name: String,
    ) : AdminMapViewerEffect
}

/**
 * `mapId`/`name`/`imageUrl` chegam por navegação (`Screen.AdminMapViewer`) e alimentam tanto a
 * exibição quanto a query de pontos (`ObservePointsUseCase(mapId)`) e o cadastro
 * (`CreatePointUseCase`) — ver design.md do change admin-map-point-registration, Decisão 3.
 */
class AdminMapViewerViewModel(
    mapId: String,
    name: String,
    imageUrl: String,
    private val createPointUseCase: CreatePointUseCase,
    observePoints: ObservePointsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(AdminMapViewerState(mapId = mapId, name = name, imageUrl = imageUrl))
    val state: StateFlow<AdminMapViewerState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AdminMapViewerEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<AdminMapViewerEffect> = _effects.asSharedFlow()

    init {
        observePoints(mapId)
            .onEach { points -> _state.update { it.copy(points = points) } }
            .catch {
                // Falha ao observar pontos mantém a última lista conhecida em vez de derrubar a tela.
            }.launchIn(viewModelScope)
    }

    fun onIntent(intent: AdminMapViewerIntent) {
        when (intent) {
            AdminMapViewerIntent.TogglePickMode -> togglePickMode()
            is AdminMapViewerIntent.TapAt -> capturePending(intent.x, intent.y)
            is AdminMapViewerIntent.ManualCoordinateEntered -> capturePending(intent.x, intent.y)
            is AdminMapViewerIntent.PointNameChanged ->
                _state.update { it.copy(pointName = intent.value, error = null) }
            is AdminMapViewerIntent.PointDescriptionChanged ->
                _state.update { it.copy(pointDescription = intent.value) }
            AdminMapViewerIntent.ConfirmPoint -> confirmPoint()
            AdminMapViewerIntent.CancelPendingPoint -> clearPending()
        }
    }

    private fun togglePickMode() {
        _state.update {
            it.copy(
                isPickModeActive = !it.isPickModeActive,
                pendingPoint = null,
                pointName = "",
                pointDescription = "",
                error = null,
            )
        }
    }

    /**
     * Cada toque (ou coordenada manual) move o pino pendente para o último local indicado —
     * um único pino por vez, como o marcador de local do Google Maps — sem descartar o
     * nome/descrição já digitados quando o admin só está ajustando a posição de um pino que
     * já estava pendente.
     */
    private fun capturePending(
        x: Int,
        y: Int,
    ) {
        _state.update { current ->
            val isAdjustingExistingPending = current.pendingPoint != null
            current.copy(
                pendingPoint = PendingPoint(x = x, y = y),
                pointName = if (isAdjustingExistingPending) current.pointName else "",
                pointDescription = if (isAdjustingExistingPending) current.pointDescription else "",
                error = null,
            )
        }
    }

    private fun clearPending() {
        _state.update { it.copy(pendingPoint = null, pointName = "", pointDescription = "", error = null) }
    }

    private fun confirmPoint() {
        val current = _state.value
        val pending = current.pendingPoint ?: return
        if (current.isSubmittingPoint) return

        _state.update { it.copy(isSubmittingPoint = true, error = null) }
        viewModelScope.launch {
            val outcome =
                createPointUseCase(
                    mapId = current.mapId,
                    name = current.pointName,
                    description = current.pointDescription,
                    x = pending.x,
                    y = pending.y,
                )
            when (outcome) {
                CreatePointOutcome.Success -> {
                    _state.update {
                        it.copy(
                            isSubmittingPoint = false,
                            pendingPoint = null,
                            pointName = "",
                            pointDescription = "",
                        )
                    }
                    _effects.emit(AdminMapViewerEffect.PointCreated(current.pointName))
                }
                is CreatePointOutcome.Failure ->
                    _state.update { it.copy(isSubmittingPoint = false, error = outcome.error) }
            }
        }
    }
}
