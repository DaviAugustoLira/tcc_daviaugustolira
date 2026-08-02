package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.di

import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data.FirebaseAdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data.FirestoreMapsRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.LoginUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.LogoutUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObserveMapsUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObserveSessionUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminShellViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.LoginViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.MapsRepository
import org.koin.dsl.module

val adminLoginModule =
    module {
        single<AdminSessionRepository> { FirebaseAdminSessionRepository() }
        single<MapsRepository> { FirestoreMapsRepository() }
        factory { LoginUseCase(get()) }
        factory { LogoutUseCase(get()) }
        factory { ObserveSessionUseCase(get()) }
        factory { ObserveMapsUseCase(get()) }
        factory { LoginViewModel(get()) }
        factory { AdminShellViewModel(get(), get(), get()) }
    }
