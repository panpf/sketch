package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import org.koin.core.context.startKoin
import org.koin.dsl.module

object Koins {

    init {
        startKoin {
            modules(module {
                single { Sketch(getTestContext()) }
            })
        }
    }

    fun initial() {

    }
}