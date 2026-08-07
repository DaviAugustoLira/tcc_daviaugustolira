package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

/**
 * Documento escrito em `beacons/{"${uuid}_${major}_${minor}"}` ao cadastrar um beacon novo (ver
 * `docs/firestore-schema.md`). Repositório grava com `.document(id).set(this)`, não `.add(...)`
 * — o ID composto é o que garante a unicidade do trio uuid/major/minor no Firestore.
 */
@Serializable
data class NewBeaconDto(
    val mapId: String,
    val uuid: String,
    val major: Long,
    val minor: Long,
    val name: String,
    val description: String,
    val x: Long,
    val y: Long,
    val txPower: Double,
    val createdBy: String,
    val battery: Double? = null,
    val isActive: Boolean = true,
    val createdAt: BaseTimestamp = Timestamp.ServerTimestamp,
    val updatedAt: BaseTimestamp = Timestamp.ServerTimestamp,
)
