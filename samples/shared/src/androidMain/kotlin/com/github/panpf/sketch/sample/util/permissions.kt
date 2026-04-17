package com.github.panpf.sketch.sample.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.ACTION_REQUEST_PERMISSIONS
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.panpf.sketch.sample.util.WithDataActivityResultContracts.RequestPermission.Input

/**
 * {@inheritDoc}
 *
 *
 *
 * If the host of this fragment is an [ActivityResultRegistryOwner] the
 * [ActivityResultRegistry] of the host will be used. Otherwise, this will use the
 * registry of the Fragment's Activity.
 */
@MainThread
fun <I, O> Fragment.registerForActivityResult(
    contract: WithDataActivityResultContracts.WithDataActivityResultContract<I, O>,
): ActivityResultLauncher<I> {
    return registerForActivityResult(contract) {
        contract.onActivityResult(it)
    }
}

@Suppress("UnnecessaryVariable")
object WithDataActivityResultContracts {

    abstract class WithDataActivityResultContract<I, O> : ActivityResultContract<I, O>(),
        ActivityResultCallback<O>

    class RequestPermission : WithDataActivityResultContract<Input, Boolean>() {

        private var input: Input? = null

        override fun createIntent(context: Context, input: Input): Intent {
            this.input = input
            return Intent(ACTION_REQUEST_PERMISSIONS).apply {
                putExtra(RequestMultiplePermissions.EXTRA_PERMISSIONS, arrayOf(input.permission))
            }
        }

        override fun onActivityResult(result: Boolean) {
            val input = input!!
            input.onCallback(result)
        }

        @Suppress("AutoBoxing")
        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
            if (intent == null || resultCode != Activity.RESULT_OK) {
                return false
            }
            val grantResults =
                intent.getIntArrayExtra(RequestMultiplePermissions.EXTRA_PERMISSION_GRANT_RESULTS)
            val grant =
                grantResults?.any { result -> result == PackageManager.PERMISSION_GRANTED } == true
            return grant
        }

        override fun getSynchronousResult(
            context: Context,
            input: Input
        ): SynchronousResult<Boolean>? {
            this.input = input
            val granted = ContextCompat.checkSelfPermission(
                context,
                input.permission
            ) == PackageManager.PERMISSION_GRANTED
            return if (granted) {
                SynchronousResult(true)
            } else {
                // proceed with permission request
                null
            }
        }

        data class Input(val permission: String, val onCallback: (Boolean) -> Unit)
    }
}
