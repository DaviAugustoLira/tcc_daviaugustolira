package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSession
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveSessionUseCaseTest {
    @Test
    fun `starts as loading before the repository reports a session`() =
        runTest {
            val repository = FakeAdminSessionRepository(initialSessionState = AdminSessionState.Loading)
            val useCase = ObserveSessionUseCase(repository)

            assertEquals(AdminSessionState.Loading, useCase().first())
        }

    @Test
    fun `forwards authenticated state from the repository`() =
        runTest {
            val session = AdminSession(uid = "uid-1", email = "admin@example.com")
            val repository =
                FakeAdminSessionRepository(initialSessionState = AdminSessionState.Authenticated(session))
            val useCase = ObserveSessionUseCase(repository)

            assertEquals(AdminSessionState.Authenticated(session), useCase().first())
        }

    @Test
    fun `forwards unauthenticated state from the repository`() =
        runTest {
            val repository = FakeAdminSessionRepository(initialSessionState = AdminSessionState.Unauthenticated)
            val useCase = ObserveSessionUseCase(repository)

            assertEquals(AdminSessionState.Unauthenticated, useCase().first())
        }
}
