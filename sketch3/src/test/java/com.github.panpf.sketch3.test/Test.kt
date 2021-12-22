package com.github.panpf.sketch3.test

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Test

class Test {
    @Test
    fun test() {
        val result = GlobalScope.launch {
            coroutineScope {
                async {
                    delay(2000)
                    System.currentTimeMillis()
                }.await()
                System.currentTimeMillis()
            }
            System.currentTimeMillis()
        }
        System.currentTimeMillis()
    }
}