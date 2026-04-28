package com.github.panpf.sketch.core.android.test.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class ProcessNameTestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(this, ProcessNameTestService::class.java))
    }
}