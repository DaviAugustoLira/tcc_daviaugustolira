package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.CreateMapError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.CreateMapOutcome
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.MapsRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.NewMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Fake em memória — CLAUDE.md secao 8: nunca Firebase real em unit test. */
class FakeMapsRepository(
    initialMaps: List<IndoorMap> = emptyList(),
    private val createMapResult: (NewMap, String) -> CreateMapOutcome = { _, _ ->
        CreateMapOutcome.Failure(CreateMapError.Unknown(null))
    },
) : MapsRepository {
    val mapsFlow = MutableStateFlow(initialMaps)

    var createMapCallCount = 0
        private set
    var lastCreateMapArgs: Pair<NewMap, String>? = null
        private set

    override fun observeMaps(): Flow<List<IndoorMap>> = mapsFlow

    override suspend fun createMap(
        map: NewMap,
        createdByUid: String,
    ): CreateMapOutcome {
        createMapCallCount++
        lastCreateMapArgs = map to createdByUid
        return createMapResult(map, createdByUid)
    }
}
