package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

/**
 * Documento escrito em `maps/{autoId}` ao cadastrar um mapa novo (ver `docs/firestore-schema.md`).
 * `imageWidthPx`/`imageHeightPx`/`sector` são obrigatórios no schema alvo mas opcionais aqui
 * porque `CreateMapUseCase`/`CreateMapScreen` ainda não coletam esse dado — grava `null` até
 * esse fluxo ser estendido, em vez de inventar um valor falso (ex.: `0` de largura).
 */
@Serializable
data class NewMapDto(
    val name: String,
    val description: String,
    val svgUrl: String,
    val scale: Double,
    val floor: Int,
    val createdBy: String,
    val imageWidthPx: Long? = null,
    val imageHeightPx: Long? = null,
    val sector: String? = null,
    val isActive: Boolean = true,
    val createdAt: BaseTimestamp = Timestamp.ServerTimestamp,
    val updatedAt: BaseTimestamp = Timestamp.ServerTimestamp,
)
