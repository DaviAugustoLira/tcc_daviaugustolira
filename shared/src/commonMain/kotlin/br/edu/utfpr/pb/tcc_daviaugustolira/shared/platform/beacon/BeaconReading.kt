package br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon

data class BeaconReading(
    val uuid: String,
    val major: Int,
    val minor: Int,
    val rssi: Int,
    val txPower: Int,
    val timestampMs: Long,
)
