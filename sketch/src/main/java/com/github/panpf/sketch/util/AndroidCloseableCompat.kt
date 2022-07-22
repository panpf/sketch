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