package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSession
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointOutcome
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.NewPoint
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreatePointUseCaseTest {
    @Test
    fun `empty name fails validation without calling the repository`() =
        runTest {
            val sessionRepository = authenticatedSessionRepository()
            val pointsRepository = FakePointsRepository()
            val useCase = CreatePointUseCase(pointsRepository, sessionRepository)

            val outcome = useCase(mapId = "map-1", name = "  ", description = "", x = 10, y = 20)

            assertEquals(CreatePointOutcome.Failure(CreatePointError.EmptyName), outcome)
            assertEquals(0, pointsRepository.createPointCallCount)
        }

    @Test
    fun `negative coordinate fails validation without calling the repository`() =
        runTest {
            val sessionRepository = authenticatedSessionRepository()
            val pointsRepository = FakePointsRepository()
            val useCase = CreatePointUseCase(pointsRepository, sessionRepository)

            val outcome = useCase(mapId = "map-1", name = "Entrada", description = "", x = -1, y = 20)

            assertEquals(CreatePointOutcome.Failure(CreatePointError.InvalidCoordinate), outcome)
            assertEquals(0, pointsRepository.createPointCallCount)
        }

    @Test
    fun `no active session fails without calling the repository`() =
        runTest {
            val sessionRepository = FakeAdminSessionRepository(initialSessionState = AdminSessionState.Unauthenticated)
            val pointsRepository = FakePointsRepository()
            val useCase = CreatePointUseCase(pointsRepository, sessionRepository)

            val outcome = useCase(mapId = "map-1", name = "Entrada", description = "", x = 10, y = 20)

            assertEquals(CreatePointOutcome.Failure(CreatePointError.NoActiveSession), outcome)
            assertEquals(0, pointsRepository.createPointCallCount)
        }

    @Test
    fun `delegates to the repository with the authenticated uid when data is valid`() =
        runTest {
            val sessionRepository = authenticatedSessionRepository(uid = "uid-1")
            val pointsRepository =
                FakePointsRepository(createPointResult = { _, _ -> CreatePointOutcome.Success })
            val useCase = CreatePointUseCase(pointsRepository, sessionRepository)

            val outcome = useCase(mapId = "map-1", name = "Entrada", description = "Portão A", x = 10, y = 20)

            assertEquals(CreatePointOutcome.Success, outcome)
            assertEquals(1, pointsRepository.createPointCallCount)
            assertEquals(
                NewPoint(mapId = "map-1", name = "Entrada", description = "Portão A", x = 10, y = 20) to "uid-1",
                pointsRepository.lastCreatePointArgs,
            )
        }

    @Test
    fun `propagates repository failure for network unavailable`() =
        runTest {
            val sessionRepository = authenticatedSessionRepository()
            val pointsRepository =
                FakePointsRepository(createPointResult = { _, _ -> CreatePointOutcome.Failure(CreatePointError.NetworkUnavailable) })
            val useCase = CreatePointUseCase(pointsRepository, sessionRepository)

            val outcome = useCase(mapId = "map-1", name = "Entrada", description = "", x = 10, y = 20)

            assertEquals(CreatePointOutcome.Failure(CreatePointError.NetworkUnavailable), outcome)
        }

    private fun authenticatedSessionRepository(uid: String = "uid-1") =
        FakeAdminSessionRepository(
            initialSessionState = AdminSessionState.Authenticated(AdminSession(uid = uid, email = "admin@example.com")),
        )
}
