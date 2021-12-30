package com.github.panpf.sketch.request.internal

import kotlinx.coroutines.sync.Mutex
import java.util.*

class RepeatTaskFilter {

    private val httpFetchTaskLockMap: MutableMap<String, Mutex> = WeakHashMap()

    @Synchronized
    fun getOrCreateHttpFetchMutexLock(key: String): Mutex {
        return httpFetchTaskLockMap[key] ?: Mutex().apply {
            this@RepeatTaskFilter.httpFetchTaskLockMap[key] = this
        }
    }
}