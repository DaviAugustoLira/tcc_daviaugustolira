package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel

import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.FakeAdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.FakeMapsRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.LogoutUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObserveMapsUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObserveSessionUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSession
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.CreateMapError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.CreateMapOutcome
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.MapsRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.NewMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AdminShellViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `exposes the authenticated admin email and the maps from the repositories`() =
        runTest {
            val session = AdminSession(uid = "uid-1", email = "admin@example.com")
            val sessionRepository =
                FakeAdminSessionRepository(initialSessionState = AdminSessionState.Authenticated(session))
            val maps = listOf(IndoorMap(id = "map-1", name = "Bloco A", imageUrl = "https://example.com/bloco-a.png"))
            val mapsRepository = FakeMapsRepository(initialMaps = maps)

            val viewModel = buildViewModel(sessionRepository, mapsRepository)

            assertEquals("admin@example.com", viewModel.state.value.adminEmail)
            assertEquals(maps, viewModel.state.value.maps)
            assertEquals(false, viewModel.state.value.isLoadingMaps)
            assertNull(viewModel.state.value.mapsError)
        }

    @Test
    fun `maps error from the repository surfaces as a mapsError instead of crashing`() =
        runTest {
            val sessionRepository = FakeAdminSessionRepository()
            val failingMapsRepository =
                object : MapsRepository {
                    override fun observeMaps(): Flow<List<IndoorMap>> = flow { throw IllegalStateException("boom") }

                    override suspend fun createMap(
                        map: NewMap,
                        createdByUid: String,
                    ): CreateMapOutcome = CreateMapOutcome.Failure(CreateMapError.Unknown(null))
                }

            val viewModel =
                AdminShellViewModel(
                    logoutUseCase = LogoutUseCase(sessionRepository),
                    observeSession = ObserveSessionUseCase(sessionRepository),
                    observeMaps = ObserveMapsUseCase(failingMapsRepository),
                )

            assertEquals("boom", viewModel.state.value.mapsError)
            assertEquals(false, viewModel.state.value.isLoadingMaps)
        }

    @Test
    fun `logout intent delegates to the session repository`() =
        runTest {
            val session = AdminSession(uid = "uid-1", email = "admin@example.com")
            val sessionRepository =
                FakeAdminSessionRepository(initialSessionState = AdminSessionState.Authenticated(session))
            val viewModel = buildViewModel(sessionRepository, FakeMapsRepository())

            viewModel.onIntent(AdminShellIntent.Logout)

            assertEquals(1, sessionRepository.logoutCallCount)
        }

    private fun buildViewModel(
        sessionRepository: FakeAdminSessionRepository,
        mapsRepository: FakeMapsRepository,
    ) = AdminShellViewModel(
        logoutUseCase = LogoutUseCase(sessionRepository),
        observeSession = ObserveSessionUseCase(sessionRepository),
        observeMaps = ObserveMapsUseCase(mapsRepository),
    )
}
