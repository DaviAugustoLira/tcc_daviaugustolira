package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminMapViewerEffect
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminMapViewerIntent
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminMapViewerState
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminMapViewerViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.PendingPoint
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.mapTapToImagePixel
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.Point
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.INavigator
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.Screen
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.resource.Resources
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.Color
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontSize
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontWeight
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.components.CustomTextField
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val MIN_ZOOM = 1f
private const val DEFAULT_ZOOM = 2f
private const val MAX_ZOOM = 6f
private val MARKER_SIZE = 20.dp
private val PENDING_MARKER_SIZE = 24.dp

@Composable
fun AdminMapViewerScreenRoute(
    navigator: INavigator,
    screen: Screen.AdminMapViewer,
) {
    val viewModel: AdminMapViewerViewModel =
        koinViewModel { parametersOf(screen.mapId, screen.name, screen.imageUrl) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val hapticFeedback = LocalHapticFeedback.current
    var announcement by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AdminMapViewerEffect.PointCreated -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    announcement = "Destino ${effect.name} cadastrado"
                }
            }
        }
    }
    LaunchedEffect(announcement) {
        if (announcement != null) {
            delay(3_000)
            announcement = null
        }
    }

    AdminMapViewerScreen(
        state = state,
        announcement = announcement,
        onIntent = viewModel::onIntent,
        onNavigateBack = { navigator.navigateBack() },
    )
}

/**
 * Zoom/pan + toque para "pickar" um destino (change admin-map-point-registration). A base de
 * exibição ajusta a imagem à largura do viewport (`fitScale`); pinça multiplica esse valor até
 * `MAX_ZOOM`. Toque em modo de cadastro é convertido para pixel da imagem original via
 * `mapTapToImagePixel` (ver design.md, Decisão 5).
 */
@Composable
fun AdminMapViewerScreen(
    state: AdminMapViewerState = AdminMapViewerState(),
    announcement: String? = null,
    onIntent: (AdminMapViewerIntent) -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(state.error) {
        if (state.error != null) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(containerColor = Color.BACKGROUND_10) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            AdminMapViewerImage(state = state, onIntent = onIntent)
            AdminMapViewerTopBar(onNavigateBack = onNavigateBack)
            AdminMapViewerActions(state = state, onIntent = onIntent)
            announcement?.let { AdminMapViewerAnnouncement(text = it) }
            state.pendingPoint?.let {
                AdminMapViewerPendingForm(state = state, onIntent = onIntent)
            }
        }
    }
}

/** Nó só de anúncio: some visualmente rápido, mas o `liveRegion` garante que o leitor de tela fale a confirmação. */
@Composable
private fun BoxScope.AdminMapViewerAnnouncement(text: String) {
    Text(
        modifier =
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
                .fillMaxWidth()
                .background(Color.BACKGROUND_30)
                .padding(12.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        fontSize = FontSize.SMALL,
        fontWeight = FontWeight.MEDIUM,
        text = text,
    )
}

@Composable
private fun AdminMapViewerImage(
    state: AdminMapViewerState,
    onIntent: (AdminMapViewerIntent) -> Unit,
) {
    // Padrão de 200% (Decisão do ajuste visual) — o mapa já nasce ampliado em relação ao
    // ajuste de largura (`fitScale` = 100%), mas o admin pode arrastar e usar pinça livremente
    // entre `MIN_ZOOM` (mapa inteiro) e `MAX_ZOOM`.
    var zoom by remember { mutableStateOf(DEFAULT_ZOOM) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    var intrinsicSize by remember { mutableStateOf(IntSize.Zero) }

    val fitScale = if (intrinsicSize.width > 0) viewportSize.width.toFloat() / intrinsicSize.width else 1f
    val combinedScale = fitScale * zoom

    val transformableState =
        rememberTransformableState { zoomChange, panChange, _ ->
            val newZoom = (zoom * zoomChange).coerceIn(MIN_ZOOM, MAX_ZOOM)
            val newCombinedScale = fitScale * newZoom
            val scaledWidth = intrinsicSize.width * newCombinedScale
            val scaledHeight = intrinsicSize.height * newCombinedScale
            val minX = minOf(0f, viewportSize.width - scaledWidth)
            val maxX = maxOf(0f, viewportSize.width - scaledWidth)
            val minY = minOf(0f, viewportSize.height - scaledHeight)
            val maxY = maxOf(0f, viewportSize.height - scaledHeight)
            val newPan = pan + panChange

            zoom = newZoom
            pan = Offset(x = newPan.x.coerceIn(minX, maxX), y = newPan.y.coerceIn(minY, maxY))
        }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .onSizeChanged { viewportSize = it }
                .transformable(state = transformableState)
                .pointerInput(state.isPickModeActive) {
                    if (state.isPickModeActive) {
                        detectTapGestures { tapOffset ->
                            val pixel =
                                mapTapToImagePixel(
                                    tapOffset = tapOffset,
                                    panOffset = pan,
                                    combinedScale = combinedScale,
                                    intrinsicWidth = intrinsicSize.width,
                                    intrinsicHeight = intrinsicSize.height,
                                )
                            onIntent(AdminMapViewerIntent.TapAt(pixel.x, pixel.y))
                        }
                    }
                },
    ) {
        Box(
            modifier =
                Modifier.graphicsLayer(
                    scaleX = combinedScale,
                    scaleY = combinedScale,
                    translationX = pan.x,
                    translationY = pan.y,
                    transformOrigin = TransformOrigin(0f, 0f),
                ),
        ) {
            AsyncImage(
                model = state.imageUrl,
                contentDescription = "Imagem do mapa ${state.name}",
                contentScale = ContentScale.None,
                onSuccess = { successState: AsyncImagePainter.State.Success ->
                    val size = successState.painter.intrinsicSize
                    if (size.isSpecified) {
                        intrinsicSize = IntSize(size.width.toInt(), size.height.toInt())
                    }
                },
            )
            AdminMapViewerMarkersGroup(points = state.points, pendingPoint = state.pendingPoint)
        }
    }
}

/**
 * Um único nó de semântica (`mergeDescendants`) resume a contagem de destinos em vez de expor
 * cada marcador individualmente — evita que o TalkBack/VoiceOver precise varrer dezenas de
 * pins ao percorrer a tela linearmente (ver skill accessibility-audit, item "Agrupamento e
 * ordem de foco"). Os marcadores em si são puramente visuais/decorativos.
 */
@Composable
private fun AdminMapViewerMarkersGroup(
    points: List<Point>,
    pendingPoint: PendingPoint?,
) {
    Box(
        modifier =
            Modifier.semantics(mergeDescendants = true) {
                contentDescription = markersSummary(points = points, pendingPoint = pendingPoint)
            },
    ) {
        points.forEach { point -> AdminMapViewerMarker(point = point) }
        pendingPoint?.let { pending -> AdminMapViewerPendingMarker(pending = pending) }
    }
}

private fun markersSummary(
    points: List<Point>,
    pendingPoint: PendingPoint?,
): String {
    val base =
        when (points.size) {
            0 -> "Nenhum destino cadastrado neste mapa"
            1 -> "1 destino cadastrado neste mapa"
            else -> "${points.size} destinos cadastrados neste mapa"
        }
    return if (pendingPoint != null) "$base, 1 destino pendente de confirmação" else base
}

@Composable
private fun AdminMapViewerMarker(point: Point) {
    val radiusPx = with(LocalDensity.current) { (MARKER_SIZE / 2).roundToPx() }
    Box(
        modifier =
            Modifier
                .offset { IntOffset(point.x - radiusPx, point.y - radiusPx) }
                .size(MARKER_SIZE)
                .clip(CircleShape)
                .background(Color.ORANGE_20),
    )
}

@Composable
private fun AdminMapViewerPendingMarker(pending: PendingPoint) {
    val radiusPx = with(LocalDensity.current) { (PENDING_MARKER_SIZE / 2).roundToPx() }
    Box(
        modifier =
            Modifier
                .offset { IntOffset(pending.x - radiusPx, pending.y - radiusPx) }
                .size(PENDING_MARKER_SIZE)
                .clip(CircleShape)
                .background(
                    androidx.compose.ui.graphics.Color.Red
                        .copy(alpha = 0.6f),
                ),
    )
}

@Composable
private fun AdminMapViewerTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.BACKGROUND_30)
                .padding(8.dp)
                // Prioriza o botão de voltar na ordem de leitura do TalkBack/VoiceOver, à
                // frente da imagem e dos marcadores — sem isso, um leitor de tela varreria a
                // imagem inteira antes de chegar aqui.
                .semantics { traversalIndex = -1f },
    ) {
        AdminMapViewerBackButton(onNavigateBack = onNavigateBack)
    }
}

@Composable
private fun AdminMapViewerBackButton(onNavigateBack: () -> Unit) {
    Row(
        modifier =
            Modifier
                .heightIn(min = 48.dp)
                .clickable(onClickLabel = "Fechar visualização do mapa", role = Role.Button, onClick = onNavigateBack)
                .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Image(painterResource(Resources.Icons.ArrowLeftCircle), contentDescription = null)
        Text(fontWeight = FontWeight.MEDIUM, fontSize = FontSize.SMALL, text = "Fechar")
    }
}

/**
 * Botão flutuante que expande em duas ações — "Adicionar" (ativa o toque no mapa) e "Editar"
 * (mostra a entrada manual de coordenada, escondida por padrão). Substitui os controles que
 * antes ficavam sempre visíveis na barra superior, para não poluir a tela com a entrada de
 * texto de X/Y quando o admin só quer visualizar o map.
 */
@Composable
private fun BoxScope.AdminMapViewerActions(
    state: AdminMapViewerState,
    onIntent: (AdminMapViewerIntent) -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isManualEntryVisible by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .semantics { traversalIndex = -1f },
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (isManualEntryVisible) {
            AdminMapViewerManualCoordinateEntry(onIntent = onIntent)
        }
        if (isMenuExpanded) {
            AdminMapViewerFabAction(
                label = "Editar",
                description =
                    if (isManualEntryVisible) "Fechar edição de coordenada manual" else "Editar coordenada manualmente",
                isActive = isManualEntryVisible,
                onClick = {
                    isManualEntryVisible = !isManualEntryVisible
                    isMenuExpanded = false
                },
            )
            AdminMapViewerFabAction(
                label = "Adicionar",
                description =
                    if (state.isPickModeActive) "Desativar cadastro de destino por toque" else "Adicionar destino tocando no mapa",
                isActive = state.isPickModeActive,
                onClick = {
                    onIntent(AdminMapViewerIntent.TogglePickMode)
                    isMenuExpanded = false
                },
            )
        }
        AdminMapViewerMainFab(
            isExpanded = isMenuExpanded,
            onToggle = { isMenuExpanded = !isMenuExpanded },
        )
    }
}

@Composable
private fun AdminMapViewerFabAction(
    label: String,
    description: String,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    Button(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        colors =
            ButtonDefaults.buttonColors(
                containerColor = if (isActive) Color.ORANGE_20 else Color.BACKGROUND_10,
                contentColor = if (isActive) Color.BACKGROUND_10 else Color.BLACK_ABSOLUTE,
            ),
        modifier =
            Modifier
                .heightIn(min = 48.dp)
                .semantics { contentDescription = description },
    ) {
        Text(fontWeight = FontWeight.MEDIUM, fontSize = FontSize.SMALL, text = label)
    }
}

@Composable
private fun AdminMapViewerMainFab(
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    FloatingActionButton(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onToggle()
        },
        containerColor = Color.ORANGE_20,
        contentColor = Color.BACKGROUND_10,
        modifier =
            Modifier.semantics {
                contentDescription = if (isExpanded) "Fechar menu de ações do mapa" else "Abrir menu de ações do mapa"
            },
    ) {
        Text(fontSize = FontSize.LARGE, fontWeight = FontWeight.SEMIBOLD, text = if (isExpanded) "×" else "+")
    }
}

@Composable
private fun AdminMapViewerManualCoordinateEntry(onIntent: (AdminMapViewerIntent) -> Unit) {
    var xText by remember { mutableStateOf("") }
    var yText by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .clip(RoundedCornerShape(size = 16.dp))
                .background(Color.BACKGROUND_30)
                .padding(12.dp),
    ) {
        Text(
            fontSize = FontSize.SMALL,
            fontWeight = FontWeight.REGULAR,
            text = "Coordenada em pixels do destino",
        )
        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CustomTextField(
                label = "X (px)",
                value = xText,
                onValueChange = {
                    xText = it
                    validationError = false
                },
                keyboardType = KeyboardType.Number,
            )
            CustomTextField(
                label = "Y (px)",
                value = yText,
                onValueChange = {
                    yText = it
                    validationError = false
                },
                keyboardType = KeyboardType.Number,
            )
            Button(
                onClick = {
                    val x = xText.trim().toIntOrNull()
                    val y = yText.trim().toIntOrNull()
                    if (x != null && y != null && x >= 0 && y >= 0) {
                        onIntent(AdminMapViewerIntent.ManualCoordinateEntered(x, y))
                        xText = ""
                        yText = ""
                        validationError = false
                    } else {
                        validationError = true
                    }
                },
                modifier =
                    Modifier
                        .heightIn(min = 48.dp)
                        .semantics { contentDescription = "Aplicar coordenada manual do destino" },
            ) {
                Text(fontSize = FontSize.SMALL, text = "Aplicar")
            }
        }
        if (validationError) {
            Text(
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
                fontSize = FontSize.SMALL,
                fontWeight = FontWeight.MEDIUM,
                text = "Informe valores de X e Y válidos (números inteiros, maiores ou iguais a zero).",
            )
        }
    }
}

@Composable
private fun BoxScope.AdminMapViewerPendingForm(
    state: AdminMapViewerState,
    onIntent: (AdminMapViewerIntent) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color.BACKGROUND_10)
                .padding(20.dp),
    ) {
        Text(
            modifier = Modifier.semantics { heading() },
            fontSize = FontSize.MEDIUM,
            fontWeight = FontWeight.SEMIBOLD,
            text = "Novo destino",
        )
        Spacer(modifier = Modifier.size(12.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            label = "Nome do destino",
            value = state.pointName,
            onValueChange = { onIntent(AdminMapViewerIntent.PointNameChanged(it)) },
        )
        Spacer(modifier = Modifier.size(10.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            label = "Descrição",
            value = state.pointDescription,
            onValueChange = { onIntent(AdminMapViewerIntent.PointDescriptionChanged(it)) },
        )
        state.error?.let { error ->
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Assertive },
                fontSize = FontSize.SMALL,
                fontWeight = FontWeight.MEDIUM,
                text = errorMessage(error),
            )
        }
        Spacer(modifier = Modifier.size(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = { onIntent(AdminMapViewerIntent.CancelPendingPoint) },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color.BACKGROUND_30,
                        contentColor = Color.BLACK_ABSOLUTE,
                    ),
                modifier =
                    Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .semantics { contentDescription = "Cancelar cadastro do destino" },
            ) {
                Text(fontSize = FontSize.SMALL, text = "Cancelar")
            }
            Button(
                onClick = { onIntent(AdminMapViewerIntent.ConfirmPoint) },
                enabled = !state.isSubmittingPoint,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color.ORANGE_20,
                        contentColor = Color.BACKGROUND_10,
                    ),
                modifier =
                    Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .semantics {
                            contentDescription =
                                if (state.isSubmittingPoint) "Cadastrando destino, aguarde" else "Confirmar destino"
                        },
            ) {
                Text(fontSize = FontSize.SMALL, text = if (state.isSubmittingPoint) "Cadastrando..." else "Confirmar")
            }
        }
    }
}

private fun errorMessage(error: CreatePointError): String =
    when (error) {
        CreatePointError.EmptyName -> "Informe o nome do destino."
        CreatePointError.InvalidCoordinate -> "Coordenada inválida para este destino."
        CreatePointError.NoActiveSession -> "Sessão administrativa expirada. Faça login novamente."
        CreatePointError.NetworkUnavailable -> "Sem conexão com a internet. Verifique sua rede e tente novamente."
        is CreatePointError.Unknown -> "Não foi possível cadastrar o destino agora. Tente novamente em instantes."
    }
