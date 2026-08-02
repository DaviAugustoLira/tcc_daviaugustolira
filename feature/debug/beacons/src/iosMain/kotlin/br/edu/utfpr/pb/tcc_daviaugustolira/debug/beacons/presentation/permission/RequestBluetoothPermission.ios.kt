package br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun RequestBluetoothPermission(onResult: (granted: Boolean) -> Unit) {
    // A autorização de localização já é solicitada dentro do CoreLocationBeaconScanner
    // (requestWhenInUseAuthorization) ao iniciar o ranging — aqui só libera a UI pra tentar.
    LaunchedEffect(Unit) {
        onResult(true)
    }
}
