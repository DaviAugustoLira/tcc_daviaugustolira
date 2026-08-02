package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap

/** Documento inválido (sem `name`) vira `Result.failure`, nunca um crash de mapeamento. */
fun MapDto.toDomain(id: String): Result<IndoorMap> {
    val name = name
    return if (name.isNullOrBlank()) {
        Result.failure(IllegalStateException("Documento maps/$id sem campo 'name' válido"))
    } else {
        Result.success(IndoorMap(id = id, name = name))
    }
}
