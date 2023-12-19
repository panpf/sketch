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
package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.fragment.app.commit
import com.github.panpf.sketch.displayAssetImage
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.databinding.TestShareElement1FragmentBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import java.util.concurrent.TimeUnit.MILLISECONDS

class ShareElementTestFragment : BaseBindingFragment<TestShareElement1FragmentBinding>() {

    override fun onViewCreated(
        binding: TestShareElement1FragmentBinding,
        savedInstanceState: Bundle?
    ) {
        binding.testShareElement1Image.apply {
            ViewCompat.setTransitionName(this, "transition_app_icon")
            Log.i("ShareElementTest", "$id. displayImage")
            postponeEnterTransition(100, MILLISECONDS)
            displayAssetImage(AssetImages.jpeg) {
                listener(
                    onSuccess = { _, _ ->
                        startPostponedEnterTransition()
                    },
                    onError = { _, _ ->
                        startPostponedEnterTransition()
                    }
                )
            }
            setOnClickListener {
                requireParentFragment().childFragmentManager.commit {
                    setReorderingAllowed(true)
                    addSharedElement(binding.testShareElement1Image, "transition_app_icon")
                    replace(this@ShareElementTestFragment.id, ShareElement2TestFragment())
                    addToBackStack(null)
                }
            }
        }
    }
}