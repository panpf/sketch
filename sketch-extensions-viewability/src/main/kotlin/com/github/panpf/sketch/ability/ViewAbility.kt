/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
 * ViewAbility represents the extension function of a View.
 *
 * After defining your ViewAbility, attach the ViewAbility to any View that implements the ViewAbilityContainer
 * interface through the addViewAbility() method of ViewAbilityContainer.
 *
 * You can also implement the ViewObserver interface to listen to various events of View and observe various properties to help implement functions
 */
interface ViewAbility {

    /**
     * Attached host. Not null means running
     */
    var host: Host?
}