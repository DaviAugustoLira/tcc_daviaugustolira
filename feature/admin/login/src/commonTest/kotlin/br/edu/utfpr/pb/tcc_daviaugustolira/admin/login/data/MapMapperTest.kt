package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MapMapperTest {
    @Test
    fun `maps a valid dto to the domain entity`() {
        val outcome = MapDto(name = "Bloco A", svgUrl = "https://example.com/bloco-a.png").toDomain(id = "map-1")

        assertEquals(
            Result.success(IndoorMap(id = "map-1", name = "Bloco A", imageUrl = "https://example.com/bloco-a.png")),
            outcome,
        )
    }

    @Test
    fun `fails when name is missing`() {
        val outcome = MapDto(name = null, svgUrl = "https://example.com/bloco-a.png").toDomain(id = "map-1")

        assertTrue(outcome.isFailure)
    }

    @Test
    fun `fails when name is blank`() {
        val outcome = MapDto(name = "   ", svgUrl = "https://example.com/bloco-a.png").toDomain(id = "map-1")

        assertTrue(outcome.isFailure)
    }

    @Test
    fun `fails when svgUrl is missing`() {
        val outcome = MapDto(name = "Bloco A", svgUrl = null).toDomain(id = "map-1")

        assertTrue(outcome.isFailure)
    }

    @Test
    fun `fails when svgUrl is blank`() {
        val outcome = MapDto(name = "Bloco A", svgUrl = "   ").toDomain(id = "map-1")

        assertTrue(outcome.isFailure)
    }
}
