package br.edu.utfpr.pb.tcc_daviaugustolira.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.screen.AdminMapViewerScreen
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminMapViewerState
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.PendingPoint
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.Point

private const val PREVIEW_IMAGE_URL = "https://picsum.photos/seed/map1/1200/2000"

@Composable
@Preview(showBackground = true)
fun AdminMapViewerPreview() {
    AdminMapViewerScreen(
        state =
            AdminMapViewerState(
                mapId = "map-1",
                name = "Bloco A",
                imageUrl = PREVIEW_IMAGE_URL,
                points =
                    listOf(
                        Point(id = "point-1", mapId = "map-1", name = "Entrada principal", description = "", x = 300, y = 500),
                        Point(id = "point-2", mapId = "map-1", name = "Elevador", description = "", x = 700, y = 1200),
                    ),
            ),
    )
}

@Composable
@Preview(showBackground = true, name = "Modo de cadastro com destino pendente")
fun AdminMapViewerPickModePreview() {
    AdminMapViewerScreen(
        state =
            AdminMapViewerState(
                mapId = "map-1",
                name = "Bloco A",
                imageUrl = PREVIEW_IMAGE_URL,
                points =
                    listOf(
                        Point(id = "point-1", mapId = "map-1", name = "Entrada principal", description = "", x = 300, y = 500),
                    ),
                isPickModeActive = true,
                pendingPoint = PendingPoint(x = 900, y = 1500),
                pointName = "Sala 12",
            ),
    )
}
