package me.panpf.sketch.sample.event

import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.ui.Page

class ChangePageEvent(val page: Page)

class AppConfigChangedEvent(var key: AppConfig.Key)

class CacheCleanEvent

class DrawerOpenedEvent

class CloseDrawerEvent

// todo 改成 live data