/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.request

private const val SVG_BACKGROUND_COLOR_KEY = "sketch#svg_background_color"
private const val SVG_CSS_KEY = "sketch#svg_css"

/**
 * Set the background color of the SVG image, the default is transparent
 *
 * @see com.github.panpf.sketch.svg.common.test.request.SvgExtensionsTest.testSvgBackgroundColor
 */
fun ImageRequest.Builder.svgBackgroundColor(color: Int?): ImageRequest.Builder = apply {
    if (color != null) {
        setExtra(key = SVG_BACKGROUND_COLOR_KEY, value = color)
    } else {
        removeExtra(SVG_BACKGROUND_COLOR_KEY)
    }
}

/**
 * Get the background color of the SVG image
 *
 * @see com.github.panpf.sketch.svg.common.test.request.SvgExtensionsTest.testSvgBackgroundColor
 */
val ImageRequest.svgBackgroundColor: Int?
    get() = extras?.value<Int>(SVG_BACKGROUND_COLOR_KEY)

/**
 * Set the background color of the SVG image, the default is transparent
 *
 * @see com.github.panpf.sketch.svg.common.test.request.SvgExtensionsTest.testSvgBackgroundColor
 */
fun ImageOptions.Builder.svgBackgroundColor(color: Int?) = apply {
    if (color != null) {
        setExtra(key = SVG_BACKGROUND_COLOR_KEY, value = color)
    } else {
        removeExtra(SVG_BACKGROUND_COLOR_KEY)
    }
}

/**
 * Get the background color of the SVG image
 *
 * @see com.github.panpf.sketch.svg.common.test.request.SvgExtensionsTest.testSvgBackgroundColor
 */
val ImageOptions.svgBackgroundColor: Int?
    get() = extras?.value<Int>(SVG_BACKGROUND_COLOR_KEY)


/**
 * Set the background color of the SVG image, the default is transparent
 *
 * @see com.github.panpf.sketch.svg.common.test.request.SvgExtensionsTest.testSvgCss
 */
fun ImageRequest.Builder.svgCss(css: String?): ImageRequest.Builder = apply {
    if (css != null) {
        setExtra(key = SVG_CSS_KEY, value = css)
    } else {
        removeExtra(SVG_CSS_KEY)
    }
}

/**
 * Get the background color of the SVG image
 *
 * @see com.github.panpf.sketch.svg.common.test.request.SvgExtensionsTest.testSvgCss
 */
val ImageRequest.svgCss: String?
    get() = extras?.value<String>(SVG_CSS_KEY)

/**
 * Set the background color of the SVG image, the default is transparent
 *
 * @see com.github.panpf.sketch.svg.common.test.request.SvgExtensionsTest.testSvgCss
 */
fun ImageOptions.Builder.svgCss(css: String?) = apply {
    if (css != null) {
        setExtra(key = SVG_CSS_KEY, value = css)
    } else {
        removeExtra(SVG_CSS_KEY)
    }
}

/**
 * Get the background color of the SVG image
 *
 * @see com.github.panpf.sketch.svg.common.test.request.SvgExtensionsTest.testSvgCss
 */
val ImageOptions.svgCss: String?
    get() = extras?.value<String>(SVG_CSS_KEY)