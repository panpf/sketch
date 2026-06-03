package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.sample.ui.AnimatablePlaceholderTestRoute
import com.github.panpf.sketch.sample.ui.AnimatedImageTestRoute
import com.github.panpf.sketch.sample.ui.BlurHashTestRoute
import com.github.panpf.sketch.sample.ui.CrossfadePainterTestRoute
import com.github.panpf.sketch.sample.ui.DecoderTestRoute
import com.github.panpf.sketch.sample.ui.DisplayInsanityTestRoute
import com.github.panpf.sketch.sample.ui.ExifOrientationTestRoute
import com.github.panpf.sketch.sample.ui.FetcherTestRoute
import com.github.panpf.sketch.sample.ui.IconPainterTestRoute
import com.github.panpf.sketch.sample.ui.IosTempTestRoute
import com.github.panpf.sketch.sample.ui.MainThreadTestRoute
import com.github.panpf.sketch.sample.ui.PainterMixTestRoute
import com.github.panpf.sketch.sample.ui.PreviewTestRoute
import com.github.panpf.sketch.sample.ui.ProgressIndicatorTestRoute
import com.github.panpf.sketch.sample.ui.ProgressTestRoute
import com.github.panpf.sketch.sample.ui.ResizePainterTestRoute
import com.github.panpf.sketch.sample.ui.SkiaColorTypeTestRoute
import com.github.panpf.sketch.sample.ui.TempTestRoute
import com.github.panpf.sketch.sample.ui.TransformationTestRoute
import com.github.panpf.sketch.sample.ui.UIImageTestRoute

actual fun platformTestScreens(): List<Any> = listOf(
    TestGroup("Components"),
    TestItem("Decoder", DecoderTestRoute),
    TestItem("Fetcher", FetcherTestRoute),

    TestGroup("Functions"),
    TestItem("AnimatedImage", AnimatedImageTestRoute),
    TestItem("ExifOrientation", ExifOrientationTestRoute),
    TestItem("Transformation", TransformationTestRoute),
    TestItem("Progress", ProgressTestRoute),
    TestItem("DisplayInsanity", DisplayInsanityTestRoute),

    TestGroup("UI"),
    TestItem("CrossfadePainter", CrossfadePainterTestRoute),
    TestItem("ResizePainter", ResizePainterTestRoute),
    TestItem("Painter Mix", PainterMixTestRoute),
    TestItem("IconPainter", IconPainterTestRoute),
    TestItem("AnimatablePlaceholder", AnimatablePlaceholderTestRoute),
    TestItem("Preview", PreviewTestRoute),
    TestItem("ProgressIndicator", ProgressIndicatorTestRoute),
    TestItem("BlurHash", BlurHashTestRoute),

    TestGroup("Other"),
    TestItem("Main Thread Test", MainThreadTestRoute),
    TestItem("SkiaColorTypeTest", SkiaColorTypeTestRoute),
    TestItem("UIImageTest", UIImageTestRoute),
    TestItem("Temp (Common)", TempTestRoute),
    TestItem("Temp (iOS)", IosTempTestRoute),

    ProjectInfo,
)