package com.github.panpf.sketch.sample.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

actual fun EntryProviderScope<NavKey>.platformEntryProvider() {

}

actual fun PolymorphicModuleBuilder<NavKey>.platformSerializersModule() {

}