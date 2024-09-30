package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.request.internal.OneShotRequestManager
import kotlin.test.Test
import kotlin.test.assertTrue

class OneShotRequestManagerTest {

    @Test
    fun testAttached() {
        assertTrue(OneShotRequestManager().isAttached())
    }
}