package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth

/**
 * Resultado de uma tentativa de login como valor (CLAUDE.md secao 7) — nunca uma
 * exceção crua escapando de `AdminSessionRepository.login` para quem chama.
 */
sealed interface LoginOutcome {
    data class Success(
        val session: AdminSession,
    ) : LoginOutcome

    data class Failure(
        val error: AdminAuthError,
    ) : LoginOutcome
}
