package com.github.panpf.sketch3.common

import kotlinx.coroutines.Deferred
import java.util.*

class RepeatTaskFilter {

    private val httpFetchTaskDeferredMap: MutableMap<String, Deferred<*>> = WeakHashMap()

    @Synchronized
    fun putHttpFetchTaskDeferred(key: String, deferred: Deferred<*>) {
        httpFetchTaskDeferredMap[key] = deferred
    }

    @Synchronized
    fun removeHttpFetchTaskDeferred(key: String) {
        @Suppress("DeferredResultUnused")
        httpFetchTaskDeferredMap.remove(key)
    }

    @Synchronized
    @Suppress("DeferredIsResult")
    fun getHttpFetchTaskDeferred(key: String): Deferred<*>? {
        return httpFetchTaskDeferredMap[key]
    }
}