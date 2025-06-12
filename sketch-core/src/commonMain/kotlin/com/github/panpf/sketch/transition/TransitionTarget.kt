/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

package com.github.panpf.sketch.transition

import com.github.panpf.sketch.target.Target

/**
 * A [Target] that supports applying [Transition]s.
 */
@Deprecated("Please use ComposeTarget or ViewTarget instead and will be deleted in the future")
interface TransitionTarget : Target {
    @Deprecated("Please use ComposeTarget.contentScale or ViewTarget.scaleType instead and will be deleted in the future")
    val fitScale: Boolean
}