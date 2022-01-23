package com.github.panpf.sketch.request

import androidx.annotation.ColorInt

private const val SVG_BACKGROUND_COLOR_KEY = "sketch#svgBackgroundColor"

// todo 文件名改为 Svg1Extensions 提交 再改为 SvgExtensions

fun LoadRequest.Builder.svgBackgroundColor(@ColorInt color: Int) = apply {
    setParameter(SVG_BACKGROUND_COLOR_KEY, color)
}

fun LoadOptions.Builder.svgBackgroundColor(@ColorInt color: Int) = apply {
    setParameter(SVG_BACKGROUND_COLOR_KEY, color)
}

fun DisplayRequest.Builder.svgBackgroundColor(@ColorInt color: Int) = apply {
    setParameter(SVG_BACKGROUND_COLOR_KEY, color)
}

fun DisplayOptions.Builder.svgBackgroundColor(@ColorInt color: Int) = apply {
    setParameter(SVG_BACKGROUND_COLOR_KEY, color)
}

val LoadRequest.svgBackgroundColor: Int?
    get() = parameters?.value<Int>(SVG_BACKGROUND_COLOR_KEY)

val LoadOptions.svgBackgroundColor: Int?
    get() = parameters?.value<Int>(SVG_BACKGROUND_COLOR_KEY)