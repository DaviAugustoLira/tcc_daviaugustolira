package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.Point

/** Documento inválido (sem `mapId`/`name`/`x`/`y`) vira `Result.failure`, nunca um crash de mapeamento. */
fun PointDto.toDomain(id: String): Result<Point> {
    val mapId = mapId
    val name = name
    val x = x
    val y = y
    return when {
        mapId.isNullOrBlank() ->
            Result.failure(IllegalStateException("Documento points/$id sem campo 'mapId' válido"))
        name.isNullOrBlank() ->
            Result.failure(IllegalStateException("Documento points/$id sem campo 'name' válido"))
        x == null ->
            Result.failure(IllegalStateException("Documento points/$id sem campo 'x' válido"))
        y == null ->
            Result.failure(IllegalStateException("Documento points/$id sem campo 'y' válido"))
        else ->
            Result.success(
                Point(
                    id = id,
                    mapId = mapId,
                    name = name,
                    description = description.orEmpty(),
                    x = x.toInt(),
                    y = y.toInt(),
                ),
            )
    }
}
