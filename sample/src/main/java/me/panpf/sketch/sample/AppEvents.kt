package me.panpf.sketch.sample

import com.github.panpf.liveevent.LiveEvent

object AppEvents {
    val appConfigChangedEvent = LiveEvent<AppConfig.Key>()
    val cacheCleanEvent = LiveEvent<Int>()
    val playImageEvent = LiveEvent<Int>()
}