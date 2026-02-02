package com.kmpalette.palette.internal.utils

import com.kmpalette.palette.internal.annotation.ColorInt
import com.kmpalette.palette.internal.annotation.FloatRange
import com.kmpalette.palette.internal.annotation.IntRange
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

//@ThreadLocal
//private var TEMP_ARRAY: DoubleArray = DoubleArray(3)

@OptIn(ExperimentalStdlibApi::class)
internal object ColorUtils {

    private const val MIN_ALPHA_SEARCH_MAX_ITERATIONS = 10
    private const val MIN_ALPHA_SEARCH_PRECISION = 1

    @ColorInt
    val BLACK = -0x1000000
    @ColorInt
    val WHITE = -0x1

    /**
     * Convert RGB components to HSL (hue-saturation-lightness).
     *
     *  * outHsl[0] is Hue [0, 360)
     *  * outHsl[1] is Saturation [0, 1]
     *  * outHsl[2] is Lightness [0, 1]
     *
     *
     * @param red red component value [0, 255]
     * @param green green component value [0, 255]
     * @param blue blue component value [0, 255]
     * @param outHsl 3-element array which holds the resulting HSL components
     */
    fun convertRGBToHSL(
        red: Int,
        green: Int,
        blue: Int,
        outHsl: FloatArray,
    ) {
        val rf = red / 255f
        val gf = green / 255f
        val bf = blue / 255f
        val max: Float = max(rf, max(gf, bf))
        val min: Float = min(rf, min(gf, bf))
        val deltaMaxMin = max - min
        var hue: Float
        val saturation: Float
        val lightness = (max + min) / 2f
        if (max == min) {
            // Monochromatic
            saturation = 0f
            hue = saturation
        } else {
            hue = when (max) {
                rf -> (gf - bf) / deltaMaxMin % 6f
                gf -> (bf - rf) / deltaMaxMin + 2f
                else -> (rf - gf) / deltaMaxMin + 4f
            }
            saturation = deltaMaxMin / (1f - abs(2f * lightness - 1f))
        }
        hue = hue * 60f % 360f
        if (hue < 0) {
            hue += 360f
        }
        outHsl[0] = hue.coerceIn(0f, 360f)
        outHsl[1] = saturation.coerceIn(0f, 1f)
        outHsl[2] = lightness.coerceIn(0f, 1f)
    }

    @ColorInt
    fun setAlpha(
        @ColorInt color: Int,
        @IntRange(from = 0x0, to = 0xFF) alpha: Int,
    ): Int {
        if (alpha < 0 || alpha > 255) {
            throw IllegalArgumentException("alpha must be between 0 and 255.")
        }
        return color and 0x00ffffff or (alpha shl 24)
    }

    @FloatRange(from = 0.0, to = 1.0)
    fun calculateLuminance(@ColorInt color: Int): Double {
        val result: DoubleArray = DoubleArray(3)
        colorToXYZ(color, result)
        // Luminance is the Y component
        return result[1] / 100
    }

    private fun compositeColors(@ColorInt foreground: Int, @ColorInt background: Int): Int {
        val bgAlpha: Int = alpha(background)
        val fgAlpha: Int = alpha(foreground)
        val a: Int = compositeAlpha(fgAlpha, bgAlpha)
        val r: Int = compositeComponent(red(foreground), fgAlpha, red(background), bgAlpha, a)
        val g: Int = compositeComponent(green(foreground), fgAlpha, green(background), bgAlpha, a)
        val b: Int = compositeComponent(blue(foreground), fgAlpha, blue(background), bgAlpha, a)
        return argb(a, r, g, b)
    }

    private fun compositeAlpha(foregroundAlpha: Int, backgroundAlpha: Int): Int {
        return 0xFF - (0xFF - backgroundAlpha) * (0xFF - foregroundAlpha) / 0xFF
    }

    private fun compositeComponent(fgC: Int, fgA: Int, bgC: Int, bgA: Int, a: Int): Int {
        return if (a == 0) 0 else (0xFF * fgC * fgA + bgC * bgA * (0xFF - fgA)) / (a * 0xFF)
    }

    /**
     * Convert RGB components to its CIE XYZ representative components.
     *
     * The resulting XYZ representation will use the D65 illuminant and the CIE
     * 2° Standard Observer (1931).
     *
     *  * outXyz[0] is X [0, 95.047)
     *  * outXyz[1] is Y [0, 100)
     *  * outXyz[2] is Z [0, 108.883)
     *
     *
     * @param r red component value [0, 255]
     * @param g green component value [0, 255]
     * @param b blue component value [0, 255]
     * @param outXyz 3-element array which holds the resulting XYZ components
     */
    private fun convertRGBToXYZ(
        @IntRange(from = 0x0, to = 0xFF) r: Int,
        @IntRange(from = 0x0, to = 0xFF) g: Int,
        @IntRange(from = 0x0, to = 0xFF) b: Int,
        outXyz: DoubleArray,
    ) {
        if (outXyz.size != 3) {
            throw IllegalArgumentException("outXyz must have a length of 3.")
        }
        var sr = r / 255.0
        sr = if (sr < 0.04045) sr / 12.92 else pow((sr + 0.055) / 1.055, 2.4)
        var sg = g / 255.0
        sg = if (sg < 0.04045) sg / 12.92 else pow((sg + 0.055) / 1.055, 2.4)
        var sb = b / 255.0
        sb = if (sb < 0.04045) sb / 12.92 else pow((sb + 0.055) / 1.055, 2.4)
        outXyz[0] = 100 * (sr * 0.4124 + sg * 0.3576 + sb * 0.1805)
        outXyz[1] = 100 * (sr * 0.2126 + sg * 0.7152 + sb * 0.0722)
        outXyz[2] = 100 * (sr * 0.0193 + sg * 0.1192 + sb * 0.9505)
    }

    /**
     * Convert the ARGB color to its CIE XYZ representative components.
     *
     *
     * The resulting XYZ representation will use the D65 illuminant and the CIE
     * 2° Standard Observer (1931).
     *
     *
     *  * outXyz[0] is X [0, 95.047)
     *  * outXyz[1] is Y [0, 100)
     *  * outXyz[2] is Z [0, 108.883)
     *
     *
     * @param color the ARGB color to convert. The alpha component is ignored
     * @param outXyz 3-element array which holds the resulting LAB components
     */
    private fun colorToXYZ(@ColorInt color: Int, outXyz: DoubleArray) {
        convertRGBToXYZ(red(color), green(color), blue(color), outXyz)
    }

    private fun calculateContrast(@ColorInt foreground: Int, @ColorInt background: Int): Double {
        val modifiedForeground = if (alpha(foreground) >= 255) foreground
        else {
            // If the foreground is translucent, composite the foreground over the background
            compositeColors(foreground, background)
        }

        val luminance1: Double = calculateLuminance(modifiedForeground) + 0.05
        val luminance2: Double = calculateLuminance(background) + 0.05

        // Now return the lighter luminance divided by the darker luminance
        return max(luminance1, luminance2) / min(luminance1, luminance2)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun calculateMinimumAlpha(
        @ColorInt foreground: Int,
        @ColorInt background: Int,
        minContrastRatio: Float,
    ): Int {
        if (alpha(background) != 255) {
            throw IllegalArgumentException(
                "background can not be translucent: #${background.toHexString()}"
            )
        }

        // First lets check that a fully opaque foreground has sufficient contrast
        var testForeground: Int = setAlpha(foreground, 255)
        var testRatio: Double = calculateContrast(testForeground, background)
        if (testRatio < minContrastRatio) {
            // Fully opaque foreground does not have sufficient contrast, return error
            return -1
        }

        // Binary search to find a value with the minimum value which provides sufficient contrast
        var numIterations = 0
        var minAlpha = 0
        var maxAlpha = 255
        while (
            numIterations <= MIN_ALPHA_SEARCH_MAX_ITERATIONS
            && maxAlpha - minAlpha > MIN_ALPHA_SEARCH_PRECISION
        ) {
            val testAlpha = (minAlpha + maxAlpha) / 2
            testForeground = setAlpha(foreground, testAlpha)
            testRatio = calculateContrast(testForeground, background)
            if (testRatio < minContrastRatio) minAlpha = testAlpha
            else maxAlpha = testAlpha

            numIterations++
        }

        // Conservatively return the max of the range of possible alphas, which is known to pass.
        return maxAlpha
    }

    fun colorToHSL(@ColorInt color: Int, outHsl: FloatArray) {
        convertRGBToHSL(red(color), green(color), blue(color), outHsl)
    }

    /**
     * Return the alpha component of a color int. This is the same as saying
     * color >>> 24
     */
    fun alpha(@ColorInt color: Int): Int {
        return color ushr 24
    }

    /**
     * Return the red component of a color int. This is the same as saying
     * (color >> 16) & 0xFF
     */
    fun red(@ColorInt color: Int): Int {
        return color shr 16 and 0xFF
    }

    /**
     * Return the green component of a color int. This is the same as saying
     * (color >> 8) & 0xFF
     */
    fun green(@ColorInt color: Int): Int {
        return color shr 8 and 0xFF
    }

    /**
     * Return the blue component of a color int. This is the same as saying
     * color & 0xFF
     */
    fun blue(@ColorInt color: Int): Int {
        return color and 0xFF
    }

    /**
     * Return a color-int from alpha, red, green, blue components.
     * These component values should be [0..255], but there is no
     * range check performed, so if they are out of range, the
     * returned color is undefined.
     *
     * @param alpha Alpha component [0..255] of the color
     * @param red Red component [0..255] of the color
     * @param green Green component [0..255] of the color
     * @param blue Blue component [0..255] of the color
     */
    @ColorInt
    fun argb(
        alpha: Int,
        red: Int,
        green: Int,
        blue: Int,
    ): Int {
        return alpha shl 24 or (red shl 16) or (green shl 8) or blue
    }

    /**
     * Return a color-int from red, green, blue components.
     * The alpha component is implicitly 255 (fully opaque).
     * These component values should be [0..255], but there is no
     * range check performed, so if they are out of range, the
     * returned color is undefined.
     *
     * @param red  Red component [0..255] of the color
     * @param green Green component [0..255] of the color
     * @param blue  Blue component [0..255] of the color
     */
    @ColorInt
    fun rgb(
        red: Int,
        green: Int,
        blue: Int,
    ): Int {
        return -0x1000000 or (red shl 16) or (green shl 8) or blue
    }
}