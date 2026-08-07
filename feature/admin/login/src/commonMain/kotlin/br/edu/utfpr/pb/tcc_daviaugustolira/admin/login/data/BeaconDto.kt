package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import kotlinx.serialization.Serializable

/**
 * Espelho do documento `beacons/{id}` (ver `docs/firestore-schema.md`). O ID do documento
 * **não é auto-gerado** — é `"${uuid}_${major}_${minor}"`, para que o Firestore garanta de
 * graça a unicidade desse trio (é como o scanner BLE identifica qual beacon físico foi
 * detectado; duplicar o trio corrompe o posicionamento WKNN silenciosamente). Repositório que
 * gravar esta coleção deve usar `.document(id).set(...)`, nunca `.add(...)`.
 */
@Serializable
data class BeaconDto(
    val mapId: String? = null,
    val uuid: String? = null,
    val major: Long? = null,
    val minor: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val x: Long? = null,
    val y: Long? = null,
    val txPower: Double? = null,
    val battery: Double? = null,
    val createdBy: String? = null,
    val createdAt: BaseTimestamp? = null,
    val updatedAt: BaseTimestamp? = null,
    val isActive: Boolean? = null,
)
