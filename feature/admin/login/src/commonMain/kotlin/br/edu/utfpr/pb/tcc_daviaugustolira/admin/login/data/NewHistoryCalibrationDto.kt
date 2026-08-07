package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

/**
 * Documento escrito em `historyCalibration/{autoId}` ao concluir uma sessão de calibração (ver
 * `docs/firestore-schema.md`). Sem `updatedAt` de propósito — este documento nunca é editado
 * após criado.
 */
@Serializable
data class NewHistoryCalibrationDto(
    val pointId: String,
    val beacons: List<CalibrationBeaconSampleDto>,
    val beaconsCount: Int,
    val createdBy: String,
    val createdAt: BaseTimestamp = Timestamp.ServerTimestamp,
)
