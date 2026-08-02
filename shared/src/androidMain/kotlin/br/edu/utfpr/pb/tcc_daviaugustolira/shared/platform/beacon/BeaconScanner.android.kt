package br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon

import com.juul.kable.Scanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan

actual fun createBeaconScanner(config: BeaconScanConfig): BeaconScanner = KableBeaconScanner(config)

private class KableBeaconScanner(
    private val config: BeaconScanConfig,
) : BeaconScanner {
    private val proximityUuids = config.proximityUuids.map { it.uppercase() }.toSet()

    override fun readings(): Flow<List<BeaconReading>> =
        Scanner()
            .advertisements
            .mapNotNull { advertisement ->
                val manufacturerData = advertisement.manufacturerData ?: return@mapNotNull null
                val frame = parseIBeaconFrame(manufacturerData.code, manufacturerData.data) ?: return@mapNotNull null
                if (frame.uuid !in proximityUuids) return@mapNotNull null

                BeaconReading(
                    uuid = frame.uuid,
                    major = frame.major,
                    minor = frame.minor,
                    rssi = advertisement.rssi,
                    txPower = frame.txPower,
                    timestampMs = System.currentTimeMillis(),
                )
            }
            // Beacons físicos re-anunciam várias vezes por segundo; mantém a leitura mais
            // recente de cada beacon (chave uuid+major+minor) em vez de crescer sem limite.
            .scan(emptyMap<String, BeaconReading>()) { acc, reading -> acc + (reading.key() to reading) }
            .map { it.values.toList() }
            // Bluetooth desligado ou permissão negada é erro esperado (ver CLAUDE.md secao 2/6),
            // não motivo pra derrubar o app — fecha a lista de leituras em vez de propagar.
            .catch { emit(emptyList()) }

    private fun BeaconReading.key(): String = "$uuid:$major:$minor"
}
