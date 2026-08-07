package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.CreateMapError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.CreateMapOutcome
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.MapsRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.NewMap
import kotlinx.coroutines.flow.first

class CreateMapUseCase(
    private val mapsRepository: MapsRepository,
    private val sessionRepository: AdminSessionRepository,
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        svgUrl: String,
        scale: Double?,
        floor: Int?,
    ): CreateMapOutcome {
        if (name.isBlank()) return CreateMapOutcome.Failure(CreateMapError.EmptyName)
        if (svgUrl.isBlank()) return CreateMapOutcome.Failure(CreateMapError.EmptySvgUrl)
        if (scale == null) return CreateMapOutcome.Failure(CreateMapError.InvalidScale)
        if (floor == null) return CreateMapOutcome.Failure(CreateMapError.InvalidFloor)

        val sessionState = sessionRepository.observeSession().first()
        val session =
            (sessionState as? AdminSessionState.Authenticated)?.session
                ?: return CreateMapOutcome.Failure(CreateMapError.NoActiveSession)

        return mapsRepository.createMap(
            map =
                NewMap(
                    name = name,
                    description = description,
                    svgUrl = svgUrl,
                    scale = scale,
                    floor = floor,
                ),
            createdByUid = session.uid,
        )
    }
}
