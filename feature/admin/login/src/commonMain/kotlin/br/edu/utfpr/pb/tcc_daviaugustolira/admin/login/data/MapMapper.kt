package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap

/** Documento inválido (sem `name`/`svgUrl`) vira `Result.failure`, nunca um crash de mapeamento. */
fun MapDto.toDomain(id: String): Result<IndoorMap> {
    val name = name
    val imageUrl = svgUrl
    return when {
        name.isNullOrBlank() ->
            Result.failure(IllegalStateException("Documento maps/$id sem campo 'name' válido"))
        imageUrl.isNullOrBlank() ->
            Result.failure(IllegalStateException("Documento maps/$id sem campo 'svgUrl' válido"))
        else -> Result.success(IndoorMap(id = id, name = name, imageUrl = imageUrl))
    }
}
