package com.github.panpf.sketch.sample.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.base.BaseActivity
import com.github.panpf.sketch.sample.ui.gallery.PhotoPagerScreen
import com.github.panpf.sketch.sample.ui.util.isDarkTheme
import com.github.panpf.sketch.sample.util.collectWithLifecycle
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.google.android.material.internal.EdgeToEdgeUtils

class ComposeMainActivity : BaseActivity() {

    private var lightStatusAndNavigationBar: Boolean? = null
        set(value) {
            if (value != field) {
                field = value
                if (resumed) {
                    setupLightStatusAndNavigationBar()
                }
            }
        }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EdgeToEdgeUtils.applyEdgeToEdge(/* window = */ window,/* edgeToEdgeEnabled = */ true)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.parseColor("#60000000")
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            window.navigationBarColor = Color.TRANSPARENT
        }

        setContent {
            App(onContentChanged = { navigator ->
                lightStatusAndNavigationBar = navigator.lastItem !is PhotoPagerScreen
            })
        }

        appSettings.composePage.ignoreFirst().collectWithLifecycle(this) {
            startActivity(Intent(this, ViewMainActivity::class.java))
            this@ComposeMainActivity.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        setupLightStatusAndNavigationBar()
    }

    @SuppressLint("RestrictedApi")
    private fun setupLightStatusAndNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EdgeToEdgeUtils.setLightStatusBar(
                /* window = */ window,
                /* isLight = */
                lightStatusAndNavigationBar != false && !isDarkTheme()
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            EdgeToEdgeUtils.setLightNavigationBar(
                /* window = */ window,
                /* isLight = */
                lightStatusAndNavigationBar != false && !isDarkTheme()
            )
        }
    }
}