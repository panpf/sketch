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
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.NavDirections
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.databinding.DialogImageInfoBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingDialogFragment
import com.github.panpf.tools4k.lang.asOrThrow

class PhotoInfoDialogFragment : BaseBindingDialogFragment<DialogImageInfoBinding>() {

    private val args by navArgs<PhotoInfoDialogFragmentArgs>()

    override fun onViewCreated(binding: DialogImageInfoBinding, savedInstanceState: Bundle?) {
        binding.uriText.text = args.uri
        binding.optionsText.text = args.optionsInfo

        binding.throwableText.text = args.throwableString
        binding.imageInfoText.text = args.imageInfo
        binding.bitmapInfoText.text = args.bitmapInfo
        binding.drawableInfoText.text = args.drawableInfo
        binding.dataFromText.text = args.dataFromInfo
        binding.transformedText.text = args.transformedInfo

        binding.throwableText.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString != null
        binding.imageInfoText.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString == null
        binding.bitmapInfoText.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString == null
        binding.drawableInfoText.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString == null
        binding.dataFromText.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString == null
        binding.transformedText.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString == null
    }

    companion object {

        fun createNavDirections(imageResult: ImageResult?): NavDirections {
            val uri: String? = imageResult?.request?.uriString
            var optionsInfo: String? = null
            var imageInfo: String? = null
            var bitmapInfo: String? = null
            var drawableInfo: String? = null
            var dataFromInfo: String? = null
            var transformedInfo: String? = null
            var throwableString: String? = null
            if (imageResult is ImageResult.Success) {
                imageInfo = imageResult.imageInfo.run {
                    "${width}x${height}, ${mimeType}, ${ExifOrientation.name(exifOrientation)}"
                }

                optionsInfo = imageResult.cacheKey
                    .replace(imageResult.request.uriString, "")
                    .let { if (it.startsWith("?")) it.substring(1) else it }
                    .split("&")
                    .filter { it.trim().isNotEmpty() }
                    .joinToString(separator = "\n")

                bitmapInfo = imageResult.image.toString()

                drawableInfo = imageResult.image.let {
                    "${it.width}x${it.height}"
                }

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
                imageInfo = imageInfo,
                bitmapInfo = bitmapInfo,
                drawableInfo = drawableInfo,
                optionsInfo = optionsInfo,
                dataFromInfo = dataFromInfo,
                transformedInfo = transformedInfo,
                throwableString = throwableString
            )
        }
    }
}