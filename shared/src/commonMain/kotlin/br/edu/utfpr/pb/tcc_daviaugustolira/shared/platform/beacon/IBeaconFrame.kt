package br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon

/**
 * Só o que dá pra extrair do payload iBeacon em si (UUID/major/minor/txPower calibrado).
 * RSSI e timestamp não fazem parte do frame — vêm do pacote de advertisement recebido
 * ao vivo, então quem monta o [BeaconReading] final é o `actual` de cada plataforma.
 */
data class IBeaconFrame(
    val uuid: String,
    val major: Int,
    val minor: Int,
    val txPower: Int,
)

private const val APPLE_COMPANY_ID = 0x004C
private const val IBEACON_TYPE: Byte = 0x02
private const val IBEACON_DATA_LENGTH: Byte = 0x15
private const val IBEACON_FRAME_SIZE = 23
private const val UUID_START = 2
private const val UUID_END = 18
private const val MAJOR_OFFSET = 18
private const val MINOR_OFFSET = 20
private const val TX_POWER_OFFSET = 22

/**
 * Parseia o manufacturer data (0x004C, Apple) de um advertisement BLE como frame iBeacon.
 * Retorna `null` (nunca lança) se o company id ou o layout dos bytes não baterem com o
 * formato esperado — frame inválido é um caso esperado (outro fabricante, pacote truncado),
 * não uma exceção.
 */
fun parseIBeaconFrame(
    companyId: Int,
    data: ByteArray,
): IBeaconFrame? {
    val isValidIBeaconFrame =
        companyId == APPLE_COMPANY_ID &&
            data.size >= IBEACON_FRAME_SIZE &&
            data[0] == IBEACON_TYPE &&
            data[1] == IBEACON_DATA_LENGTH
    if (!isValidIBeaconFrame) return null

    return IBeaconFrame(
        uuid = formatUuid(data.copyOfRange(UUID_START, UUID_END)),
        major = readUInt16BigEndian(data, MAJOR_OFFSET),
        minor = readUInt16BigEndian(data, MINOR_OFFSET),
        txPower = data[TX_POWER_OFFSET].toInt(),
    )
}

private fun readUInt16BigEndian(
    data: ByteArray,
    offset: Int,
): Int = ((data[offset].toInt() and 0xFF) shl 8) or (data[offset + 1].toInt() and 0xFF)

private fun formatUuid(bytes: ByteArray): String {
    val hex = bytes.toHex()
    return listOf(
        hex.substring(0, 8),
        hex.substring(8, 12),
        hex.substring(12, 16),
        hex.substring(16, 20),
        hex.substring(20, 32),
    ).joinToString("-").uppercase()
}

private fun ByteArray.toHex(): String {
    val hexChars = "0123456789abcdef"
    val builder = StringBuilder(size * 2)
    for (byte in this) {
        val value = byte.toInt() and 0xFF
        builder.append(hexChars[value shr 4])
        builder.append(hexChars[value and 0x0F])
    }
    return builder.toString()
}
