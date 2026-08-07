package br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Search : Screen()

    @Serializable
    data object Login : Screen()

    @Serializable
    data object BeaconDebug : Screen()

    @Serializable
    data object AdminHome : Screen()

    @Serializable
    data object AdminCreateMap : Screen()

    @Serializable
    data class AdminMapViewer(
        val mapId: String,
        val name: String,
        val imageUrl: String,
    ) : Screen()
}
