package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.CreateMapUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.CreateMapError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.CreateMapOutcome
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateMapState(
    val name: String = "",
    val description: String = "",
    val svgUrl: String = "",
    val scaleText: String = "",
    val floorText: String = "",
    val isSubmitting: Boolean = false,
    val error: CreateMapError? = null,
)

sealed interface CreateMapIntent {
    data class NameChanged(
        val value: String,
    ) : CreateMapIntent

    data class DescriptionChanged(
        val value: String,
    ) : CreateMapIntent

    data class SvgUrlChanged(
        val value: String,
    ) : CreateMapIntent

    data class ScaleChanged(
        val value: String,
    ) : CreateMapIntent

    data class FloorChanged(
        val value: String,
    ) : CreateMapIntent

    data object Submit : CreateMapIntent
}

sealed interface CreateMapEffect {
    data object MapCreated : CreateMapEffect
}

class CreateMapViewModel(
    private val createMapUseCase: CreateMapUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CreateMapState())
    val state: StateFlow<CreateMapState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<CreateMapEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<CreateMapEffect> = _effects.asSharedFlow()

    fun onIntent(intent: CreateMapIntent) {
        when (intent) {
            is CreateMapIntent.NameChanged -> _state.update { it.copy(name = intent.value, error = null) }
            is CreateMapIntent.DescriptionChanged -> _state.update { it.copy(description = intent.value, error = null) }
            is CreateMapIntent.SvgUrlChanged -> _state.update { it.copy(svgUrl = intent.value, error = null) }
            is CreateMapIntent.ScaleChanged -> _state.update { it.copy(scaleText = intent.value, error = null) }
            is CreateMapIntent.FloorChanged -> _state.update { it.copy(floorText = intent.value, error = null) }
            CreateMapIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val current = _state.value
        if (current.isSubmitting) return

        _state.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            val outcome =
                createMapUseCase(
                    name = current.name,
                    description = current.description,
                    svgUrl = current.svgUrl,
                    scale = current.scaleText.trim().toDoubleOrNull(),
                    floor = current.floorText.trim().toIntOrNull(),
                )
            when (outcome) {
                is CreateMapOutcome.Success -> {
                    _state.update { it.copy(isSubmitting = false) }
                    _effects.emit(CreateMapEffect.MapCreated)
                }
                is CreateMapOutcome.Failure -> {
                    _state.update { it.copy(isSubmitting = false, error = outcome.error) }
                }
            }
        }
    }
}
