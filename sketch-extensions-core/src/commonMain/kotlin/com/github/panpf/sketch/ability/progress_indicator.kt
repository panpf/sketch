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

package com.github.panpf.sketch.ability

/**
 * Progress indicator configuration constants
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.ability.ProgressIndicatorTest.testConsts
 */

const val PROGRESS_INDICATOR_STEP_ANIMATION_DURATION: Int = 150
const val PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE: Boolean = false
const val PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED: Boolean = true

const val PROGRESS_INDICATOR_MASK_COLOR: Int = 0x22000000

const val PROGRESS_INDICATOR_RING_COLOR: Int = 0xFFFFFFFF.toInt()
const val PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT: Float = 0.25f
const val PROGRESS_INDICATOR_RING_SIZE: Float = 50f
const val PROGRESS_INDICATOR_RING_WIDTH_PERCENT: Float = 0.1f

const val PROGRESS_INDICATOR_SECTOR_SIZE: Float = 50f
const val PROGRESS_INDICATOR_SECTOR_BACKGROUND_COLOR: Int = 0x44000000
const val PROGRESS_INDICATOR_SECTOR_PROGRESS_COLOR: Int = 0xFFFFFFFF.toInt()
const val PROGRESS_INDICATOR_SECTOR_STROKE_COLOR: Int = 0xFFFFFFFF.toInt()
const val PROGRESS_INDICATOR_SECTOR_STROKE_WIDTH_PERCENT: Float = 0.02f