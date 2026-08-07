package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.Point
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObservePointsUseCaseTest {
    @Test
    fun `forwards the points emitted by the repository`() =
        runTest {
            val points =
                listOf(
                    Point(id = "point-1", mapId = "map-1", name = "Entrada", description = "", x = 10, y = 20),
                    Point(id = "point-2", mapId = "map-1", name = "Saída", description = "", x = 30, y = 40),
                )
            val repository = FakePointsRepository(initialPoints = points)
            val useCase = ObservePointsUseCase(repository)

            assertEquals(points, useCase(mapId = "map-1").first())
        }

    @Test
    fun `starts empty when the repository has no points yet`() =
        runTest {
            val useCase = ObservePointsUseCase(FakePointsRepository())

            assertEquals(emptyList(), useCase(mapId = "map-1").first())
        }
}
