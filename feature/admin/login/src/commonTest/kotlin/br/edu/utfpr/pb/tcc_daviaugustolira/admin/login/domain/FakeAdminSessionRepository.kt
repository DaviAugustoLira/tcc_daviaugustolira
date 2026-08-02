package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminAuthError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.LoginOutcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Fake em memória — CLAUDE.md secao 8: nunca Firebase real em unit test. */
class FakeAdminSessionRepository(
    initialSessionState: AdminSessionState = AdminSessionState.Loading,
    private val loginResult: (String, String) -> LoginOutcome = { _, _ ->
        LoginOutcome.Failure(AdminAuthError.Unknown(null))
    },
) : AdminSessionRepository {
    val sessionFlow = MutableStateFlow(initialSessionState)

    var loginCallCount = 0
        private set
    var logoutCallCount = 0
        private set

    override fun observeSession(): Flow<AdminSessionState> = sessionFlow

    override suspend fun login(
        email: String,
        password: String,
    ): LoginOutcome {
        loginCallCount++
        return loginResult(email, password)
    }

    override suspend fun logout() {
        logoutCallCount++
        sessionFlow.value = AdminSessionState.Unauthenticated
    }
}
