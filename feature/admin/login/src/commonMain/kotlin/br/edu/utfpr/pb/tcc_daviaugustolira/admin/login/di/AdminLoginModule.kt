package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.di

import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data.FirebaseAdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data.FirestoreMapsRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data.FirestorePointsRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.CreateMapUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.CreatePointUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.LoginUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.LogoutUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObserveMapsUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObservePointsUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain.ObserveSessionUseCase
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminMapViewerViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.AdminShellViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.CreateMapViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.LoginViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.MapsRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.PointsRepository
import org.koin.dsl.module

val adminLoginModule =
    module {
        single<AdminSessionRepository> { FirebaseAdminSessionRepository() }
        single<MapsRepository> { FirestoreMapsRepository() }
        single<PointsRepository> { FirestorePointsRepository() }
        factory { LoginUseCase(get()) }
        factory { LogoutUseCase(get()) }
        factory { ObserveSessionUseCase(get()) }
        factory { ObserveMapsUseCase(get()) }
        factory { CreateMapUseCase(get(), get()) }
        factory { ObservePointsUseCase(get()) }
        factory { CreatePointUseCase(get(), get()) }
        factory { LoginViewModel(get()) }
        factory { AdminShellViewModel(get(), get(), get()) }
        factory { CreateMapViewModel(get()) }
        factory { (mapId: String, name: String, imageUrl: String) ->
            AdminMapViewerViewModel(mapId, name, imageUrl, get(), get())
        }
    }
