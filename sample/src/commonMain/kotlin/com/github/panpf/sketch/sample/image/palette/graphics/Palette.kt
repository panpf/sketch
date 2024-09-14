/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kmpalette.palette.graphics

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.height
import com.github.panpf.sketch.util.readIntPixels
import com.github.panpf.sketch.util.scale
import com.github.panpf.sketch.width
import com.kmpalette.palette.graphics.Palette.Builder
import com.kmpalette.palette.internal.ColorCutQuantizer
import com.kmpalette.palette.internal.annotation.ColorInt
import com.kmpalette.palette.internal.utils.ColorUtils
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * A helper class to extract prominent colors from an image.
 *
 * A number of colors with different profiles are extracted from the image:
 *
 *  * Vibrant
 *  * Vibrant Dark
 *  * Vibrant Light
 *  * Muted
 *  * Muted Dark
 *  * Muted Light
 *
 * These can be retrieved from the appropriate getter method.
 *
 * Instances are created with a [Builder] which supports several options to tweak the
 * generated Palette. See that class' documentation for more information.
 *
 *
 * Generation should always be completed on a background thread, ideally the one in
 * which you load your image on. [Builder] supports both synchronous and asynchronous
 * generation:
 *
 * ```
 * Palette p = Palette.from(bitmap).generate();
 * ```
 *
 * Copy from https://github.com/jordond/kmpalette/blob/main/androidx-palette/src/commonMain/kotlin/com/kmpalette/palette/graphics/Palette.kt
 * https://developer.android.com/develop/ui/views/graphics/palette-colors
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class Palette internal constructor(
    /**
     * Returns the dominant swatch from the palette.
     *
     *
     * The dominant swatch is defined as the swatch with the greatest population (frequency)
     * within the palette.
     */
    val swatches: List<Swatch>,

    /**
     * Returns all of the swatches which make up the palette.
     */
    private val targets: List<Target>,
) {

    private val selectedSwatches: HashMap<Target, Swatch?> = HashMap()
    private val usedColors: HashMap<Int, Boolean> = HashMap()

    val dominantSwatch: Swatch? = findDominantSwatch()

    /**
     * Returns the most vibrant swatch in the palette. Might be null.
     *
     * @see Target.VIBRANT
     */
    val vibrantSwatch: Swatch?
        get() = getSwatchForTarget(Target.VIBRANT)

    /**
     * Returns a light and vibrant swatch from the palette. Might be null.
     *
     * @see Target.LIGHT_VIBRANT
     */
    val lightVibrantSwatch: Swatch?
        get() = getSwatchForTarget(Target.LIGHT_VIBRANT)

    /**
     * Returns a dark and vibrant swatch from the palette. Might be null.
     *
     * @see Target.DARK_VIBRANT
     */
    val darkVibrantSwatch: Swatch?
        get() = getSwatchForTarget(Target.DARK_VIBRANT)

    /**
     * Returns a muted swatch from the palette. Might be null.
     *
     * @see Target.MUTED
     */
    val mutedSwatch: Swatch?
        get() = getSwatchForTarget(Target.MUTED)

    /**
     * Returns a muted and light swatch from the palette. Might be null.
     *
     * @see Target.LIGHT_MUTED
     */
    val lightMutedSwatch: Swatch?
        get() = getSwatchForTarget(Target.LIGHT_MUTED)

    /**
     * Returns a muted and dark swatch from the palette. Might be null.
     *
     * @see Target.DARK_MUTED
     */
    val darkMutedSwatch: Swatch?
        get() = getSwatchForTarget(Target.DARK_MUTED)

    /**
     * Returns the most vibrant color in the palette as an RGB packed int.
     *
     * @param defaultColor value to return if the swatch isn't available
     * @see .getVibrantSwatch
     */
    @ColorInt
    fun getVibrantColor(@ColorInt defaultColor: Int): Int {
        return getColorForTarget(Target.VIBRANT, defaultColor)
    }

    /**
     * Returns a light and vibrant color from the palette as an RGB packed int.
     *
     * @param defaultColor value to return if the swatch isn't available
     * @see .getLightVibrantSwatch
     */
    @ColorInt
    fun getLightVibrantColor(@ColorInt defaultColor: Int): Int {
        return getColorForTarget(Target.LIGHT_VIBRANT, defaultColor)
    }

    /**
     * Returns a dark and vibrant color from the palette as an RGB packed int.
     *
     * @param defaultColor value to return if the swatch isn't available
     * @see .getDarkVibrantSwatch
     */
    @ColorInt
    fun getDarkVibrantColor(@ColorInt defaultColor: Int): Int {
        return getColorForTarget(Target.DARK_VIBRANT, defaultColor)
    }

    /**
     * Returns a muted color from the palette as an RGB packed int.
     *
     * @param defaultColor value to return if the swatch isn't available
     * @see .getMutedSwatch
     */
    @ColorInt
    fun getMutedColor(@ColorInt defaultColor: Int): Int {
        return getColorForTarget(Target.MUTED, defaultColor)
    }

    /**
     * Returns a muted and light color from the palette as an RGB packed int.
     *
     * @param defaultColor value to return if the swatch isn't available
     * @see .getLightMutedSwatch
     */
    @ColorInt
    fun getLightMutedColor(@ColorInt defaultColor: Int): Int {
        return getColorForTarget(Target.LIGHT_MUTED, defaultColor)
    }

    /**
     * Returns a muted and dark color from the palette as an RGB packed int.
     *
     * @param defaultColor value to return if the swatch isn't available
     * @see .getDarkMutedSwatch
     */
    @ColorInt
    fun getDarkMutedColor(@ColorInt defaultColor: Int): Int {
        return getColorForTarget(Target.DARK_MUTED, defaultColor)
    }

    /**
     * Returns the selected swatch for the given target from the palette, or `null` if one
     * could not be found.
     */
    fun getSwatchForTarget(target: Target): Swatch? {
        return selectedSwatches[target]
    }

    /**
     * Returns the selected color for the given target from the palette as an RGB packed int.
     *
     * @param defaultColor value to return if the swatch isn't available
     */
    @ColorInt
    fun getColorForTarget(target: Target, @ColorInt defaultColor: Int): Int {
        val swatch = getSwatchForTarget(target)
        return swatch?.rgb ?: defaultColor
    }

    /**
     * Returns the color of the dominant swatch from the palette, as an RGB packed int.
     *
     * @param defaultColor value to return if the swatch isn't available
     * @see .getDominantSwatch
     */
    @ColorInt
    fun getDominantColor(@ColorInt defaultColor: Int): Int {
        return dominantSwatch?.rgb ?: defaultColor
    }

    fun generate() {
        // We need to make sure that the scored targets are generated first. This is so that
        // inherited targets have something to inherit from
        targets.forEach { target ->
            target.normalizeWeights()
            selectedSwatches[target] = generateScoredTarget(target)
        }

        // We now clear out the used colors
        usedColors.clear()
    }

    private fun generateScoredTarget(target: Target): Swatch? {
        val maxScoreSwatch = getMaxScoredSwatchForTarget(target)
        if (maxScoreSwatch != null && target.isExclusive) {
            // If we have a swatch, and the target is exclusive, add the color to the used list
            usedColors[maxScoreSwatch.rgb] = true
        }
        return maxScoreSwatch
    }

    private fun getMaxScoredSwatchForTarget(target: Target): Swatch? {
        var maxScore = 0f
        var maxScoreSwatch: Swatch? = null
        var i = 0
        val count = swatches.size
        while (i < count) {
            val swatch = swatches[i]
            if (shouldBeScoredForTarget(swatch, target)) {
                val score = generateScore(swatch, target)
                if (maxScoreSwatch == null || score > maxScore) {
                    maxScoreSwatch = swatch
                    maxScore = score
                }
            }
            i++
        }
        return maxScoreSwatch
    }

    private fun shouldBeScoredForTarget(swatch: Swatch, target: Target): Boolean {
        // Check whether the HSL values are within the correct ranges, and this color hasn't
        // been used yet.
        val hsl = swatch.hsl
        return hsl[1] >= target.minimumSaturation
                && hsl[1] <= target.maximumSaturation
                && hsl[2] >= target.minimumLightness
                && hsl[2] <= target.maximumLightness
                && usedColors[swatch.rgb]?.not() ?: true
    }

    private fun generateScore(swatch: Swatch, target: Target): Float {
        val hsl = swatch.hsl
        var saturationScore = 0f
        var luminanceScore = 0f
        var populationScore = 0f
        val maxPopulation = dominantSwatch?.population ?: 1
        if (target.saturationWeight > 0) {
            saturationScore = (target.saturationWeight
                    * (1f - abs(hsl[1] - target.targetSaturation)))
        }
        if (target.lightnessWeight > 0) {
            luminanceScore = (target.lightnessWeight
                    * (1f - abs(hsl[2] - target.targetLightness)))
        }
        if (target.populationWeight > 0) {
            populationScore = (target.populationWeight
                    * (swatch.population / maxPopulation.toFloat()))
        }
        return saturationScore + luminanceScore + populationScore
    }

    private fun findDominantSwatch(): Swatch? {
        return swatches.maxByOrNull { it.population }
    }

    /**
     * Represents a color swatch generated from an image's palette. The RGB color can be retrieved
     * by calling [rgb].
     */
    class Swatch(
        /**
         * @return this swatch's RGB color value
         */
        @get:ColorInt val rgb: Int,

        /**
         * @return the number of pixels represented by this swatch
         */
        val population: Int,
    ) {

        private val red: Int = ColorUtils.red(rgb)
        private val green: Int = ColorUtils.green(rgb)
        private val blue: Int = ColorUtils.blue(rgb)

        private var generatedTextColors = false
        private var _titleTextColor = 0
        private var _bodyTextColor = 0

        /**
         * Return this swatch's HSL values.
         * hsv[0] is Hue [0 .. 360)
         * hsv[1] is Saturation [0...1]
         * hsv[2] is Lightness [0...1]
         */
        var hsl: FloatArray = FloatArray(3)
            .apply { ColorUtils.convertRGBToHSL(red, green, blue, this) }
            private set

        /**
         * Returns an appropriate color to use for any 'title' text which is displayed over this
         * [Swatch]'s color. This color is guaranteed to have sufficient contrast.
         */
        @get:ColorInt
        val titleTextColor: Int
            get() {
                ensureTextColorsGenerated()
                return _titleTextColor
            }

        /**
         * Returns an appropriate color to use for any 'body' text which is displayed over this
         * [Swatch]'s color. This color is guaranteed to have sufficient contrast.
         */
        @get:ColorInt
        val bodyTextColor: Int
            get() {
                ensureTextColorsGenerated()
                return _bodyTextColor
            }

        private fun ensureTextColorsGenerated() {
            if (!generatedTextColors) {
                // First check white, as most colors will be dark
                val lightBodyAlpha: Int = ColorUtils.calculateMinimumAlpha(
                    ColorUtils.WHITE, rgb, MIN_CONTRAST_BODY_TEXT
                )
                val lightTitleAlpha: Int = ColorUtils.calculateMinimumAlpha(
                    ColorUtils.WHITE, rgb, MIN_CONTRAST_TITLE_TEXT
                )

                if (lightBodyAlpha != -1 && lightTitleAlpha != -1) {
                    // If we found valid light values, use them and return
                    _bodyTextColor = ColorUtils.setAlpha(ColorUtils.WHITE, lightBodyAlpha)
                    _titleTextColor = ColorUtils.setAlpha(ColorUtils.WHITE, lightTitleAlpha)
                    generatedTextColors = true
                    return
                }

                val darkBodyAlpha: Int = ColorUtils.calculateMinimumAlpha(
                    ColorUtils.BLACK, rgb, MIN_CONTRAST_BODY_TEXT
                )
                val darkTitleAlpha: Int = ColorUtils.calculateMinimumAlpha(
                    ColorUtils.BLACK, rgb, MIN_CONTRAST_TITLE_TEXT
                )
                if (darkBodyAlpha != -1 && darkTitleAlpha != -1) {
                    // If we found valid dark values, use them and return
                    _bodyTextColor = ColorUtils.setAlpha(ColorUtils.BLACK, darkBodyAlpha)
                    _titleTextColor = ColorUtils.setAlpha(ColorUtils.BLACK, darkTitleAlpha)
                    generatedTextColors = true
                    return
                }

                // If we reach here then we can not find title and body values which use the same
                // lightness, we need to use mismatched values
                _bodyTextColor =
                    if (lightBodyAlpha != -1) ColorUtils.setAlpha(ColorUtils.WHITE, lightBodyAlpha)
                    else ColorUtils.setAlpha(ColorUtils.BLACK, darkBodyAlpha)

                _titleTextColor =
                    if (lightTitleAlpha != -1) ColorUtils.setAlpha(
                        ColorUtils.WHITE,
                        lightTitleAlpha
                    )
                    else ColorUtils.setAlpha(ColorUtils.BLACK, darkTitleAlpha)

                generatedTextColors = true
            }
        }
    }

    /**
     * Builder class for generating [Palette] instances.
     */
    class Builder {

        private val swatches: List<Swatch>?
        private val imageBitmap: Bitmap?
        private val targets: MutableList<Target> = mutableListOf()
        private var maxColors = DEFAULT_CALCULATE_NUMBER_COLORS
        private var resizeArea = DEFAULT_RESIZE_BITMAP_AREA
        private var resizeMaxDimension = -1
        private val filters: MutableList<Filter> = mutableListOf()
        private var region: Rect? = null

        /**
         * Construct a new [Builder] using a source [ImageBitmap]
         */
        constructor(bitmap: Bitmap) {
            filters.add(DEFAULT_FILTER)
            imageBitmap = bitmap
            swatches = null

            // Add the default targets
            targets.add(Target.LIGHT_VIBRANT)
            targets.add(Target.VIBRANT)
            targets.add(Target.DARK_VIBRANT)
            targets.add(Target.LIGHT_MUTED)
            targets.add(Target.MUTED)
            targets.add(Target.DARK_MUTED)
        }

        /**
         * Construct a new [Builder] using a list of [Swatch] instances.
         * Typically only used for testing.
         */
        constructor(swatches: List<Swatch>) {
            if (swatches.isEmpty()) {
                throw IllegalArgumentException("List of Swatches is not valid")
            }
            filters.add(DEFAULT_FILTER)
            this.swatches = swatches
            imageBitmap = null
        }

        /**
         * Set the maximum number of colors to use in the quantization step when using a
         * [ImageBitmap] as the source.
         *
         *
         * Good values for depend on the source image type. For landscapes, good values are in
         * the range 10-16. For images which are largely made up of people's faces then this
         * value should be increased to ~24.
         */
        fun maximumColorCount(colors: Int): Builder {
            maxColors = colors
            return this
        }

        /**
         * Set the resize value when using a [ImageBitmap] as the source.
         * If the bitmap's area is greater than the value specified, then the bitmap
         * will be resized so that its area matches `area`. If the
         * bitmap is smaller or equal, the original is used as-is.
         *
         *
         * This value has a large effect on the processing time. The larger the resized image is,
         * the greater time it will take to generate the palette. The smaller the image is, the
         * more detail is lost in the resulting image and thus less precision for color selection.
         *
         * @param area the number of pixels that the intermediary scaled down Bitmap should cover,
         * or any value <= 0 to disable resizing.
         */
        fun resizeBitmapArea(area: Int): Builder {
            resizeArea = area
            resizeMaxDimension = -1
            return this
        }

        /**
         * Clear all added filters. This includes any default filters added automatically by
         * [Palette].
         */
        fun clearFilters(): Builder {
            filters.clear()
            return this
        }

        /**
         * Add a filter to be able to have fine grained control over which colors are
         * allowed in the resulting palette.
         *
         * @param filter filter to add.
         */
        fun addFilter(filter: Filter): Builder {
            filters.add(filter)
            return this
        }

        /**
         * Set a region of the bitmap to be used exclusively when calculating the palette.
         *
         * This only works when the original input is a [ImageBitmap].
         *
         * @param left The left side of the rectangle used for the region.
         * @param top The top of the rectangle used for the region.
         * @param right The right side of the rectangle used for the region.
         * @param bottom The bottom of the rectangle used for the region.
         */
        fun setRegion(left: Int, top: Int, right: Int, bottom: Int): Builder {
            val bitmap = imageBitmap
            if (bitmap != null) {
                if (region == null) {
                    region = Rect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
                }

                // Now just get the intersection with the region
                val other = Rect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
                if (!region!!.overlaps(other)) {
                    throw IllegalArgumentException(
                        "The given region must intersect with "
                                + "the Bitmap's dimensions."
                    )
                }
            }
            return this
        }

        /**
         * Clear any previously region set via [.setRegion].
         */
        fun clearRegion(): Builder {
            region = null
            return this
        }

        /**
         * Add a target profile to be generated in the palette.
         *
         *
         * You can retrieve the result via [Palette.getSwatchForTarget].
         */
        fun addTarget(target: Target): Builder {
            if (!targets.contains(target)) {
                targets.add(target)
            }
            return this
        }

        /**
         * Clear all added targets. This includes any default targets added automatically by
         * [Palette].
         */
        fun clearTargets(): Builder {
            targets.clear()
            return this
        }

        /**
         * Generate and return the [Palette] synchronously.
         */
        fun generate(): Palette {
            val swatches: List<Swatch>
            val imageBitmap = imageBitmap
            if (imageBitmap != null) {
                // We have a Bitmap so we need to use quantization to reduce the number of colors
                // First we'll scale down the bitmap if needed
                val bitmap: Bitmap = scaleBitmapDown(imageBitmap)
                val region: Rect? = region
                if (bitmap != imageBitmap && region != null) {
                    // If we have a scaled bitmap and a selected region, we need to scale down the
                    // region to match the new scale
                    val scale: Float = bitmap.width / imageBitmap.width.toFloat()
                    this.region = region.copy(
                        left = floor(region.left * scale),
                        top = floor(region.top * scale),
                        right = min(ceil(region.right * scale), bitmap.width.toFloat()),
                        bottom = min(ceil(region.bottom * scale), bitmap.height.toFloat()),
                    )
                }

                // Now generate a quantizer from the Bitmap
                val quantizer = ColorCutQuantizer(
                    pixels = getPixelsFromBitmap(bitmap),
                    maxColors = maxColors,
                    filters = if (filters.isEmpty()) null else filters.toTypedArray(),
                )
                swatches = quantizer.quantizedColors
            } else if (this.swatches != null) {
                // Else we're using the provided swatches
                swatches = this.swatches
            } else {
                // The constructors enforce either a bitmap or swatches are present.
                throw AssertionError()
            }

            // Now create a Palette instance
            val p = Palette(swatches, targets)
            // And make it generate itself
            p.generate()
            return p
        }

        private fun getPixelsFromBitmap(bitmap: Bitmap): IntArray {
            val bitmapWidth: Int = bitmap.width
//            val bitmapHeight: Int = bitmap.height
            val pixels = requireNotNull(bitmap.readIntPixels())

            val region = region
            return if (region == null) {
                // If we don't have a region, return all of the pixels
                pixels
            } else {
                // If we do have a region, lets create a subset array containing only the region's
                // pixels
                val regionWidth = region.width
                val regionHeight = region.height
                // pixels contains all of the pixels, so we need to iterate through each row and
                // copy the regions pixels into a new smaller array
                val subsetPixels = IntArray((regionWidth * regionHeight).toInt())
                for (row in 0 until regionHeight.toInt()) {
                    pixels.copyInto(
                        destination = subsetPixels,
                        destinationOffset = (row * regionWidth).toInt(),
                        startIndex = ((row + region.top) * bitmapWidth + region.left).toInt(),
                        endIndex = ((row + region.top) * bitmapWidth + region.left + regionWidth).toInt()
                    )
                }
                subsetPixels
            }
        }

        /**
         * Scale the bitmap down as needed.
         */
        private fun scaleBitmapDown(bitmap: Bitmap): Bitmap {
            var scaleRatio = -1.0
            if (resizeArea > 0) {
                val bitmapArea: Int = bitmap.width * bitmap.height
                if (bitmapArea > resizeArea) {
                    scaleRatio = sqrt(resizeArea / bitmapArea.toDouble())
                }
            } else if (resizeMaxDimension > 0) {
                val maxDimension: Int = max(bitmap.width, bitmap.height)
                if (maxDimension > resizeMaxDimension) {
                    scaleRatio = resizeMaxDimension / maxDimension.toDouble()
                }
            }
            return if (scaleRatio <= 0) {
                // Scaling has been disabled or not needed so just return the Bitmap
                bitmap
            } else {
                bitmap.scale(scaleRatio.toFloat())
            }
        }
    }

    /**
     * A Filter provides a mechanism for exercising fine-grained control over which colors
     * are valid within a resulting [Palette].
     */
    fun interface Filter {

        /**
         * Hook to allow clients to be able filter colors from resulting palette.
         *
         * @param rgb the color in RGB888.
         * @param hsl HSL representation of the color.
         *
         * @return true if the color is allowed, false if not.
         *
         * @see Builder.addFilter
         */
        fun isAllowed(rgb: Int, hsl: FloatArray): Boolean
    }

    companion object {

        const val DEFAULT_RESIZE_BITMAP_AREA: Int = 112 * 112
        const val DEFAULT_CALCULATE_NUMBER_COLORS: Int = 16
        const val MIN_CONTRAST_TITLE_TEXT: Float = 3.0f
        const val MIN_CONTRAST_BODY_TEXT: Float = 4.5f

        /**
         * Start generating a [Palette] with the returned [Builder] instance.
         */
        fun from(bitmap: Bitmap): Builder {
            return Builder(bitmap)
        }

        /**
         * Generate a [Palette] from the pre-generated list of [Palette.Swatch] swatches.
         * This is useful for testing, or if you want to resurrect a [Palette] instance from a
         * list of swatches. Will return null if the `swatches` is null.
         */
        fun from(swatches: List<Swatch>): Palette {
            return Builder(swatches).generate()
        }

        /**
         * The default filter.
         */
        val DEFAULT_FILTER: Filter = object : Filter {
            private val BLACK_MAX_LIGHTNESS = 0.05f
            private val WHITE_MIN_LIGHTNESS = 0.95f
            override fun isAllowed(rgb: Int, hsl: FloatArray): Boolean {
                return !isWhite(hsl) && !isBlack(hsl) && !isNearRedILine(hsl)
            }

            /**
             * @return true if the color represents a color which is close to black.
             */
            private fun isBlack(hslColor: FloatArray): Boolean {
                return hslColor[2] <= BLACK_MAX_LIGHTNESS
            }

            /**
             * @return true if the color represents a color which is close to white.
             */
            private fun isWhite(hslColor: FloatArray): Boolean {
                return hslColor[2] >= WHITE_MIN_LIGHTNESS
            }

            /**
             * @return true if the color lies close to the red side of the I line.
             */
            private fun isNearRedILine(hslColor: FloatArray): Boolean {
                return hslColor[0] in 10f..37f && hslColor[1] <= 0.82f
            }
        }
    }
}
