package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import kotlinx.serialization.Serializable

/**
 * Espelho do documento `points/{id}` (ver `docs/firestore-schema.md`). `type`, `instruction`,
 * `isAccessible`, `connections` e `fingerprint` existem no schema alvo mas nenhum fluxo de
 * cadastro/calibração ainda os preenche — ver "Estado de implementação atual" no doc de schema.
 */
@Serializable
data class PointDto(
    val mapId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val type: String? = null,
    val instruction: String? = null,
    val x: Long? = null,
    val y: Long? = null,
    val isAccessible: Boolean? = null,
    val connections: List<PointConnectionDto>? = null,
    val fingerprint: List<PointFingerprintEntryDto>? = null,
    val fingerprintUpdatedAt: BaseTimestamp? = null,
    val createdBy: String? = null,
    val createdAt: BaseTimestamp? = null,
    val updatedAt: BaseTimestamp? = null,
    val isActive: Boolean? = null,
)

/** Aresta do grafo de navegação — `toPointId` pode apontar para um `points` de outro `mapId`. */
@Serializable
data class PointConnectionDto(
    val toPointId: String? = null,
    val weightMeters: Double? = null,
)

/** Entrada do fingerprint agregado (WKNN), uma por beacon relevante para este PR. */
@Serializable
data class PointFingerprintEntryDto(
    val beaconId: String? = null,
    val rssiMean: Double? = null,
    val rssiStdDev: Double? = null,
    val sampleCount: Long? = null,
)
