package com.github.panpf.sketch.sample.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.panpf.sketch.sample.ui.gallery.PhotoPagerParams
import com.github.panpf.sketch.sample.ui.gallery.PhotoPagerScreen
import com.github.panpf.sketch.sample.ui.test.AnimatablePlaceholderTestScreen
import com.github.panpf.sketch.sample.ui.test.AnimatedImageTestScreen
import com.github.panpf.sketch.sample.ui.test.BlurHashTestScreen
import com.github.panpf.sketch.sample.ui.test.CrossfadePainterTestScreen
import com.github.panpf.sketch.sample.ui.test.DecoderTestScreen
import com.github.panpf.sketch.sample.ui.test.DisplayInsanityTestScreen
import com.github.panpf.sketch.sample.ui.test.ExifOrientationTestScreen
import com.github.panpf.sketch.sample.ui.test.FetcherTestScreen
import com.github.panpf.sketch.sample.ui.test.IconPainterTestScreen
import com.github.panpf.sketch.sample.ui.test.PainterMixTestScreen
import com.github.panpf.sketch.sample.ui.test.PreviewTestScreen
import com.github.panpf.sketch.sample.ui.test.ProgressIndicatorTestScreen
import com.github.panpf.sketch.sample.ui.test.ProgressTestScreen
import com.github.panpf.sketch.sample.ui.test.ResizePainterTestScreen
import com.github.panpf.sketch.sample.ui.test.TempTestScreen
import com.github.panpf.sketch.sample.ui.test.TransformationTestScreen
import com.github.panpf.sketch.sample.ui.test.UserZoomTestScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

interface MyNavKey : NavKey {
    val lightStatusAndNavigationBar: Boolean
        get() = true
}

@Serializable
sealed interface Route : MyNavKey

@Serializable
data object VerHomeRoute : Route

@Serializable
data object HorHomeRoute : Route

@Serializable
data class PhotoPagerRoute(val params: PhotoPagerParams) : Route {
    override val lightStatusAndNavigationBar: Boolean
        get() = false
}

@Serializable
data object DecoderTestRoute : Route

@Serializable
data object FetcherTestRoute : Route

@Serializable
data object AnimatedImageTestRoute : Route

@Serializable
data object ExifOrientationTestRoute : Route

@Serializable
data object TransformationTestRoute : Route

@Serializable
data object ProgressTestRoute : Route

@Serializable
data object CrossfadePainterTestRoute : Route

@Serializable
data object ResizePainterTestRoute : Route

@Serializable
data object PainterMixTestRoute : Route

@Serializable
data object IconPainterTestRoute : Route

@Serializable
data object AnimatablePlaceholderTestRoute : Route

@Serializable
data object PreviewTestRoute : Route

@Serializable
data object ProgressIndicatorTestRoute : Route

@Serializable
data object BlurHashTestRoute : Route

@Serializable
data object DisplayInsanityTestRoute : Route

@Serializable
data object UserZoomTestRoute : Route

@Serializable
data object TempTestRoute : Route

val navSavedStateConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
//            subclassesOfSealed<Route>()
            subclass(VerHomeRoute::class, VerHomeRoute.serializer())
            subclass(HorHomeRoute::class, HorHomeRoute.serializer())
            subclass(PhotoPagerRoute::class, PhotoPagerRoute.serializer())
            subclass(DecoderTestRoute::class, DecoderTestRoute.serializer())
            subclass(FetcherTestRoute::class, FetcherTestRoute.serializer())
            subclass(AnimatedImageTestRoute::class, AnimatedImageTestRoute.serializer())
            subclass(ExifOrientationTestRoute::class, ExifOrientationTestRoute.serializer())
            subclass(TransformationTestRoute::class, TransformationTestRoute.serializer())
            subclass(ProgressTestRoute::class, ProgressTestRoute.serializer())
            subclass(CrossfadePainterTestRoute::class, CrossfadePainterTestRoute.serializer())
            subclass(ResizePainterTestRoute::class, ResizePainterTestRoute.serializer())
            subclass(PainterMixTestRoute::class, PainterMixTestRoute.serializer())
            subclass(IconPainterTestRoute::class, IconPainterTestRoute.serializer())
            subclass(
                AnimatablePlaceholderTestRoute::class,
                AnimatablePlaceholderTestRoute.serializer()
            )
            subclass(PreviewTestRoute::class, PreviewTestRoute.serializer())
            subclass(ProgressIndicatorTestRoute::class, ProgressIndicatorTestRoute.serializer())
            subclass(BlurHashTestRoute::class, BlurHashTestRoute.serializer())
            subclass(DisplayInsanityTestRoute::class, DisplayInsanityTestRoute.serializer())
            subclass(UserZoomTestRoute::class, UserZoomTestRoute.serializer())
            subclass(TempTestRoute::class, TempTestRoute.serializer())

            platformSerializersModule()
        }
    }
}

expect fun PolymorphicModuleBuilder<NavKey>.platformSerializersModule()

@Suppress("RemoveExplicitTypeArguments")
val navEntryProvider = entryProvider<NavKey> {
    entry<VerHomeRoute> { VerHomeScreen() }
    entry<HorHomeRoute> { HorHomeScreen() }
    entry<PhotoPagerRoute> { PhotoPagerScreen(it.params) }
    entry<DecoderTestRoute> { DecoderTestScreen() }
    entry<FetcherTestRoute> { FetcherTestScreen() }
    entry<AnimatedImageTestRoute> { AnimatedImageTestScreen() }
    entry<ExifOrientationTestRoute> { ExifOrientationTestScreen() }
    entry<TransformationTestRoute> { TransformationTestScreen() }
    entry<ProgressTestRoute> { ProgressTestScreen() }
    entry<CrossfadePainterTestRoute> { CrossfadePainterTestScreen() }
    entry<ResizePainterTestRoute> { ResizePainterTestScreen() }
    entry<PainterMixTestRoute> { PainterMixTestScreen() }
    entry<IconPainterTestRoute> { IconPainterTestScreen() }
    entry<AnimatablePlaceholderTestRoute> { AnimatablePlaceholderTestScreen() }
    entry<PreviewTestRoute> { PreviewTestScreen() }
    entry<ProgressIndicatorTestRoute> { ProgressIndicatorTestScreen() }
    entry<BlurHashTestRoute> { BlurHashTestScreen() }
    entry<DisplayInsanityTestRoute> { DisplayInsanityTestScreen() }
    entry<UserZoomTestRoute> { UserZoomTestScreen() }
    entry<TempTestRoute> { TempTestScreen() }

    platformEntryProvider()
}

expect fun EntryProviderScope<NavKey>.platformEntryProvider()

val LocalNavBackStack: ProvidableCompositionLocal<NavBackStack<NavKey>> =
    staticCompositionLocalOf { error("No NavStack provided") }