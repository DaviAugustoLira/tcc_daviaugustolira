package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points

import kotlinx.coroutines.flow.Flow

interface PointsRepository {
    fun observePoints(mapId: String): Flow<List<Point>>

    suspend fun createPoint(
        point: NewPoint,
        createdByUid: String,
    ): CreatePointOutcome
}
