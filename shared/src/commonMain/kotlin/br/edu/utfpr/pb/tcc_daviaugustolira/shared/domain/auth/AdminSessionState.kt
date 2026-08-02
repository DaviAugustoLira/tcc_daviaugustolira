package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth

/**
 * Estado da sessão administrativa. `Loading` existe para distinguir "ainda não sei"
 * (Firebase Auth ainda não restaurou a sessão persistida) de `Unauthenticated`
 * ("sei que não há sessão") — só o segundo deve disparar redirecionamento ao login.
 */
sealed interface AdminSessionState {
    data object Loading : AdminSessionState

    data class Authenticated(
        val session: AdminSession,
    ) : AdminSessionState

    data object Unauthenticated : AdminSessionState
}
