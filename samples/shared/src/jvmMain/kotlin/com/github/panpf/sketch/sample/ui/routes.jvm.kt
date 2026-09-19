package com.github.panpf.sketch.sample.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.panpf.sketch.sample.ui.test.JvmTempTestScreen
import com.github.panpf.sketch.sample.ui.test.SkiaColorTypeTestScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder

@Serializable
sealed interface JvmRoute : NavKey

@Serializable
data object JvmTempTestRoute : JvmRoute

@Serializable
data object SkiaColorTypeTestRoute : JvmRoute

actual fun PolymorphicModuleBuilder<NavKey>.platformSerializersModule() {
//    subclassesOfSealed<JvmRoute>()
    subclass(JvmTempTestRoute::class, JvmTempTestRoute.serializer())
    subclass(SkiaColorTypeTestRoute::class, SkiaColorTypeTestRoute.serializer())
}

actual fun EntryProviderScope<NavKey>.platformEntryProvider() {
    entry<JvmTempTestRoute> { JvmTempTestScreen() }
    entry<SkiaColorTypeTestRoute> { SkiaColorTypeTestScreen() }
}