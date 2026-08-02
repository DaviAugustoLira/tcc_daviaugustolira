package br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon

/**
 * No iOS, CoreLocation só faz ranging de um CLBeaconRegion com proximity UUID conhecido
 * de antemão (ao contrário do Android, que pode filtrar o scan BLE genérico por UUID).
 * Por isso o(s) UUID(s) do(s) beacon(s) físico(s) entram como config injetável, nunca
 * hardcoded dentro do `actual`.
 */
@Suppress("ForbiddenComment")
data class BeaconScanConfig(
    val proximityUuids: List<String> =
        listOf(
            // TODO: troque pelo proximity UUID real do beacon físico de teste antes de rodar em iPhone.
            "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0",
        ),
)
