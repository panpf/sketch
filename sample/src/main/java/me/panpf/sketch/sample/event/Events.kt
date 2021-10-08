package me.panpf.sketch.sample.event

import me.panpf.sketch.sample.AppConfig

class AppConfigChangedEvent(var key: AppConfig.Key)

class CacheCleanEvent

// todo 改成 live data