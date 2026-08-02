package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveMapsUseCaseTest {
    @Test
    fun `forwards the maps emitted by the repository`() =
        runTest {
            val maps = listOf(IndoorMap(id = "map-1", name = "Bloco A"), IndoorMap(id = "map-2", name = "Bloco B"))
            val repository = FakeMapsRepository(initialMaps = maps)
            val useCase = ObserveMapsUseCase(repository)

            assertEquals(maps, useCase().first())
        }

    @Test
    fun `starts empty when the repository has no maps yet`() =
        runTest {
            val useCase = ObserveMapsUseCase(FakeMapsRepository())

            assertEquals(emptyList(), useCase().first())
        }
}
