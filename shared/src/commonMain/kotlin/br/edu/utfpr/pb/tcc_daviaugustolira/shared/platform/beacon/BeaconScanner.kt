package br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon

import kotlinx.coroutines.flow.Flow

interface BeaconScanner {
    fun readings(): Flow<List<BeaconReading>>
}

expect fun createBeaconScanner(config: BeaconScanConfig): BeaconScanner
