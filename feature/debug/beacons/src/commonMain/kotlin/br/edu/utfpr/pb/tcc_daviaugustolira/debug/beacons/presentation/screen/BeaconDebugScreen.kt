package br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.permission.RequestBluetoothPermission
import br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.viewmodel.BeaconDebugIntent
import br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.viewmodel.BeaconDebugState
import br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.viewmodel.BeaconDebugViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.INavigator
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon.BeaconReading
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.resource.Resources
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BeaconDebugScreenRoute(navigator: INavigator) {
    val viewModel: BeaconDebugViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    BeaconDebugScreen(
        state = state,
        onRequestPermissionResult = { granted ->
            viewModel.onIntent(BeaconDebugIntent.PermissionResult(granted))
        },
        onRunFirebaseHealthcheck = {
            viewModel.onIntent(BeaconDebugIntent.RunFirebaseHealthcheck)
        },
        onNavigateBack = { navigator.navigateBack() },
    )
}

@Composable
fun BeaconDebugScreen(
    state: BeaconDebugState = BeaconDebugState(),
    onRequestPermissionResult: (Boolean) -> Unit = {},
    onRunFirebaseHealthcheck: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    if (state.permissionGranted == null) {
        RequestBluetoothPermission(onResult = onRequestPermissionResult)
    }

    val hapticFeedback = LocalHapticFeedback.current
    var previousReadingCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(state.readings.size) {
        // Beacon novo entrando no alcance é o equivalente aqui a "ponto de interesse próximo"
        // (CLAUDE.md secao 2) — audio (liveRegion abaixo) + tátil, nunca só visual.
        if (state.readings.size > previousReadingCount) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        previousReadingCount = state.readings.size
    }

    Scaffold { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            BeaconDebugHeader(onNavigateBack = onNavigateBack)

            Text(
                modifier =
                    Modifier
                        .padding(top = 16.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                text = statusDescription(state),
            )

            FirebaseHealthcheckSection(
                result = state.healthcheckResult,
                onRunFirebaseHealthcheck = onRunFirebaseHealthcheck,
            )

            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.readings, key = { "${it.uuid}:${it.major}:${it.minor}" }) { reading ->
                    BeaconRow(reading)
                }
            }
        }
    }
}

@Composable
private fun BeaconDebugHeader(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(Resources.Icons.ArrowLeftCircle),
            contentDescription = "Voltar",
            modifier =
                Modifier
                    .size(48.dp)
                    .clickable(onClickLabel = "Voltar", role = Role.Button, onClick = onNavigateBack),
        )
        Text(
            modifier =
                Modifier
                    .padding(start = 12.dp)
                    .semantics { heading() },
            text = "Beacons detectados",
        )
    }
}

@Composable
private fun FirebaseHealthcheckSection(
    result: String?,
    onRunFirebaseHealthcheck: () -> Unit,
) {
    Button(
        modifier = Modifier.padding(top = 16.dp),
        onClick = onRunFirebaseHealthcheck,
    ) {
        Text("Testar Firebase (healthcheck)")
    }

    result?.let { healthcheckResult ->
        Text(
            modifier =
                Modifier
                    .padding(top = 8.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
            text = healthcheckResult,
        )
    }
}

@Composable
private fun BeaconRow(reading: BeaconReading) {
    Text(
        modifier =
            Modifier.semantics {
                contentDescription =
                    "Beacon major ${reading.major}, minor ${reading.minor}, sinal ${reading.rssi} dBm"
            },
        text = "UUID ${reading.uuid} · major ${reading.major} · minor ${reading.minor} · RSSI ${reading.rssi} dBm",
    )
}

private fun statusDescription(state: BeaconDebugState): String =
    when {
        state.permissionGranted == false ->
            "Permissão de Bluetooth e localização negada. Não é possível escanear beacons."
        state.permissionGranted == null -> "Pedindo permissão de Bluetooth e localização."
        state.readings.isEmpty() -> "Escaneando. Nenhum beacon encontrado ainda."
        else -> "${state.readings.size} beacons encontrados."
    }
