package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class MapPixelMapperTest {
    @Test
    fun `maps a tap at the origin to pixel zero with no pan and scale one`() {
        val pixel =
            mapTapToImagePixel(
                tapOffset = Offset.Zero,
                panOffset = Offset.Zero,
                combinedScale = 1f,
                intrinsicWidth = 1000,
                intrinsicHeight = 2000,
            )

        assertEquals(IntOffset(0, 0), pixel)
    }

    @Test
    fun `the same visual point maps to the same pixel regardless of the zoom level`() {
        // Toque no mesmo local visual (tela em 300,600) em dois zooms diferentes, com o pan
        // ajustado para manter esse ponto centralizado (como faria o gesto de pinça real).
        val fitScale = 0.5f
        val intrinsicWidth = 1000
        val intrinsicHeight = 2000

        val zoom1 = 1f
        val pan1 = Offset(x = 50f, y = 100f)
        val pixelAtZoom1 =
            mapTapToImagePixel(
                tapOffset = Offset(300f, 600f),
                panOffset = pan1,
                combinedScale = fitScale * zoom1,
                intrinsicWidth = intrinsicWidth,
                intrinsicHeight = intrinsicHeight,
            )

        val zoom2 = 3f
        // pan recalculado para que o mesmo pixel da imagem apareça na mesma posição de tela
        val expectedPixel = pixelAtZoom1
        val combinedScale2 = fitScale * zoom2
        val pan2 =
            Offset(
                x = 300f - expectedPixel.x * combinedScale2,
                y = 600f - expectedPixel.y * combinedScale2,
            )
        val pixelAtZoom2 =
            mapTapToImagePixel(
                tapOffset = Offset(300f, 600f),
                panOffset = pan2,
                combinedScale = combinedScale2,
                intrinsicWidth = intrinsicWidth,
                intrinsicHeight = intrinsicHeight,
            )

        assertEquals(pixelAtZoom1, pixelAtZoom2)
    }

    @Test
    fun `clamps the result to the image bounds`() {
        val pixel =
            mapTapToImagePixel(
                tapOffset = Offset(10_000f, 10_000f),
                panOffset = Offset.Zero,
                combinedScale = 1f,
                intrinsicWidth = 100,
                intrinsicHeight = 200,
            )

        assertEquals(IntOffset(99, 199), pixel)
    }

    @Test
    fun `returns zero when the image size or scale is not known yet`() {
        val pixel =
            mapTapToImagePixel(
                tapOffset = Offset(50f, 50f),
                panOffset = Offset.Zero,
                combinedScale = 0f,
                intrinsicWidth = 0,
                intrinsicHeight = 0,
            )

        assertEquals(IntOffset.Zero, pixel)
    }
}
