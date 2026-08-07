package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps

data class NewMap(
    val name: String,
    val description: String,
    val svgUrl: String,
    val scale: Double,
    val floor: Int,
)
