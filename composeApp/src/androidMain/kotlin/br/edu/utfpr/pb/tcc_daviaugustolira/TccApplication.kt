package br.edu.utfpr.pb.tcc_daviaugustolira

import android.app.Application
import org.koin.android.ext.koin.androidContext

class TccApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initAppKoin {
            androidContext(this@TccApplication)
        }
    }
}
