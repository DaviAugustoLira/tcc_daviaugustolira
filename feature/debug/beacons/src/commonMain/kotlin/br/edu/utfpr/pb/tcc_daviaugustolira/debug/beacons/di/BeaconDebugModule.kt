package br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.di

import br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.presentation.viewmodel.BeaconDebugViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon.BeaconScanConfig
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon.BeaconScanner
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon.createBeaconScanner
import org.koin.dsl.module

val beaconDebugModule =
    module {
        single<BeaconScanner> { createBeaconScanner(BeaconScanConfig()) }
        factory { BeaconDebugViewModel(get()) }
    }
