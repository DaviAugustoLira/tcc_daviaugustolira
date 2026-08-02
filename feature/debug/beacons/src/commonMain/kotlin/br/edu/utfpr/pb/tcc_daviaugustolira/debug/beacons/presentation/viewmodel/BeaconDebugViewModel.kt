package br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon.BeaconReading
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon.BeaconScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class BeaconDebugState(
    val readings: List<BeaconReading> = emptyList(),
    val isScanning: Boolean = false,
    val permissionGranted: Boolean? = null,
)

sealed interface BeaconDebugIntent {
    data class PermissionResult(
        val granted: Boolean,
    ) : BeaconDebugIntent
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
}
