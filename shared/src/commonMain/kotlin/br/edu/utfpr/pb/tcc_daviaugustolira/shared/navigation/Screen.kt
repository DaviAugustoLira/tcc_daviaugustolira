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
}
