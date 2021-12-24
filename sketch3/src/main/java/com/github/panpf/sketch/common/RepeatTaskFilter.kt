package com.github.panpf.sketch.common

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
    fun getActiveHttpFetchTaskDeferred(key: String): Deferred<*>? {
        val deferred = httpFetchTaskDeferredMap[key]
        return if (deferred != null && !deferred.isActive) {
            @Suppress("DeferredResultUnused")
            httpFetchTaskDeferredMap.remove(key)
            null
        } else {
            deferred
        }
    }

    @Synchronized
    @Suppress("DeferredIsResult")
    @Deprecated("This function is only used to test environment, production environment, please use getActiveHttpFetchTaskDeferred instead")
    internal fun getHttpFetchTaskDeferred(key: String): Deferred<*>? {
        return httpFetchTaskDeferredMap[key]
    }
}