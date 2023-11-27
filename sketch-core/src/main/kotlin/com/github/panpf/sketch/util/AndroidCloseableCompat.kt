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
package com.github.panpf.sketch.util

import android.content.res.AssetFileDescriptor
import android.os.Build
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <R> AssetFileDescriptor.useCompat(block: (AssetFileDescriptor) -> R): R {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        this.use(block)
    } else {
        try {
            return block(this)
        } catch (e: Throwable) {
            throw e
        } finally {
            try {
                close()
            } catch (closeException: Throwable) {
                // cause.addSuppressed(closeException) // ignored here
            }
        }
    }
}

///**
// * Constant check of api version used during compilation
// *
// * This function is evaluated at compile time to a constant value,
// * so there should be no references to it in other modules.
// *
// * The function usages are validated to have literal argument values.
// */
//@PublishedApi
//@SinceKotlin("1.2")
//internal fun apiVersionIsAtLeast(major: Int, minor: Int, patch: Int) =
//    KotlinVersion.CURRENT.isAtLeast(major, minor, patch)