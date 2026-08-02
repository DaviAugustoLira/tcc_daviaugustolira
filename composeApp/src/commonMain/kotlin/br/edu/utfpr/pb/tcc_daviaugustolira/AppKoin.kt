package br.edu.utfpr.pb.tcc_daviaugustolira

import br.edu.utfpr.pb.tcc_daviaugustolira.debug.beacons.di.beaconDebugModule
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.di.KoinAppDeclaration
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.di.initKoin

fun initAppKoin(appDeclaration: KoinAppDeclaration = {}) {
    initKoin {
        modules(beaconDebugModule)
        appDeclaration()
    }
}
