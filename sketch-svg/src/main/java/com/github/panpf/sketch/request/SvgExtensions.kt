package com.github.panpf.sketch.request

import androidx.annotation.ColorInt

private const val SVG_BACKGROUND_COLOR_KEY = "sketch#svg_background_color"

/**
 * Set the background color of the SVG image, the default is transparent
 */
fun ImageRequest.Builder.svgBackgroundColor(@ColorInt color: Int): ImageRequest.Builder = apply {
    setParameter(SVG_BACKGROUND_COLOR_KEY, color)
}

/**
 * Set the background color of the SVG image, the default is transparent
 */
fun DisplayRequest.Builder.svgBackgroundColor(@ColorInt color: Int): DisplayRequest.Builder =
    apply {
        setParameter(SVG_BACKGROUND_COLOR_KEY, color)
    }

/**
 * Set the background color of the SVG image, the default is transparent
 */
fun LoadRequest.Builder.svgBackgroundColor(@ColorInt color: Int): LoadRequest.Builder = apply {
    setParameter(SVG_BACKGROUND_COLOR_KEY, color)
}

/**
 * Get the background color of the SVG image
 */
val ImageRequest.svgBackgroundColor: Int?
    get() = parameters?.value<Int>(SVG_BACKGROUND_COLOR_KEY)

/**
 * Set the background color of the SVG image, the default is transparent
 */
fun ImageOptions.Builder.svgBackgroundColor(@ColorInt color: Int) = apply {
    setParameter(SVG_BACKGROUND_COLOR_KEY, color)
}

/**
 * Get the background color of the SVG image
 */
val ImageOptions.svgBackgroundColor: Int?
    get() = parameters?.value<Int>(SVG_BACKGROUND_COLOR_KEY)