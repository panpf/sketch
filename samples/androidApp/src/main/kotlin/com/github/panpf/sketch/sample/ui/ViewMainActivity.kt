package com.github.panpf.sketch.sample.ui

import android.content.Intent
import android.os.Bundle
import com.github.panpf.sketch.sample.databinding.ActivityNavHostBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingActivity
import com.github.panpf.sketch.sample.util.collectWithLifecycle
import com.github.panpf.sketch.sample.util.ignoreFirst

class ViewMainActivity : BaseBindingActivity<ActivityNavHostBinding>() {

    override fun onCreate(binding: ActivityNavHostBinding, savedInstanceState: Bundle?) {
        appSettings.composePage.ignoreFirst().collectWithLifecycle(this) {
            startActivity(Intent(this, ComposeMainActivity::class.java))
            this@ViewMainActivity.finish()
        }
    }
}