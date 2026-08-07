package com.github.panpf.sketch.sample.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.panpf.sketch.sample.ui.test.MacosTempTestScreen
import com.github.panpf.sketch.sample.ui.test.SkiaColorTypeTestScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder

@Serializable
sealed interface MacosRoute : NavKey

@Serializable
data object MacosTempTestRoute : MacosRoute

@Serializable
data object SkiaColorTypeTestRoute : MacosRoute

actual fun PolymorphicModuleBuilder<NavKey>.platformSerializersModule() {
//    subclassesOfSealed<MacosRoute>()
    subclass(MacosTempTestRoute::class, MacosTempTestRoute.serializer())
    subclass(SkiaColorTypeTestRoute::class, SkiaColorTypeTestRoute.serializer())
}

actual fun EntryProviderScope<NavKey>.platformEntryProvider() {
    entry<MacosTempTestRoute> { MacosTempTestScreen() }
    entry<SkiaColorTypeTestRoute> { SkiaColorTypeTestScreen() }
}