package com.github.panpf.sketch.sample.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.panpf.sketch.sample.ui.test.SkiaColorTypeTestScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder

@Serializable
sealed interface JsCommonRoute : NavKey

@Serializable
data object SkiaColorTypeTestRoute : JsCommonRoute

actual fun PolymorphicModuleBuilder<NavKey>.platformSerializersModule() {
    subclass(SkiaColorTypeTestRoute::class, SkiaColorTypeTestRoute.serializer())
}

actual fun EntryProviderScope<NavKey>.platformEntryProvider() {
    entry<SkiaColorTypeTestRoute> { SkiaColorTypeTestScreen().Content() }
}