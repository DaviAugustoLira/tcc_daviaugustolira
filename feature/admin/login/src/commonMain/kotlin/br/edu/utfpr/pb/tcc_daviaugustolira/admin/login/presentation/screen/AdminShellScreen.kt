package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminShellIntent
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminShellState
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminShellViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.INavigator
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.Color
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontSize
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontWeight
import org.koin.compose.viewmodel.koinViewModel

/**
 * Casca mínima da área administrativa (Feature 1). Não representa nenhuma tela de
 * conteúdo real ainda — features futuras (Feature 6+) se registram como novas rotas
 * `Screen.Admin*` e reusam o `AdminRouteGuard`. Sair navega de volta ao login: a
 * navegação em si é feita reativamente pelo guard ao ver a sessão virar Unauthenticated.
 */
@Composable
fun AdminShellScreenRoute(
    @Suppress("UNUSED_PARAMETER") navigator: INavigator,
) {
    val viewModel: AdminShellViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    AdminShellScreen(
        state = state,
        onLogout = { viewModel.onIntent(AdminShellIntent.Logout) },
    )
}

@Composable
fun AdminShellScreen(
    state: AdminShellState = AdminShellState(),
    onLogout: () -> Unit = {},
) {
    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(state.mapsError) {
        if (state.mapsError != null) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        containerColor = Color.BACKGROUND_20,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .padding(bottom = 20.dp)
                    .clip(shape = RoundedCornerShape(size = 25.dp))
                    .fillMaxSize()
                    .background(Color.BACKGROUND_10),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AdminShellGreeting(email = state.adminEmail)
            Spacer(modifier = Modifier.size(30.dp))
            AdminShellMapsSection(
                maps = state.maps,
                isLoading = state.isLoadingMaps,
                error = state.mapsError,
            )
            Spacer(modifier = Modifier.size(25.dp))
            AdminShellLogoutButton(onLogout = onLogout)
        }
    }
}

@Composable
private fun AdminShellGreeting(email: String?) {
    Text(
        modifier =
            Modifier
                .padding(top = 30.dp)
                .fillMaxWidth()
                .semantics { heading() },
        textAlign = TextAlign.Center,
        fontSize = FontSize.LARGE,
        fontWeight = FontWeight.SEMIBOLD,
        text = if (email != null) "Olá, $email" else "Olá, administrador",
    )
}

@Composable
private fun AdminShellMapsSection(
    maps: List<IndoorMap>,
    isLoading: Boolean,
    error: String?,
) {
    Text(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
        textAlign = TextAlign.Center,
        fontSize = FontSize.MEDIUM,
        fontWeight = FontWeight.BOLD,
        text = "Seus maps",
    )
    Spacer(modifier = Modifier.size(12.dp))

    when {
        isLoading -> AdminShellMapsStatus("Carregando maps...")
        error != null -> AdminShellMapsStatus("Não foi possível carregar os maps agora.")
        maps.isEmpty() -> AdminShellMapsStatus("Nenhum map disponível ainda.")
        else -> AdminShellMapsList(maps)
    }
}

@Composable
private fun AdminShellMapsStatus(message: String) {
    Text(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
        textAlign = TextAlign.Center,
        fontSize = FontSize.SMALL,
        fontWeight = FontWeight.MEDIUM,
        text = message,
    )
}

@Composable
private fun AdminShellMapsList(maps: List<IndoorMap>) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(maps, key = { it.id }) { map ->
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 12.dp))
                        .background(Color.BACKGROUND_30)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                fontSize = FontSize.MEDIUM,
                fontWeight = FontWeight.MEDIUM,
                text = map.name,
            )
        }
    }
}

@Composable
private fun AdminShellLogoutButton(onLogout: () -> Unit) {
    val hapticFeedback = LocalHapticFeedback.current

    Button(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onLogout()
        },
        colors =
            ButtonDefaults.buttonColors(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                contentColor = Color.BACKGROUND_10,
            ),
        modifier =
            Modifier
                .clip(RoundedCornerShape(size = 24.dp))
                .background(
                    brush = Brush.horizontalGradient(colors = listOf(Color.ORANGE_10, Color.ORANGE_20)),
                ).semantics { contentDescription = "Sair da conta administrativa" },
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 32.dp),
            fontWeight = FontWeight.SEMIBOLD,
            fontSize = FontSize.MEDIUM,
            text = "Sair",
        )
    }
}
