package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.request.internal.BaseRequestManager
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.target.Target

class TestCountTarget : Target {


    private val requestManager = BaseRequestManager()

    override fun getRequestManager(): RequestManager = requestManager
}