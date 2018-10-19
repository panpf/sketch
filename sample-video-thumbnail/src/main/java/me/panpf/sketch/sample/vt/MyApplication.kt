package me.panpf.sketch.sample.vt

import android.app.Application
import com.squareup.leakcanary.LeakCanary

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this)
        }
    }
}