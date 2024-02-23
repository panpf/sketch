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

/**
 * A class which allows custom selection of colors in a [Palette]'s generation. Instances
 * can be created via the [Builder] class.
 *
 * To use the target, use the [Palette.Builder.addTarget] API when building a
 * Palette.
 */
@Suppress("MemberVisibilityCanBePrivate")
public class Target {

    public val saturationTargets: FloatArray = FloatArray(3)
    public val lightnessTargets: FloatArray = FloatArray(3)
    public val weights: FloatArray = FloatArray(3)

    /**
     * Returns whether any color selected for this target is exclusive for this target only.
     *
     * If false, then the color can be selected for other targets.
     */
    public var isExclusive: Boolean = true

    internal constructor() {
        setTargetDefaultValues(saturationTargets)
        setTargetDefaultValues(lightnessTargets)
        setDefaultWeights()
    }

    @Suppress("unused")
    internal constructor(from: Target) {
        from.saturationTargets.copyInto(saturationTargets)
        from.lightnessTargets.copyInto(lightnessTargets)
        from.weights.copyInto(weights)
    }

    /**
     * The minimum saturation value for this target.
     *
     * Valid range is 0..1
     */
    public val minimumSaturation: Float
        get() = saturationTargets[INDEX_MIN]

    /**
     * The target saturation value for this target.
     *
     * Valid range is 0..1
     */
    public val targetSaturation: Float
        get() = saturationTargets[INDEX_TARGET]

    /**
     * The maximum saturation value for this target.
     *
     * Valid Range is 0..1
     */
    public val maximumSaturation: Float
        get() = saturationTargets[INDEX_MAX]

    /**
     * The minimum lightness value for this target.
     *
     * Valid Range is 0..1
     */
    public val minimumLightness: Float
        get() = lightnessTargets[INDEX_MIN]

    /**
     * The target lightness value for this target.
     *
     * Valid Range is 0..1
     */
    public val targetLightness: Float
        get() = lightnessTargets[INDEX_TARGET]

    /**
     * The maximum lightness value for this target.
     *
     * Valid Range is 0..1
     */
    public val maximumLightness: Float
        get() = lightnessTargets[INDEX_MAX]

    /**
     * Returns the weight of importance that this target places on a color's saturation within
     * the image.
     *
     *
     * The larger the weight, relative to the other weights, the more important that a color
     * being close to the target value has on selection.
     *
     * @see targetSaturation
     */
    public val saturationWeight: Float
        get() = weights[INDEX_WEIGHT_SAT]

    /**
     * Returns the weight of importance that this target places on a color's lightness within
     * the image.
     *
     *
     * The larger the weight, relative to the other weights, the more important that a color
     * being close to the target value has on selection.
     *
     * @see targetLightness
     */
    public val lightnessWeight: Float
        get() = weights[INDEX_WEIGHT_LUMA]

    /**
     * Returns the weight of importance that this target places on a color's population within
     * the image.
     *
     *
     * The larger the weight, relative to the other weights, the more important that a
     * color's population being close to the most populous has on selection.
     */
    public val populationWeight: Float
        get() = weights[INDEX_WEIGHT_POP]

    private fun setDefaultWeights() {
        weights[INDEX_WEIGHT_SAT] = WEIGHT_SATURATION
        weights[INDEX_WEIGHT_LUMA] = WEIGHT_LUMA
        weights[INDEX_WEIGHT_POP] = WEIGHT_POPULATION
    }

    public fun normalizeWeights() {
        var sum = 0f
        var index = 0
        val z = weights.size
        while (index < z) {
            val weight = weights[index]
            if (weight > 0) {
                sum += weight
            }
            index++
        }

        if (sum != 0f) {
            index = 0
            while (index < z) {
                if (weights[index] > 0) {
                    weights[index] /= sum
                }
                index++
            }
        }
    }

    /**
     * Builder class for generating custom [Target] instances.
     */
    public class Builder(
        private val target: Target = Target(),
    ) {

        /**
         * Set the minimum saturation value for this target.
         *
         * Valid range is 0..1
         */
        public fun setMinimumSaturation(value: Float): Builder = apply {
            require(value in 0.0..1.0) { "Minimum saturation value must be between 0..1" }
            target.saturationTargets[INDEX_MIN] = value
        }

        /**
         * Set the target/ideal saturation value for this target.
         *
         * Valid range is 0..1
         */
        public fun setTargetSaturation(value: Float): Builder = apply {
            require(value in 0.0..1.0) { "Maximum saturation value must be between 0..1" }
            target.saturationTargets[INDEX_TARGET] = value
        }

        /**
         * Set the maximum saturation value for this target.
         *
         * Valid range is 0..1
         */
        public fun setMaximumSaturation(value: Float): Builder = apply {
            require(value in 0.0..1.0) { "Saturation value must be between 0..1" }
            target.saturationTargets[INDEX_MAX] = value
        }

        /**
         * Set the minimum lightness value for this target.
         *
         * Valid range is 0..1
         */
        public fun setMinimumLightness(value: Float): Builder = apply {
            require(value in 0.0..1.0) { "Min lightness value must be in the range 0..1" }
            target.lightnessTargets[INDEX_MIN] = value
        }

        /**
         * Set the target/ideal lightness value for this target.
         *
         * Valid range is 0..1
         */
        public fun setTargetLightness(value: Float): Builder = apply {
            require(value in 0.0..1.0) { "Target lightness value must be in the range 0..1" }
            target.lightnessTargets[INDEX_TARGET] = value
        }

        /**
         * Set the maximum lightness value for this target.
         *
         * Valid range is 0..1
         */
        public fun setMaximumLightness(value: Float): Builder = apply {
            require(value in 0.0..1.0) { "Max lightness value must be in the range 0..1" }
            target.lightnessTargets[INDEX_MAX] = value
        }

        /**
         * Set the weight of importance that this target will place on saturation values.
         *
         *
         * The larger the weight, relative to the other weights, the more important that a color
         * being close to the target value has on selection.
         *
         *
         * A weight of 0 means that it has no weight, and thus has no
         * bearing on the selection.
         *
         * @see setTargetSaturation
         */
        public fun setSaturationWeight(weight: Float): Builder = apply {
            require(weight >= 0) { "Saturation weight must be non negative" }
            target.weights[INDEX_WEIGHT_SAT] = weight
        }

        /**
         * Set the weight of importance that this target will place on lightness values.
         *
         *
         * The larger the weight, relative to the other weights, the more important that a color
         * being close to the target value has on selection.
         *
         *
         * A weight of 0 means that it has no weight, and thus has no
         * bearing on the selection.
         *
         * @see .setTargetLightness
         */
        public fun setLightnessWeight(weight: Float): Builder = apply {
            require(weight >= 0) { "Lightness weight must be non negative" }
            target.weights[INDEX_WEIGHT_LUMA] = weight
        }

        /**
         * Set the weight of importance that this target will place on a color's population within
         * the image.
         *
         *
         * The larger the weight, relative to the other weights, the more important that a
         * color's population being close to the most populous has on selection.
         *
         *
         * A weight of 0 means that it has no weight, and thus has no
         * bearing on the selection.
         */
        public fun setPopulationWeight(weight: Float): Builder = apply {
            require(weight >= 0) { "Population weight must be non negative" }
            target.weights[INDEX_WEIGHT_POP] = weight
        }

        /**
         * Set whether any color selected for this target is exclusive to this target only.
         * Defaults to true.
         *
         * @param exclusive true if any the color is exclusive to this target, or false is the
         * color can be selected for other targets.
         */
        public fun setExclusive(exclusive: Boolean): Builder = apply {
            target.isExclusive = exclusive
        }

        /**
         * Builds and returns the resulting [Target].
         */
        public fun build(): Target = target
    }

    public companion object {

        private const val TARGET_DARK_LUMA = 0.26f
        private const val MAX_DARK_LUMA = 0.45f
        private const val MIN_LIGHT_LUMA = 0.55f
        private const val TARGET_LIGHT_LUMA = 0.74f
        private const val MIN_NORMAL_LUMA = 0.3f
        private const val TARGET_NORMAL_LUMA = 0.5f
        private const val MAX_NORMAL_LUMA = 0.7f
        private const val TARGET_MUTED_SATURATION = 0.3f
        private const val MAX_MUTED_SATURATION = 0.4f
        private const val TARGET_VIBRANT_SATURATION = 1f
        private const val MIN_VIBRANT_SATURATION = 0.35f
        private const val WEIGHT_SATURATION = 0.24f
        private const val WEIGHT_LUMA = 0.52f
        private const val WEIGHT_POPULATION = 0.24f

        internal const val INDEX_MIN = 0
        internal const val INDEX_TARGET = 1
        internal const val INDEX_MAX = 2
        internal const val INDEX_WEIGHT_SAT = 0
        internal const val INDEX_WEIGHT_LUMA = 1
        internal const val INDEX_WEIGHT_POP = 2

        /**
         * A target which has the characteristics of a vibrant color which is light in luminance.
         */
        public val LIGHT_VIBRANT: Target = Target().apply {
            setDefaultLightLightnessValues()
            setDefaultVibrantSaturationValues()
        }

        /**
         * A target which has the characteristics of a vibrant color which is neither light or dark.
         */
        public val VIBRANT: Target = Target().apply {
            setDefaultNormalLightnessValues()
            setDefaultVibrantSaturationValues()
        }

        /**
         * A target which has the characteristics of a vibrant color which is dark in luminance.
         */
        public val DARK_VIBRANT: Target = Target().apply {
            setDefaultDarkLightnessValues()
            setDefaultVibrantSaturationValues()
        }

        /**
         * A target which has the characteristics of a muted color which is light in luminance.
         */
        public val LIGHT_MUTED: Target = Target().apply {
            setDefaultLightLightnessValues()
            setDefaultMutedSaturationValues()
        }

        /**
         * A target which has the characteristics of a muted color which is neither light or dark.
         */
        public val MUTED: Target = Target().apply {
            setDefaultNormalLightnessValues()
            setDefaultMutedSaturationValues()
        }

        /**
         * A target which has the characteristics of a muted color which is dark in luminance.
         */
        public val DARK_MUTED: Target = Target().apply {
            setDefaultDarkLightnessValues()
            setDefaultMutedSaturationValues()
        }

        private fun setTargetDefaultValues(values: FloatArray) {
            values[INDEX_MIN] = 0f
            values[INDEX_TARGET] = 0.5f
            values[INDEX_MAX] = 1f
        }

        private fun Target.setDefaultDarkLightnessValues() {
            lightnessTargets[INDEX_TARGET] = TARGET_DARK_LUMA
            lightnessTargets[INDEX_MAX] = MAX_DARK_LUMA
        }

        private fun Target.setDefaultNormalLightnessValues() {
            lightnessTargets[INDEX_MIN] = MIN_NORMAL_LUMA
            lightnessTargets[INDEX_TARGET] = TARGET_NORMAL_LUMA
            lightnessTargets[INDEX_MAX] = MAX_NORMAL_LUMA
        }

        private fun Target.setDefaultLightLightnessValues() {
            lightnessTargets[INDEX_MIN] = MIN_LIGHT_LUMA
            lightnessTargets[INDEX_TARGET] = TARGET_LIGHT_LUMA
        }

        private fun Target.setDefaultVibrantSaturationValues() {
            saturationTargets[INDEX_MIN] = MIN_VIBRANT_SATURATION
            saturationTargets[INDEX_TARGET] = TARGET_VIBRANT_SATURATION
        }

        private fun Target.setDefaultMutedSaturationValues() {
            saturationTargets[INDEX_TARGET] = TARGET_MUTED_SATURATION
            saturationTargets[INDEX_MAX] = MAX_MUTED_SATURATION
        }
    }
}
