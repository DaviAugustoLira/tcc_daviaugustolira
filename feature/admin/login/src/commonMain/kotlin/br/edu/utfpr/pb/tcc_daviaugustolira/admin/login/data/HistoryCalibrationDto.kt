package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import kotlinx.serialization.Serializable

/**
 * Espelho do documento `historyCalibration/{id}` (ver `docs/firestore-schema.md`) — registro
 * bruto e **imutável** de uma sessão de calibração offline em um PR. Sem `mapId`: é sempre
 * derivável via `pointId -> points.mapId`, nunca duplicado aqui. `points.fingerprint` é o
 * agregado consumido em tempo real; este documento é a fonte histórica da qual ele é calculado.
 */
@Serializable
data class HistoryCalibrationDto(
    val pointId: String? = null,
    val beacons: List<CalibrationBeaconSampleDto>? = null,
    val beaconsCount: Long? = null,
    val createdBy: String? = null,
    val createdAt: BaseTimestamp? = null,
)

/** Amostras brutas de RSSI coletadas de um beacon durante uma sessão de calibração. */
@Serializable
data class CalibrationBeaconSampleDto(
    val beaconId: String? = null,
    val rssiSamples: List<Double>? = null,
    val rssiMean: Double? = null,
    val rssiStdDev: Double? = null,
)
