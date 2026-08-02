package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth

import kotlinx.coroutines.flow.Flow

/**
 * Porta de sessão administrativa. Vive em `shared/domain` (não em uma feature) porque
 * é consumida por 2+ consumidores: quem escreve a sessão (feature/admin/login) e quem
 * só lê para guardar rota (:navigation/AdminRouteGuard) — ver CLAUDE.md seção 1.
 */
interface AdminSessionRepository {
    fun observeSession(): Flow<AdminSessionState>

    suspend fun login(
        email: String,
        password: String,
    ): LoginOutcome

    suspend fun logout()
}
