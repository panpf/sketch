package com.github.panpf.sketch.sample.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.panpf.sketch.sample.ui.test.MainThreadTestScreen
import com.github.panpf.sketch.sample.ui.test.SkiaColorTypeTestScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder

@Serializable
sealed interface IosRoute : NavKey

@Serializable
data object MainThreadTestRoute : IosRoute

@Serializable
data object SkiaColorTypeTestRoute : IosRoute

actual fun PolymorphicModuleBuilder<NavKey>.platformSerializersModule() {
//    subclassesOfSealed<IosRoute>()
    subclass(MainThreadTestRoute::class, MainThreadTestRoute.serializer())
    subclass(SkiaColorTypeTestRoute::class, SkiaColorTypeTestRoute.serializer())
}

actual fun EntryProviderScope<NavKey>.platformEntryProvider() {
    entry<MainThreadTestRoute> { MainThreadTestScreen().Content() }
    entry<SkiaColorTypeTestRoute> { SkiaColorTypeTestScreen().Content() }
}
