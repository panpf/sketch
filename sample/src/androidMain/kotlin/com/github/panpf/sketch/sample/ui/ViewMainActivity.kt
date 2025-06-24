package com.github.panpf.sketch.sample.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import com.github.panpf.sketch.sample.databinding.ActivityNavHostBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingActivity
import com.github.panpf.sketch.sample.util.collectWithLifecycle
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.google.android.material.internal.EdgeToEdgeUtils

class ViewMainActivity : BaseBindingActivity<ActivityNavHostBinding>() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(binding: ActivityNavHostBinding, savedInstanceState: Bundle?) {
        EdgeToEdgeUtils.applyEdgeToEdge(/* window = */ window,/* edgeToEdgeEnabled = */ true)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.parseColor("#60000000")
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            window.navigationBarColor = Color.TRANSPARENT
        }

        appSettings.composePage.ignoreFirst().collectWithLifecycle(this) {
            startActivity(Intent(this, ComposeMainActivity::class.java))
            this@ViewMainActivity.finish()
        }
    }
}