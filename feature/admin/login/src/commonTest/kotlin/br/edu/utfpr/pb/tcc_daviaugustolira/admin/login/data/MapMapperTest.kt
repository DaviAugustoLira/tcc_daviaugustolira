package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MapMapperTest {
    @Test
    fun `maps a valid dto to the domain entity`() {
        val outcome = MapDto(name = "Bloco A").toDomain(id = "map-1")

        assertEquals(Result.success(IndoorMap(id = "map-1", name = "Bloco A")), outcome)
    }

    @Test
    fun `fails when name is missing`() {
        val outcome = MapDto(name = null).toDomain(id = "map-1")

        assertTrue(outcome.isFailure)
    }

    @Test
    fun `fails when name is blank`() {
        val outcome = MapDto(name = "   ").toDomain(id = "map-1")

        assertTrue(outcome.isFailure)
    }
}
