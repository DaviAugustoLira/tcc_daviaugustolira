package br.edu.utfpr.pb.tcc_daviaugustolira.shared.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
    }
}

typealias KoinAppDeclaration = KoinApplication.() -> Unit
