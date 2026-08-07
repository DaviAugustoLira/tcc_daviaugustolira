package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/**
 * Converte um toque em coordenada de tela (`tapOffset`, relativa ao viewport, antes do
 * pan/zoom aplicados pelo visualizador) para o pixel correspondente na imagem original do
 * map, desfazendo o deslocamento (`panOffset`) e a escala combinada (`fitScale * zoom`).
 * Resultado sempre dentro dos limites da imagem — função pura, sem estado nem dependência de
 * plataforma (ver design.md do change admin-map-point-registration, Decisão 5).
 */
fun mapTapToImagePixel(
    tapOffset: Offset,
    panOffset: Offset,
    combinedScale: Float,
    intrinsicWidth: Int,
    intrinsicHeight: Int,
): IntOffset {
    if (combinedScale <= 0f || intrinsicWidth <= 0 || intrinsicHeight <= 0) return IntOffset.Zero

    val contentLocalX = (tapOffset.x - panOffset.x) / combinedScale
    val contentLocalY = (tapOffset.y - panOffset.y) / combinedScale

    val pixelX = contentLocalX.roundToInt().coerceIn(0, intrinsicWidth - 1)
    val pixelY = contentLocalY.roundToInt().coerceIn(0, intrinsicHeight - 1)
    return IntOffset(pixelX, pixelY)
}
