package br.edu.utfpr.pb.tcc_daviaugustolira.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.screen.AdminShellScreen
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminShellState
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap

@Composable
@Preview(showBackground = true)
fun AdminShellPreview() {
    AdminShellScreen(
        state =
            AdminShellState(
                adminEmail = "admin@example.com",
                maps =
                    listOf(
                        IndoorMap(id = "map-1", name = "Bloco A", imageUrl = "https://picsum.photos/seed/map1/400"),
                        IndoorMap(id = "map-2", name = "Bloco B", imageUrl = "https://picsum.photos/seed/map2/400"),
                    ),
                isLoadingMaps = false,
            ),
    )
}
