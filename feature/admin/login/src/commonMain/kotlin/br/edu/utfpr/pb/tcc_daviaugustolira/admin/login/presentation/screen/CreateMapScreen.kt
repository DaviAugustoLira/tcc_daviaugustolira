package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.CreateMapEffect
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.CreateMapIntent
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.CreateMapState
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.CreateMapViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.CreateMapError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.INavigator
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.resource.Resources
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.Color
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontSize
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontWeight
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.components.CustomTextField
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateMapScreenRoute(navigator: INavigator) {
    val viewModel: CreateMapViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                CreateMapEffect.MapCreated -> navigator.navigateBack()
            }
        }
    }

    CreateMapScreen(
        state = state,
        onNameChanged = { viewModel.onIntent(CreateMapIntent.NameChanged(it)) },
        onDescriptionChanged = { viewModel.onIntent(CreateMapIntent.DescriptionChanged(it)) },
        onSvgUrlChanged = { viewModel.onIntent(CreateMapIntent.SvgUrlChanged(it)) },
        onScaleChanged = { viewModel.onIntent(CreateMapIntent.ScaleChanged(it)) },
        onFloorChanged = { viewModel.onIntent(CreateMapIntent.FloorChanged(it)) },
        onSubmit = { viewModel.onIntent(CreateMapIntent.Submit) },
        onNavigateBack = { navigator.navigateBack() },
    )
}

@Composable
fun CreateMapScreen(
    state: CreateMapState = CreateMapState(),
    onNameChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onSvgUrlChanged: (String) -> Unit = {},
    onScaleChanged: (String) -> Unit = {},
    onFloorChanged: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(state.error) {
        if (state.error != null) {
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
            CreateMapBackButton(onNavigateBack)
            Spacer(modifier = Modifier.size(12.dp))
            CreateMapHeader()
            Spacer(modifier = Modifier.size(24.dp))
            CreateMapFormFields(
                state = state,
                onNameChanged = onNameChanged,
                onDescriptionChanged = onDescriptionChanged,
                onSvgUrlChanged = onSvgUrlChanged,
                onScaleChanged = onScaleChanged,
                onFloorChanged = onFloorChanged,
            )
            state.error?.let { CreateMapErrorMessage(it) }
            Spacer(modifier = Modifier.size(18.dp))
            CreateMapSubmitButton(isSubmitting = state.isSubmitting, onSubmit = onSubmit)
        }
    }
}

@Composable
private fun CreateMapBackButton(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        Row(
            modifier =
                Modifier
                    .heightIn(min = 48.dp)
                    .clickable(onClickLabel = "Voltar", role = Role.Button, onClick = onNavigateBack)
                    .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Image(painterResource(Resources.Icons.ArrowLeftCircle), contentDescription = null)
            Text(fontWeight = FontWeight.MEDIUM, text = "Voltar")
        }
    }
}

@Composable
private fun CreateMapHeader() {
    Text(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .semantics { heading() },
        textAlign = TextAlign.Center,
        fontSize = FontSize.LARGE,
        fontWeight = FontWeight.SEMIBOLD,
        text = "Cadastrar novo mapa",
    )
}

@Composable
private fun CreateMapFormFields(
    state: CreateMapState,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSvgUrlChanged: (String) -> Unit,
    onScaleChanged: (String) -> Unit,
    onFloorChanged: (String) -> Unit,
) {
    CreateMapTextField(label = "Nome do mapa", value = state.name, onValueChange = onNameChanged)
    Spacer(modifier = Modifier.size(14.dp))
    CreateMapTextField(label = "Descrição", value = state.description, onValueChange = onDescriptionChanged)
    Spacer(modifier = Modifier.size(14.dp))
    CreateMapTextField(
        label = "URL do SVG",
        value = state.svgUrl,
        onValueChange = onSvgUrlChanged,
        keyboardType = KeyboardType.Uri,
    )
    Spacer(modifier = Modifier.size(14.dp))
    CreateMapTextField(
        label = "Escala",
        value = state.scaleText,
        onValueChange = onScaleChanged,
        keyboardType = KeyboardType.Decimal,
    )
    Spacer(modifier = Modifier.size(14.dp))
    CreateMapTextField(
        label = "Andar",
        value = state.floorText,
        onValueChange = onFloorChanged,
        keyboardType = KeyboardType.Number,
    )
}

@Composable
private fun CreateMapTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType? = null,
) {
    CustomTextField(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
        label = label,
        value = value,
        onValueChange = onValueChange,
        keyboardType = keyboardType,
    )
}

@Composable
private fun CreateMapErrorMessage(error: CreateMapError) {
    Spacer(modifier = Modifier.size(12.dp))
    Text(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .semantics { liveRegion = LiveRegionMode.Assertive },
        textAlign = TextAlign.Center,
        fontSize = FontSize.SMALL,
        fontWeight = FontWeight.MEDIUM,
        text = errorMessage(error),
    )
}

@Composable
private fun CreateMapSubmitButton(
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    Button(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onSubmit()
        },
        enabled = !isSubmitting,
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
                ).semantics {
                    contentDescription = if (isSubmitting) "Cadastrando mapa, aguarde" else "Cadastrar mapa"
                },
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 32.dp),
            fontWeight = FontWeight.SEMIBOLD,
            fontSize = FontSize.MEDIUM,
            text = if (isSubmitting) "Cadastrando..." else "Cadastrar",
        )
    }
}

private fun errorMessage(error: CreateMapError): String =
    when (error) {
        CreateMapError.EmptyName -> "Informe o nome do mapa."
        CreateMapError.EmptySvgUrl -> "Informe a URL do SVG do mapa."
        CreateMapError.InvalidScale -> "Informe uma escala numérica válida."
        CreateMapError.InvalidFloor -> "Informe um andar numérico válido."
        CreateMapError.NoActiveSession -> "Sessão administrativa expirada. Faça login novamente."
        CreateMapError.NetworkUnavailable -> "Sem conexão com a internet. Verifique sua rede e tente novamente."
        is CreateMapError.Unknown -> "Não foi possível cadastrar o mapa agora. Tente novamente em instantes."
    }
