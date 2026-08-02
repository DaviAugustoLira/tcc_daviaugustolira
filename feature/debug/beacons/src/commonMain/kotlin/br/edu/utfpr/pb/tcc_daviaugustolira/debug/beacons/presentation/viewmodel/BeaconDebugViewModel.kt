package br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.healthcheck.runFirebaseHealthcheck
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon.BeaconReading
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon.BeaconScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BeaconDebugState(
    val readings: List<BeaconReading> = emptyList(),
    val isScanning: Boolean = false,
    val permissionGranted: Boolean? = null,
    val healthcheckResult: String? = null,
)

sealed interface BeaconDebugIntent {
    data class PermissionResult(
        val granted: Boolean,
    ) : BeaconDebugIntent

    data object RunFirebaseHealthcheck : BeaconDebugIntent
}

class BeaconDebugViewModel(
    private val beaconScanner: BeaconScanner,
) : ViewModel() {
    private val _state = MutableStateFlow(BeaconDebugState())
    val state: StateFlow<BeaconDebugState> = _state.asStateFlow()

    private var isScanStarted = false

    fun onIntent(intent: BeaconDebugIntent) {
        when (intent) {
            is BeaconDebugIntent.PermissionResult -> onPermissionResult(intent.granted)
            is BeaconDebugIntent.RunFirebaseHealthcheck -> runHealthcheck()
        }
    }

    private fun onPermissionResult(granted: Boolean) {
        _state.update { it.copy(permissionGranted = granted) }
        if (granted) startScanning()
    }

    private fun startScanning() {
        if (isScanStarted) return
        isScanStarted = true
        _state.update { it.copy(isScanning = true) }
        beaconScanner
            .readings()
            .onEach { readings -> _state.update { it.copy(readings = readings) } }
            .launchIn(viewModelScope)
    }

    private fun runHealthcheck() {
        _state.update { it.copy(healthcheckResult = "Testando conexão com o Firestore...") }
        viewModelScope.launch {
            val result = runFirebaseHealthcheck()
            val message =
                result.fold(
                    onSuccess = { ok ->
                        if (ok) {
                            "Firestore ok: leitura e escrita confirmadas."
                        } else {
                            "Firestore respondeu, mas o valor lido não bate."
                        }
                    },
                    onFailure = { error -> "Falha ao falar com o Firestore: ${error.message}" },
                )
            _state.update { it.copy(healthcheckResult = message) }
        }
    }
}
