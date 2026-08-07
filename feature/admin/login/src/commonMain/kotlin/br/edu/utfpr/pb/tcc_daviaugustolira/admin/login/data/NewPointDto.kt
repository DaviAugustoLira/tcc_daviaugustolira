package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

/**
 * Documento escrito em `points/{autoId}` ao cadastrar um destino novo dentro de um map (ver
 * `docs/firestore-schema.md`). `connections`/`fingerprint` gravam vazio até o cadastro de
 * grafo de navegação e a calibração existirem como fluxos próprios — nunca inventados aqui.
 */
@Serializable
data class NewPointDto(
    val mapId: String,
    val name: String,
    val description: String,
    val x: Long,
    val y: Long,
    val createdBy: String,
    val type: String? = null,
    val instruction: String? = null,
    val isAccessible: Boolean? = null,
    val connections: List<PointConnectionDto> = emptyList(),
    val fingerprint: List<PointFingerprintEntryDto> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: BaseTimestamp = Timestamp.ServerTimestamp,
    val updatedAt: BaseTimestamp = Timestamp.ServerTimestamp,
)
