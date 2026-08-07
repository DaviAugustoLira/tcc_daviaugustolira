package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.Point
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PointMapperTest {
    @Test
    fun `maps a valid dto to the domain entity`() {
        val outcome =
            PointDto(mapId = "map-1", name = "Entrada principal", description = "Portão A", x = 120L, y = 340L)
                .toDomain(id = "point-1")

        assertEquals(
            Result.success(
                Point(id = "point-1", mapId = "map-1", name = "Entrada principal", description = "Portão A", x = 120, y = 340),
            ),
            outcome,
        )
    }

    @Test
    fun `defaults description to empty when missing`() {
        val outcome = PointDto(mapId = "map-1", name = "Entrada", description = null, x = 0L, y = 0L).toDomain(id = "point-1")

        assertEquals("", outcome.getOrNull()?.description)
    }

    @Test
    fun `fails when mapId is missing`() {
        val outcome = PointDto(mapId = null, name = "Entrada", x = 0L, y = 0L).toDomain(id = "point-1")

        assertTrue(outcome.isFailure)
    }

    @Test
    fun `fails when mapId is blank`() {
        val outcome = PointDto(mapId = "   ", name = "Entrada", x = 0L, y = 0L).toDomain(id = "point-1")

        assertTrue(outcome.isFailure)
    }

    @Test
    fun `fails when name is missing`() {
        val outcome = PointDto(mapId = "map-1", name = null, x = 0L, y = 0L).toDomain(id = "point-1")

        assertTrue(outcome.isFailure)
    }

    @Test
    fun `fails when name is blank`() {
        val outcome = PointDto(mapId = "map-1", name = "   ", x = 0L, y = 0L).toDomain(id = "point-1")

        assertTrue(outcome.isFailure)
    }

    @Test
    fun `fails when x is missing`() {
        val outcome = PointDto(mapId = "map-1", name = "Entrada", x = null, y = 0L).toDomain(id = "point-1")

        assertTrue(outcome.isFailure)
    }

    @Test
    fun `fails when y is missing`() {
        val outcome = PointDto(mapId = "map-1", name = "Entrada", x = 0L, y = null).toDomain(id = "point-1")

        assertTrue(outcome.isFailure)
    }
}
