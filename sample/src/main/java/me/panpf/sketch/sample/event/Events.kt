package me.panpf.sketch.sample.event

import me.panpf.sketch.sample.ui.Page
import me.panpf.sketch.sample.util.AppConfig

class AppConfigChangedEvent(var key: AppConfig.Key)

class CacheCleanEvent

class DrawerOpenedEvent

class SwitchMainPageEvent(val page: Page)

class CloseDrawerEvent

class ChangeMainPageBgEvent(val imageUrl: String)