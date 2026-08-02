package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.MapsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Fake em memória — CLAUDE.md secao 8: nunca Firebase real em unit test. */
class FakeMapsRepository(
    initialMaps: List<IndoorMap> = emptyList(),
) : MapsRepository {
    val mapsFlow = MutableStateFlow(initialMaps)

    override fun observeMaps(): Flow<List<IndoorMap>> = mapsFlow
}
