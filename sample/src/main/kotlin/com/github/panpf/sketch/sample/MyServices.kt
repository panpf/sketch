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
package com.github.panpf.sketch.sample

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.github.panpf.sketch.sample.data.api.ApiServices
import com.github.panpf.sketch.sample.util.ParamLazy

object MyServices {
    val apiServiceLazy = ParamLazy<Context, ApiServices> { ApiServices(it) }
    val prefsServiceLazy = ParamLazy<Context, PrefsService> { PrefsService(it) }
    val eventServiceLazy = ParamLazy<Context, EventService> { EventService() }
}

val Context.apiService: ApiServices
    get() = MyServices.apiServiceLazy.get(this.applicationContext)
val Fragment.apiService: ApiServices
    get() = MyServices.apiServiceLazy.get(this.requireContext().applicationContext)
val View.apiService: ApiServices
    get() = MyServices.apiServiceLazy.get(this.context.applicationContext)

val Context.prefsService: PrefsService
    get() = MyServices.prefsServiceLazy.get(this.applicationContext)
val Fragment.prefsService: PrefsService
    get() = MyServices.prefsServiceLazy.get(this.requireContext().applicationContext)
val View.prefsService: PrefsService
    get() = MyServices.prefsServiceLazy.get(this.context.applicationContext)

val Context.eventService: EventService
    get() = MyServices.eventServiceLazy.get(this.applicationContext)
val Fragment.eventService: EventService
    get() = MyServices.eventServiceLazy.get(this.requireContext().applicationContext)
val View.eventService: EventService
    get() = MyServices.eventServiceLazy.get(this.context.applicationContext)