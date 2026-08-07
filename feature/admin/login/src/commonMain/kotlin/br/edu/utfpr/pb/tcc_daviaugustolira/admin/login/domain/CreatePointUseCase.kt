package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointOutcome
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.NewPoint
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.PointsRepository
import kotlinx.coroutines.flow.first

class CreatePointUseCase(
    private val pointsRepository: PointsRepository,
    private val sessionRepository: AdminSessionRepository,
) {
    suspend operator fun invoke(
        mapId: String,
        name: String,
        description: String,
        x: Int,
        y: Int,
    ): CreatePointOutcome {
        if (name.isBlank()) return CreatePointOutcome.Failure(CreatePointError.EmptyName)
        if (x < 0 || y < 0) return CreatePointOutcome.Failure(CreatePointError.InvalidCoordinate)

        val sessionState = sessionRepository.observeSession().first()
        val session =
            (sessionState as? AdminSessionState.Authenticated)?.session
                ?: return CreatePointOutcome.Failure(CreatePointError.NoActiveSession)

        return pointsRepository.createPoint(
            point =
                NewPoint(
                    mapId = mapId,
                    name = name,
                    description = description,
                    x = x,
                    y = y,
                ),
            createdByUid = session.uid,
        )
    }
}
