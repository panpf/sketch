package com.github.panpf.sketch3

import android.content.Context
import com.github.panpf.sketch3.common.Disposable
import com.github.panpf.sketch3.common.OneShotDisposable
import com.github.panpf.sketch3.common.cache.disk.DiskCache
import com.github.panpf.sketch3.common.cache.disk.LruDiskCache
import com.github.panpf.sketch3.download.DownloadRequest
import com.github.panpf.sketch3.download.DownloadResult
import com.github.panpf.sketch3.download.internal.DownloadExecutor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File

class Sketch3(val context: Context, val diskCache: DiskCache) {

    private val downloadExecutor: DownloadExecutor = DownloadExecutor(context)

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, throwable ->
//          logger?.log(TAG, throwable)
            SLog.wmt("scope", throwable, "exception")
        }
    )

    fun enqueueDownload(downloadRequest: DownloadRequest): Disposable<DownloadResult> {
        val job = scope.async {
            downloadExecutor.executeOnMain(downloadRequest)
        }
        return OneShotDisposable(job)
    }

    suspend fun executeDownload(downloadRequest: DownloadRequest): DownloadResult =
        coroutineScope {
            val job = scope.async {
                downloadExecutor.executeOnMain(downloadRequest)
            }
            return@coroutineScope job.await()
        }

    class Builder(context: Context) {

        private val appContext: Context = context.applicationContext
        private var diskCache: DiskCache? = null

        fun diskCache(diskCache: DiskCache): Builder = apply {
            this.diskCache = diskCache
        }

        fun build(): Sketch3 = Sketch3(
            appContext,
            diskCache ?: LruDiskCache(
                appContext,
                errorCallback = { dir: File, throwable: Throwable ->
//                configuration.callback.onError(InstallDiskCacheException(e, cacheDir))
                }
            )
        )
    }
}