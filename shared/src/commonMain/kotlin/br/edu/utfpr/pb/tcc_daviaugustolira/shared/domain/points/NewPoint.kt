package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points

data class NewPoint(
    val mapId: String,
    val name: String,
    val description: String,
    val x: Int,
    val y: Int,
)
