package br.edu.utfpr.pb.tcc_daviaugustolira.shared.healthcheck

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.Serializable

/**
 * Spike descartável (ver CLAUDE.md secao 5/9): só prova que o app fala com o Firestore nas duas
 * plataformas. Não é schema real do TCC (maps, points, beacons, historyCalibration,
 * administrators, logs), não vira repositório/domain — pode ser removido assim que a ponte
 * estiver confirmada em Android e iOS reais.
 */
@Serializable
private data class HealthcheckDoc(
    val ok: Boolean = true,
)

suspend fun runFirebaseHealthcheck(): Result<Boolean> =
    runCatching {
        val doc = Firebase.firestore.collection("healthcheck").document("ping")
        doc.set(HealthcheckDoc())
        doc.get().get<Boolean>("ok")
    }
