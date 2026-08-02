package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminAuthError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSession
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.LoginOutcome
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class LoginUseCaseTest {
    @Test
    fun `empty email fails validation without calling the repository`() =
        runTest {
            val repository = FakeAdminSessionRepository()
            val useCase = LoginUseCase(repository)

            val outcome = useCase(email = "", password = "secret")

            assertEquals(LoginOutcome.Failure(AdminAuthError.EmptyFields), outcome)
            assertEquals(0, repository.loginCallCount)
        }

    @Test
    fun `empty password fails validation without calling the repository`() =
        runTest {
            val repository = FakeAdminSessionRepository()
            val useCase = LoginUseCase(repository)

            val outcome = useCase(email = "admin@example.com", password = "")

            assertEquals(LoginOutcome.Failure(AdminAuthError.EmptyFields), outcome)
            assertEquals(0, repository.loginCallCount)
        }

    @Test
    fun `delegates to the repository and returns success when credentials are valid`() =
        runTest {
            val session = AdminSession(uid = "uid-1", email = "admin@example.com")
            val repository =
                FakeAdminSessionRepository(
                    loginResult = { _, _ -> LoginOutcome.Success(session) },
                )
            val useCase = LoginUseCase(repository)

            val outcome = useCase(email = "admin@example.com", password = "secret")

            assertEquals(LoginOutcome.Success(session), outcome)
            assertEquals(1, repository.loginCallCount)
        }

    @Test
    fun `propagates repository failure for invalid credentials`() =
        runTest {
            val repository =
                FakeAdminSessionRepository(
                    loginResult = { _, _ -> LoginOutcome.Failure(AdminAuthError.InvalidCredentials) },
                )
            val useCase = LoginUseCase(repository)

            val outcome = useCase(email = "admin@example.com", password = "wrong")

            assertIs<LoginOutcome.Failure>(outcome)
            assertEquals(AdminAuthError.InvalidCredentials, outcome.error)
            assertEquals(1, repository.loginCallCount)
        }

    @Test
    fun `propagates repository failure for network unavailable`() =
        runTest {
            val repository =
                FakeAdminSessionRepository(
                    loginResult = { _, _ -> LoginOutcome.Failure(AdminAuthError.NetworkUnavailable) },
                )
            val useCase = LoginUseCase(repository)

            val outcome = useCase(email = "admin@example.com", password = "secret")

            assertEquals(LoginOutcome.Failure(AdminAuthError.NetworkUnavailable), outcome)
        }
}
