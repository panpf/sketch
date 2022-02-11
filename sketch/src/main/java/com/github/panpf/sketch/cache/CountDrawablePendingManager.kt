package com.github.panpf.sketch.cache

import androidx.annotation.MainThread
import com.github.panpf.sketch.drawable.SketchCountDrawable
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.requiredMainThread

class CountDrawablePendingManager constructor(private val logger: Logger) {

    companion object {
        const val MODULE = "CountDrawablePendingManager"
    }

    private val map = HashMap<String, SketchCountDrawable>()

    @MainThread
    fun mark(callingStation: String, key: String, drawable: SketchCountDrawable) {
        requiredMainThread()
        val old = map[key]
        if (old != drawable) {
            if (old != null) {
                realComplete("$callingStation:mark", key, drawable)
            }
            map[key] = drawable
        }
        drawable.setIsPending("$callingStation:mark", true)
        logger.d(MODULE) {
            "mark. $callingStation. ${map.size}. $key"
        }
    }

    @MainThread
    fun complete(callingStation: String, key: String) {
        requiredMainThread()
        val drawable = map[key]
        if (drawable != null) {
            realComplete("$callingStation:complete", key, drawable)
        }
    }

    @MainThread
    private fun realComplete(callingStation: String, key: String, drawable: SketchCountDrawable) {
        drawable.setIsPending("$callingStation:complete", false)
        if (drawable.getPendingCount() == 0) {
            map.remove(key)
        }
        logger.d(MODULE) {
            "complete. $callingStation. ${map.size}. $key"
        }
    }
}