package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.RequestInterceptor.Chain

class PauseLoadWhenScrollingDisplayInterceptor : RequestInterceptor {

    companion object {
        var scrolling = false
    }

    var enabled = true

    @MainThread
    override suspend fun intercept(chain: Chain): ImageData {
        val request = chain.request
        if (request !is DisplayRequest) {
            return chain.proceed(request)
        }

        val requestDepth = request.depth
        val finalRequest = if (
            enabled
            && scrolling
            && request.isPauseLoadWhenScrolling
            && !request.isIgnoredPauseLoadWhenScrolling
            && requestDepth < RequestDepth.MEMORY
        ) {
            request.newDisplayRequest {
                depth(RequestDepth.MEMORY)
                setDepthFromPauseLoadWhenScrolling()
            }
        } else {
            request
        }
        return chain.proceed(finalRequest)
    }

    override fun toString(): String = "PauseLoadWhenScrollingDisplayInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PauseLoadWhenScrollingDisplayInterceptor

        if (enabled != other.enabled) return false

        return true
    }

    override fun hashCode(): Int {
        return enabled.hashCode()
    }
}