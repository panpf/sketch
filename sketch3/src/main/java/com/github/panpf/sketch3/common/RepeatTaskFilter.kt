package com.github.panpf.sketch3.common

import com.github.panpf.sketch3.common.fetch.FetchResult
import kotlinx.coroutines.Deferred
import java.util.*

class RepeatTaskFilter {

    private val httpFetchTaskDeferredMap: MutableMap<String, Deferred<FetchResult?>> = WeakHashMap()

    @Synchronized
    fun putHttpFetchTaskDeferred(key: String, deferred: Deferred<FetchResult?>) {
        httpFetchTaskDeferredMap[key] = deferred
    }

    @Synchronized
    fun removeHttpFetchTaskDeferred(key: String) {
        @Suppress("DeferredResultUnused")
        httpFetchTaskDeferredMap.remove(key)
    }

    @Synchronized
    @Suppress("DeferredIsResult")
    fun getHttpFetchTaskDeferred(key: String): Deferred<FetchResult?>? {
        return httpFetchTaskDeferredMap[key]
    }
}