package br.edu.utfpr.pb.tcc_daviaugustolira.navigation

import androidx.navigation.NavController
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.INavigator
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.Screen

class NavigatorImpl(
    private val navController: NavController,
) : INavigator {
    override fun navigate(screen: Screen) {
        navController.navigate(screen)
    }

    override fun navigateBack() {
        navController.popBackStack()
    }
}
