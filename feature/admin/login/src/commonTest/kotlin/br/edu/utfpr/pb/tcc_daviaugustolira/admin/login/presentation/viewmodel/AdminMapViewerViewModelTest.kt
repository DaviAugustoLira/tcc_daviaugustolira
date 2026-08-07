package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel

import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.CreatePointUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.FakeAdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.FakePointsRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObservePointsUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSession
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointOutcome
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class AdminMapViewerViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggling pick mode flips the state and clears any pending point`() =
        runTest {
            val viewModel = buildViewModel()

            viewModel.onIntent(AdminMapViewerIntent.TogglePickMode)
            assertEquals(true, viewModel.state.value.isPickModeActive)

            viewModel.onIntent(AdminMapViewerIntent.TapAt(10, 20))
            assertEquals(PendingPoint(10, 20), viewModel.state.value.pendingPoint)

            viewModel.onIntent(AdminMapViewerIntent.TogglePickMode)
            assertEquals(false, viewModel.state.value.isPickModeActive)
            assertNull(viewModel.state.value.pendingPoint)
        }

    @Test
    fun `tap captures a pending point`() =
        runTest {
            val viewModel = buildViewModel()

            viewModel.onIntent(AdminMapViewerIntent.TapAt(x = 42, y = 84))

            assertEquals(PendingPoint(42, 84), viewModel.state.value.pendingPoint)
        }

    @Test
    fun `moving the pending pin with a second tap keeps the name already typed`() =
        runTest {
            val viewModel = buildViewModel()

            viewModel.onIntent(AdminMapViewerIntent.TapAt(1, 2))
            viewModel.onIntent(AdminMapViewerIntent.PointNameChanged("Entrada"))
            viewModel.onIntent(AdminMapViewerIntent.TapAt(9, 9))

            assertEquals(PendingPoint(9, 9), viewModel.state.value.pendingPoint)
            assertEquals("Entrada", viewModel.state.value.pointName)
        }

    @Test
    fun `a fresh tap after no pending point starts with an empty name`() =
        runTest {
            val viewModel = buildViewModel()

            viewModel.onIntent(AdminMapViewerIntent.TapAt(1, 2))

            assertEquals("", viewModel.state.value.pointName)
        }

    @Test
    fun `manual coordinate entry captures a pending point the same way as a tap`() =
        runTest {
            val viewModel = buildViewModel()

            viewModel.onIntent(AdminMapViewerIntent.ManualCoordinateEntered(x = 5, y = 7))

            assertEquals(PendingPoint(5, 7), viewModel.state.value.pendingPoint)
        }

    @Test
    fun `confirming a pending point persists it and stays in pick mode`() =
        runTest {
            val pointsRepository = FakePointsRepository(createPointResult = { _, _ -> CreatePointOutcome.Success })
            val viewModel = buildViewModel(pointsRepository = pointsRepository)

            viewModel.onIntent(AdminMapViewerIntent.TogglePickMode)
            viewModel.onIntent(AdminMapViewerIntent.TapAt(1, 2))
            viewModel.onIntent(AdminMapViewerIntent.PointNameChanged("Entrada"))
            viewModel.onIntent(AdminMapViewerIntent.ConfirmPoint)

            assertEquals(1, pointsRepository.createPointCallCount)
            assertNull(viewModel.state.value.pendingPoint)
            assertEquals(true, viewModel.state.value.isPickModeActive)
            assertNull(viewModel.state.value.error)
        }

    @Test
    fun `cancelling a pending point clears it without persisting anything`() =
        runTest {
            val pointsRepository = FakePointsRepository()
            val viewModel = buildViewModel(pointsRepository = pointsRepository)

            viewModel.onIntent(AdminMapViewerIntent.TapAt(1, 2))
            viewModel.onIntent(AdminMapViewerIntent.CancelPendingPoint)

            assertNull(viewModel.state.value.pendingPoint)
            assertEquals(0, pointsRepository.createPointCallCount)
        }

    @Test
    fun `create failure surfaces as an error and keeps the pending point`() =
        runTest {
            val pointsRepository =
                FakePointsRepository(
                    createPointResult = { _, _ -> CreatePointOutcome.Failure(CreatePointError.EmptyName) },
                )
            val viewModel = buildViewModel(pointsRepository = pointsRepository)

            viewModel.onIntent(AdminMapViewerIntent.TapAt(1, 2))
            viewModel.onIntent(AdminMapViewerIntent.ConfirmPoint)

            assertEquals(CreatePointError.EmptyName, viewModel.state.value.error)
        }

    @Test
    fun `observes points from the repository for the given map`() =
        runTest {
            val points =
                listOf(Point(id = "point-1", mapId = "map-1", name = "Entrada", description = "", x = 1, y = 2))
            val viewModel = buildViewModel(pointsRepository = FakePointsRepository(initialPoints = points))

            assertEquals(points, viewModel.state.value.points)
        }

    private fun buildViewModel(
        mapId: String = "map-1",
        pointsRepository: FakePointsRepository = FakePointsRepository(),
        sessionRepository: FakeAdminSessionRepository =
            FakeAdminSessionRepository(
                initialSessionState =
                    AdminSessionState.Authenticated(AdminSession(uid = "uid-1", email = "admin@example.com")),
            ),
    ) = AdminMapViewerViewModel(
        mapId = mapId,
        name = "Bloco A",
        imageUrl = "https://example.com/bloco-a.png",
        createPointUseCase = CreatePointUseCase(pointsRepository, sessionRepository),
        observePoints = ObservePointsUseCase(pointsRepository),
    )
}
