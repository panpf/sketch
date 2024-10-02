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

package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.DrawableResource

/**
 * Set the state image when the save cellular traffic
 *
 * @see com.github.panpf.sketch.extensions.compose.resources.common.test.SaveCellularTrafficExtensionsComposeResourcesTest.testSaveCellularTrafficError
 */
@Composable
fun ConditionStateImage.Builder.saveCellularTrafficError(
    resource: DrawableResource
): ConditionStateImage.Builder = apply {
    addState(SaveCellularTrafficCondition, resource)
}