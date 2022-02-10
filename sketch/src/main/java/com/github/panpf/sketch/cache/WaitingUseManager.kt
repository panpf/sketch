package com.github.panpf.sketch.cache

import androidx.annotation.MainThread
import com.github.panpf.sketch.drawable.SketchCountDrawable

class WaitingUseManager {
    private val map = HashMap<String, SketchCountDrawable>()

    @MainThread
    fun put(callingStation: String, key: String, drawable: SketchCountDrawable) {
        val old = map[key]
        if (old != drawable) {
            remove(callingStation, key)
            drawable.setIsWaiting("$callingStation:put", true)
            map[key] = drawable
        }
    }

    @MainThread
    fun remove(callingStation: String, key: String) {
        map.remove(key)?.setIsWaiting("$callingStation:remove", false)
    }
}