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

package com.github.panpf.sketch.sample.ui.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

abstract class BasePermissionFragment : BaseFragment() {

    open val permission: String? = null
    open val permissionRequired: Boolean = false
    private var savedInstanceState: Bundle? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result || !permissionRequired) {
                onPermissionPassed(requireView(), savedInstanceState)
            } else {
                showPermissionDeniedDialog()
            }
        }

    final override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        val permission = permission
        if (permission == null || checkPermission(permission)) {
            this@BasePermissionFragment.savedInstanceState = null
            onPermissionPassed(view, savedInstanceState)
        } else {
            this@BasePermissionFragment.savedInstanceState = savedInstanceState
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun checkPermission(permission: String?): Boolean {
        if (permission == null) return true
        val checkGranted = ContextCompat.checkSelfPermission(requireContext(), permission)
        return checkGranted == PackageManager.PERMISSION_GRANTED
    }

    abstract fun onPermissionPassed(
        view: View,
        savedInstanceState: Bundle?
    )

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireActivity()).apply {
            setTitle("Error")
            setMessage("The current page must be granted '$permission' permission before it can be used normally. Please grant permission again.")
            setPositiveButton("OK") { _, _ -> permissionLauncher.launch(permission) }
        }.show()
    }
}