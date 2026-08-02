package br.edu.utfpr.pb.tcc_daviaugustolira.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.screen.BeaconDebugScreen
import br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.viewmodel.BeaconDebugState
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon.BeaconReading

@Composable
@Preview(showBackground = true)
fun BeaconDebugPreview() {
    BeaconDebugScreen(
        state =
            BeaconDebugState(
                permissionGranted = true,
                isScanning = true,
                readings =
                    listOf(
                        BeaconReading(
                            uuid = "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0",
                            major = 1,
                            minor = 12,
                            rssi = -62,
                            txPower = -59,
                            timestampMs = 0L,
                        ),
                    ),
            ),
    )
}

@Composable
@Preview(showBackground = true)
fun BeaconDebugPreviewEmpty() {
    BeaconDebugScreen(
        state = BeaconDebugState(permissionGranted = true, isScanning = true),
    )
}

@Composable
@Preview(showBackground = true)
fun BeaconDebugPreviewPermissionDenied() {
    BeaconDebugScreen(
        state = BeaconDebugState(permissionGranted = false),
    )
}
