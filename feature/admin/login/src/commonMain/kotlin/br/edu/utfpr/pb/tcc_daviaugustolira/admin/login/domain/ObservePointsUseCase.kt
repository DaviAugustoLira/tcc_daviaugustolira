package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.Point
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.PointsRepository
import kotlinx.coroutines.flow.Flow

class ObservePointsUseCase(
    private val repository: PointsRepository,
) {
    operator fun invoke(mapId: String): Flow<List<Point>> = repository.observePoints(mapId)
}
