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
package com.github.panpf.sketch.sample.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.NavDirections
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.databinding.DialogImageInfoBinding
import com.github.panpf.sketch.sample.databinding.ListItemImageInfoBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingDialogFragment
import com.github.panpf.sketch.sample.ui.util.toStringFormat

class PhotoInfoDialogFragment : BaseBindingDialogFragment<DialogImageInfoBinding>() {

    private val args by navArgs<PhotoInfoDialogFragmentArgs>()

    override fun onViewCreated(binding: DialogImageInfoBinding, savedInstanceState: Bundle?) {

        createItem(binding, null, args.uri.orEmpty())
            .apply { binding.contentLayout.addView(this) }
        createItem(binding, "Options: ", args.optionsInfo.orEmpty())
            .apply { binding.contentLayout.addView(this) }

        if (args.throwableString == null) {
            createItem(binding, "Source Image: ", args.sourceImageInfo.orEmpty())
                .apply { binding.contentLayout.addView(this) }

            createItem(binding, "Result Image: ", args.resultImageInfo.orEmpty())
                .apply { binding.contentLayout.addView(this) }

            createItem(binding, "Data From: ", args.dataFromInfo.orEmpty())
                .apply { binding.contentLayout.addView(this) }

            createItem(binding, "Transformed: ", args.transformedInfo.orEmpty())
                .apply { binding.contentLayout.addView(this) }
        } else {
            createItem(binding, "Throwable: ", args.throwableString.orEmpty())
                .apply { binding.contentLayout.addView(this) }
        }
    }

    private fun createItem(
        binding: DialogImageInfoBinding,
        title: String? = null,
        content: String
    ): View {
        return ListItemImageInfoBinding.inflate(
            LayoutInflater.from(binding.root.context),
            binding.contentLayout,
            false
        ).apply {
            this.titleText.apply {
                text = title
                isVisible = title != null
            }
            this.contentText.text = content
        }.root
    }

    companion object {

        fun createNavDirections(imageResult: ImageResult?): NavDirections {
            val uri: String? = imageResult?.request?.uriString
            var optionsInfo: String? = null
            var sourceImageInfo: String? = null
            var resultImageInfo: String? = null
            var dataFromInfo: String? = null
            var transformedInfo: String? = null
            var throwableString: String? = null
            if (imageResult is ImageResult.Success) {
                sourceImageInfo = imageResult.imageInfo.run {
                    "${width}x${height}, ${mimeType}"
                }

                optionsInfo = imageResult.cacheKey
                    .replace(imageResult.request.uriString, "")
                    .let { if (it.startsWith("?")) it.substring(1) else it }
                    .split("&")
                    .filter { it.trim().isNotEmpty() }
                    .joinToString(separator = "\n")

                resultImageInfo = imageResult.image.toStringFormat()

                dataFromInfo = imageResult.dataFrom.name

                transformedInfo = imageResult.transformedList
                    ?.joinToString(separator = "\n") { transformed ->
                        transformed.replace("Transformed", "")
                    }
            } else if (imageResult is ImageResult.Error) {
                throwableString = imageResult.throwable.toString()
            }

            return NavMainDirections.actionPhotoInfoDialogFragment(
                uri = uri,
                sourceImageInfo = sourceImageInfo,
                resultImageInfo = resultImageInfo,
                optionsInfo = optionsInfo,
                dataFromInfo = dataFromInfo,
                transformedInfo = transformedInfo,
                throwableString = throwableString
            )
        }
    }
}