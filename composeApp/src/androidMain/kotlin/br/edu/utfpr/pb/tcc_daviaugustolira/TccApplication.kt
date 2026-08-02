package br.edu.utfpr.pb.tcc_daviaugustolira

import android.app.Application
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.di.initKoin
import org.koin.android.ext.koin.androidContext

class TccApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@TccApplication)
        }
    }
}
