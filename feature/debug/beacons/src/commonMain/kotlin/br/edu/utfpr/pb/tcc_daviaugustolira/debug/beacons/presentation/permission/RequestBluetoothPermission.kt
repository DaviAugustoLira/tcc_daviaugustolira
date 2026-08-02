package br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.permission

import androidx.compose.runtime.Composable

/**
 * Só usada por esta tela hoje, então fica dentro da feature (não em :shared) — ver skill
 * platform-capability. Android pede a permissão de runtime via ActivityResult; no iOS a
 * autorização já é pedida dentro do CoreLocationBeaconScanner (requestWhenInUseAuthorization)
 * ao iniciar o ranging, então o `actual` daqui só libera o estado da UI.
 */
@Composable
expect fun RequestBluetoothPermission(onResult: (granted: Boolean) -> Unit)
