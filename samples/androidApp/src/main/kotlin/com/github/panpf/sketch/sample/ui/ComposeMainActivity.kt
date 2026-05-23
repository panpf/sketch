package com.github.panpf.sketch.sample.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.panpf.sketch.sample.ui.base.BaseActivity
import com.github.panpf.sketch.sample.ui.base.EdgeToEdgeController
import com.github.panpf.sketch.sample.ui.util.isDarkTheme
import com.github.panpf.sketch.sample.util.collectWithLifecycle
import com.github.panpf.sketch.sample.util.ignoreFirst

class ComposeMainActivity : BaseActivity() {

    private var lightModeSystemBars: Boolean = true
        set(value) {
            if (value != field) {
                field = value
                if (resumed) {
                    setupLightModeSystemBars()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(onNavBackStackChanged = {
                val route = it.lastOrNull()
                if (route != null && route is MyNavKey) {
                    lightModeSystemBars = route.lightModeSystemBars
                }
            })
        }

        appSettings.composePage.ignoreFirst().collectWithLifecycle(this) {
            startActivity(Intent(this, ViewMainActivity::class.java))
            this@ComposeMainActivity.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        setupLightModeSystemBars()
    }

    private fun setupLightModeSystemBars() {
        val darkTheme = isDarkTheme()
        val isLightMode = lightModeSystemBars && !darkTheme
        EdgeToEdgeController.setStatusBarStyle(window = window, isLightMode = isLightMode)
        EdgeToEdgeController.setNavigationBarStyle(window = window, isLightMode = isLightMode)
    }
}