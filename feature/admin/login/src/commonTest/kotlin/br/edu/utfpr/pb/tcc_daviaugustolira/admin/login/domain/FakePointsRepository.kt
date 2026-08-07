package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointOutcome
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.NewPoint
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.Point
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.PointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Fake em memória — CLAUDE.md secao 8: nunca Firebase real em unit test. */
class FakePointsRepository(
    initialPoints: List<Point> = emptyList(),
    private val createPointResult: (NewPoint, String) -> CreatePointOutcome = { _, _ ->
        CreatePointOutcome.Failure(CreatePointError.Unknown(null))
    },
) : PointsRepository {
    val pointsFlow = MutableStateFlow(initialPoints)

    var createPointCallCount = 0
        private set
    var lastCreatePointArgs: Pair<NewPoint, String>? = null
        private set

    override fun observePoints(mapId: String): Flow<List<Point>> = pointsFlow

    override suspend fun createPoint(
        point: NewPoint,
        createdByUid: String,
    ): CreatePointOutcome {
        createPointCallCount++
        lastCreatePointArgs = point to createdByUid
        return createPointResult(point, createdByUid)
    }
}
