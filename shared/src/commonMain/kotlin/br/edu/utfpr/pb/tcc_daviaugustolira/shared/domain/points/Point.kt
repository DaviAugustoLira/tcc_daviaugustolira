package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points

data class Point(
    val id: String,
    val mapId: String,
    val name: String,
    val description: String,
    val x: Int,
    val y: Int,
)
