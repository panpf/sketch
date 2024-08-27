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
package com.github.panpf.sketch.sample.ui.base

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.github.panpf.sketch.sample.databinding.FragmentContainerBinding

class PermissionContainerFragment : BaseBindingFragment<FragmentContainerBinding>() {

    private val fragmentClass by lazy { Class.forName(requireArguments().getString("fragmentClassName")!!) }
    private val fragmentArguments by lazy { requireArguments().getBundle("fragmentArguments") }
    private val permission by lazy { requireArguments().getString("permission")!! }
    private val permissionRequired by lazy { requireArguments().getBoolean("permissionRequired") }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result || !permissionRequired) {
                startFragment()
            } else {
                showPermissionDeniedDialog()
            }
        }

    override fun onViewCreated(
        binding: FragmentContainerBinding,
        savedInstanceState: Bundle?
    ) {
        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun startFragment() {
        val fragment = fragmentClass.getDeclaredConstructor().newInstance() as Fragment
        fragment.arguments = fragmentArguments
        childFragmentManager.beginTransaction()
            .replace(binding!!.fragmentContainer.id, fragment)
            .commit()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireActivity()).apply {
            setTitle("Error")
            setMessage("The current page must be granted '$permission' permission before it can be used normally. Please grant permission again.")
            setPositiveButton("OK") { _, _ -> permissionLauncher.launch(permission) }
        }.show()
    }

    companion object {
        fun newInstance(
            fragment: Fragment,
            permission: String,
            permissionRequired: Boolean
        ): PermissionContainerFragment {
            return PermissionContainerFragment().apply {
                arguments = Bundle().apply {
                    putString("permission", permission)
                    putBoolean("permissionRequired", permissionRequired)
                    putString("fragmentClassName", fragment::class.java.name)
                    putBundle("fragmentArguments", fragment.arguments)
                }
            }
        }
    }
}