package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import kotlinx.serialization.Serializable

/** Espelho do documento `maps/{id}` — campos anuláveis, o doc pode vir incompleto. */
@Serializable
data class MapDto(
    val name: String? = null,
)
