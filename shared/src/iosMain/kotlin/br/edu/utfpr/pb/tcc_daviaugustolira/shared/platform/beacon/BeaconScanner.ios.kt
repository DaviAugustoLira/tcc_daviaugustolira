package br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLBeacon
import platform.CoreLocation.CLBeaconIdentityConstraint
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSUUID
import platform.darwin.NSObject
import platform.posix.time

actual fun createBeaconScanner(config: BeaconScanConfig): BeaconScanner = CoreLocationBeaconScanner(config)

@OptIn(ExperimentalForeignApi::class)
private class CoreLocationBeaconScanner(
    private val config: BeaconScanConfig,
) : BeaconScanner {
    override fun readings(): Flow<List<BeaconReading>> =
        callbackFlow {
            val locationManager = CLLocationManager()
            val constraints =
                config.proximityUuids.mapNotNull { uuidString ->
                    NSUUID(uUIDString = uuidString).let { CLBeaconIdentityConstraint(uUID = it) }
                }

            val readingsByKey = mutableMapOf<String, BeaconReading>()

            val delegate =
                object : NSObject(), CLLocationManagerDelegateProtocol {
                    override fun locationManager(
                        manager: CLLocationManager,
                        didRangeBeacons: List<*>,
                        satisfyingConstraint: CLBeaconIdentityConstraint,
                    ) {
                        @Suppress("UNCHECKED_CAST")
                        val beacons = didRangeBeacons as List<CLBeacon>
                        val nowMs = time(null) * MILLIS_PER_SECOND

                        for (beacon in beacons) {
                            val reading =
                                BeaconReading(
                                    uuid = beacon.proximityUUID.UUIDString,
                                    major = beacon.major.intValue,
                                    minor = beacon.minor.intValue,
                                    rssi = beacon.rssi.toInt(),
                                    // iOS não expõe o txPower calibrado por beacon (CoreLocation esconde,
                                    // ver CLAUDE.md "Ponto critico") — sem valor confiável pra reportar aqui.
                                    txPower = 0,
                                    timestampMs = nowMs,
                                )
                            readingsByKey[reading.key()] = reading
                        }
                        trySend(readingsByKey.values.toList())
                    }
                }

            locationManager.delegate = delegate
            locationManager.requestWhenInUseAuthorization()
            constraints.forEach { locationManager.startRangingBeaconsSatisfyingConstraint(it) }

            awaitClose {
                constraints.forEach { locationManager.stopRangingBeaconsSatisfyingConstraint(it) }
            }
        }

    private fun BeaconReading.key(): String = "$uuid:$major:$minor"

    private companion object {
        const val MILLIS_PER_SECOND = 1000L
    }
}
