package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSession
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LogoutUseCaseTest {
    @Test
    fun `delegates to the repository and ends up unauthenticated`() =
        runTest {
            val repository =
                FakeAdminSessionRepository(
                    initialSessionState =
                        AdminSessionState.Authenticated(AdminSession("uid-1", "admin@example.com")),
                )
            val useCase = LogoutUseCase(repository)

            useCase()

            assertEquals(1, repository.logoutCallCount)
            assertEquals(AdminSessionState.Unauthenticated, repository.sessionFlow.value)
        }
}
