package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import kotlinx.serialization.Serializable

/**
 * Espelho do documento `logs/{id}` (ver `docs/firestore-schema.md`) — trilha de auditoria de
 * ações administrativas. `targetType`/`targetId` são a referência polimórfica ao alvo da ação
 * (nem toda ação é sobre um mapa, ex.: login/logout); `mapId` é conveniência denormalizada só
 * para filtro de UI, nunca autoritativa.
 */
@Serializable
data class LogDto(
    val administratorId: String? = null,
    val action: String? = null,
    val targetType: String? = null,
    val targetId: String? = null,
    val mapId: String? = null,
    val details: String? = null,
    val status: String? = null,
    val timestamp: BaseTimestamp? = null,
)
