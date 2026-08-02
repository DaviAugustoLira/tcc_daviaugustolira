package br.edu.utfpr.pb.tcc_daviaugustolira.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObserveSessionUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.INavigator
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.Screen
import org.koin.compose.koinInject

/**
 * Guarda de rota reativa (não um checkpoint único): observa a sessão continuamente
 * e redireciona ao login tanto para quem chega sem sessão quanto para quem já está
 * em uma rota admin quando a sessão expira/é revogada. Zero lógica de negócio além
 * da observação e do redirecionamento — a decisão de login/logout em si vive no
 * domain de feature/admin/login (ver CLAUDE.md secao 1 e design.md do change
 * admin-auth-shell).
 */
@Composable
fun AdminRouteGuard(
    navigator: INavigator,
    content: @Composable () -> Unit,
) {
    val observeSession: ObserveSessionUseCase = koinInject()
    val sessionState by observeSession().collectAsStateWithLifecycle(initialValue = AdminSessionState.Loading)

    LaunchedEffect(sessionState) {
        if (sessionState is AdminSessionState.Unauthenticated) {
            navigator.navigateClearingBackStack(Screen.Login)
        }
    }

    if (sessionState is AdminSessionState.Authenticated) {
        content()
    }
}
