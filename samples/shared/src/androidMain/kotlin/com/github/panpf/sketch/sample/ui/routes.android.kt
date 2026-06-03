package com.github.panpf.sketch.sample.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.panpf.sketch.sample.ui.test.AndroidTempTestScreen
import com.github.panpf.sketch.sample.ui.test.LocalVideosTestScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder

@Serializable
sealed interface AndroidRoute : MyNavKey

@Serializable
data object LocalVideosRoute : AndroidRoute

@Serializable
data object AndroidTempTestRoute : AndroidRoute

actual fun PolymorphicModuleBuilder<NavKey>.platformSerializersModule() {
    subclass(LocalVideosRoute::class, LocalVideosRoute.serializer())
    subclass(AndroidTempTestRoute::class, AndroidTempTestRoute.serializer())
}

actual fun EntryProviderScope<NavKey>.platformEntryProvider() {
    entry<LocalVideosRoute> { LocalVideosTestScreen() }
    entry<AndroidTempTestRoute> { AndroidTempTestScreen() }
}
