package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

/**
 * Documento escrito em `logs/{autoId}` ao registrar uma ação administrativa (ver
 * `docs/firestore-schema.md`). Imutável — sem update/delete nunca. Enquanto a escrita partir do
 * próprio client (sem Cloud Functions configuradas neste projeto ainda), este log é melhor
 * esforço, não prova à prova de admin malicioso — ver nota de confiabilidade no doc de schema.
 */
@Serializable
data class NewLogDto(
    val administratorId: String,
    val action: String,
    val status: String,
    val targetType: String? = null,
    val targetId: String? = null,
    val mapId: String? = null,
    val details: String? = null,
    val timestamp: BaseTimestamp = Timestamp.ServerTimestamp,
)
