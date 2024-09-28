package com.github.panpf.sketch.extensions.view.ability.test

import com.github.panpf.sketch.ability.AbsAbilityImageView
import com.github.panpf.sketch.ability.Host
import com.github.panpf.sketch.request.RequestState
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Test
import kotlin.test.assertSame

class HostTest {

    @Test
    fun test() {
        val context = getTestContext()
        val abilityView = object : AbsAbilityImageView(context) {
            override val requestState: RequestState
                get() = throw UnsupportedOperationException()
        }
        val host = Host(abilityView, abilityView)
        assertSame(abilityView.context, host.context)
    }
}