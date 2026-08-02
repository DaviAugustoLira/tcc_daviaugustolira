package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel

import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.FakeAdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.LoginUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminAuthError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSession
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.LoginOutcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `email and password intents update state and clear a previous error`() {
        val viewModel = LoginViewModel(LoginUseCase(FakeAdminSessionRepository()))

        viewModel.onIntent(LoginIntent.EmailChanged("admin@example.com"))
        viewModel.onIntent(LoginIntent.PasswordChanged("secret"))

        assertEquals("admin@example.com", viewModel.state.value.email)
        assertEquals("secret", viewModel.state.value.password)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `submit with empty fields sets a validation error and does not call the repository`() =
        runTest {
            val repository = FakeAdminSessionRepository()
            val viewModel = LoginViewModel(LoginUseCase(repository))

            viewModel.onIntent(LoginIntent.Submit)

            assertEquals(AdminAuthError.EmptyFields, viewModel.state.value.error)
            assertEquals(0, repository.loginCallCount)
            assertTrue(!viewModel.state.value.isSubmitting)
        }

    @Test
    fun `submit success clears submitting state and emits a navigate effect`() =
        runTest {
            val session = AdminSession(uid = "uid-1", email = "admin@example.com")
            val repository =
                FakeAdminSessionRepository(loginResult = { _, _ -> LoginOutcome.Success(session) })
            val viewModel = LoginViewModel(LoginUseCase(repository))
            viewModel.onIntent(LoginIntent.EmailChanged("admin@example.com"))
            viewModel.onIntent(LoginIntent.PasswordChanged("secret"))

            val effectDeferred = async { viewModel.effects.first() }
            runCurrent() // garante que o coletor já assinou o SharedFlow antes do emit síncrono abaixo
            viewModel.onIntent(LoginIntent.Submit)

            assertEquals(LoginEffect.NavigateToAdminHome, effectDeferred.await())
            assertNull(viewModel.state.value.error)
            assertTrue(!viewModel.state.value.isSubmitting)
        }

    @Test
    fun `submit failure surfaces the mapped error and does not emit a navigate effect`() =
        runTest {
            val repository =
                FakeAdminSessionRepository(
                    loginResult = { _, _ -> LoginOutcome.Failure(AdminAuthError.InvalidCredentials) },
                )
            val viewModel = LoginViewModel(LoginUseCase(repository))
            viewModel.onIntent(LoginIntent.EmailChanged("admin@example.com"))
            viewModel.onIntent(LoginIntent.PasswordChanged("wrong"))

            viewModel.onIntent(LoginIntent.Submit)

            assertEquals(AdminAuthError.InvalidCredentials, viewModel.state.value.error)
            assertTrue(!viewModel.state.value.isSubmitting)
        }
}
