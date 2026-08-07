package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import kotlinx.serialization.Serializable

/**
 * Espelho do documento `maps/{id}` (ver `docs/firestore-schema.md`) — campos anuláveis, o doc
 * pode vir incompleto. `svgUrl` deveria se chamar `imageUrl` no schema alvo (nunca guardou SVG
 * vetorial de fato); o rename fica para um diff separado porque cascateia até o formulário de
 * `CreateMapScreen`. `imageWidthPx`/`imageHeightPx`/`sector` existem no schema alvo mas nenhum
 * fluxo de cadastro ainda os preenche — ver "Estado de implementação atual" no doc de schema.
 */
@Serializable
data class MapDto(
    val name: String? = null,
    val description: String? = null,
    val svgUrl: String? = null,
    val imageWidthPx: Long? = null,
    val imageHeightPx: Long? = null,
    val scale: Double? = null,
    val floor: Long? = null,
    val sector: String? = null,
    val createdBy: String? = null,
    val createdAt: BaseTimestamp? = null,
    val updatedAt: BaseTimestamp? = null,
    val isActive: Boolean? = null,
)
