package me.panpf.sketch.sample.event

import me.panpf.sketch.sample.ui.Page
import me.panpf.sketch.sample.AppConfig

class ChangePageEvent(val page: Page)

class AppConfigChangedEvent(var key: AppConfig.Key)

class CacheCleanEvent

class DrawerOpenedEvent

class CloseDrawerEvent

class ChangeMainPageBgEvent(val imageUrl: String)