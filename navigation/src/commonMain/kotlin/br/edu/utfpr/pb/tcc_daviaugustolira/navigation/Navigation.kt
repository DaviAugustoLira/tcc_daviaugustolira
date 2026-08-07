package br.edu.utfpr.pb.tcc_daviaugustolira.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.screen.AdminMapViewerScreenRoute
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.screen.AdminShellScreenRoute
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.screen.CreateMapScreenRoute
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.screen.LoginScreenRoute
import br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.screen.BeaconDebugScreenRoute
import br.edu.utfpr.pb.tcc_daviaugustolira.home.presentation.screen.HomeScreenRoute
import br.edu.utfpr.pb.tcc_daviaugustolira.search.presentation.screen.SearchScreenRoute
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.Screen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navigator = remember { NavigatorImpl(navController) }

    NavHost(
        navController = navController,
        startDestination = Screen.Home,
    ) {
        composable<Screen.Home> {
            HomeScreenRoute(
                navigator,
            )
        }

        composable<Screen.Search> {
            SearchScreenRoute(
                navigator,
            )
        }

        composable<Screen.Login> {
            LoginScreenRoute(
                navigator,
            )
        }

        composable<Screen.BeaconDebug> {
            BeaconDebugScreenRoute(
                navigator,
            )
        }

        composable<Screen.AdminHome> {
            AdminRouteGuard(navigator) {
                AdminShellScreenRoute(
                    navigator,
                )
            }
        }

        composable<Screen.AdminCreateMap> {
            AdminRouteGuard(navigator) {
                CreateMapScreenRoute(
                    navigator,
                )
            }
        }

        composable<Screen.AdminMapViewer> { backStackEntry ->
            AdminRouteGuard(navigator) {
                AdminMapViewerScreenRoute(
                    navigator = navigator,
                    screen = backStackEntry.toRoute(),
                )
            }
        }
    }
}
