package com.github.panpf.sketch.sample.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.panpf.sketch.sample.ui.test.DesktopTempTestScreen
import com.github.panpf.sketch.sample.ui.test.SkiaColorTypeTestScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder

@Serializable
sealed interface DesktopRoute : NavKey

@Serializable
data object DesktopTempTestRoute : DesktopRoute

@Serializable
data object SkiaColorTypeTestRoute : DesktopRoute

actual fun PolymorphicModuleBuilder<NavKey>.platformSerializersModule() {
//    subclassesOfSealed<DesktopRoute>()
    subclass(DesktopTempTestRoute::class, DesktopTempTestRoute.serializer())
    subclass(SkiaColorTypeTestRoute::class, SkiaColorTypeTestRoute.serializer())
}

actual fun EntryProviderScope<NavKey>.platformEntryProvider() {
    entry<DesktopTempTestRoute> { DesktopTempTestScreen().Content() }
    entry<SkiaColorTypeTestRoute> { SkiaColorTypeTestScreen().Content() }
}