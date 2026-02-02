package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.sample.ui.util.toFormattedString

@Composable
fun PhotoInfo(imageResult: ImageResult?) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val uri: String? = imageResult?.request?.uri?.toString()
        PhotoInfoItem(null, uri.orEmpty())

        if (imageResult is ImageResult.Success) {
            val optionsInfo = imageResult.cacheKey
                .replace(imageResult.request.uri.toString(), "")
                .let { if (it.startsWith("?")) it.substring(1) else it }
                .split("&")
                .filter { it.trim().isNotEmpty() }
                .joinToString(separator = "\n")
            PhotoInfoItem("Options: ", optionsInfo)

            val sourceImageInfo = remember {
                imageResult.imageInfo.run {
                    "${width}x${height}, ${mimeType}"
                }
            }
            PhotoInfoItem("Source Image: ", sourceImageInfo)

            val resultImageInfo = imageResult.image.toFormattedString()
            PhotoInfoItem("Result Image: ", resultImageInfo)

            val resize = imageResult.resize
            PhotoInfoItem("Resize: ", resize.toString())

            val dataFromInfo = imageResult.dataFrom.name
            PhotoInfoItem("Data From: ", dataFromInfo)

            val transformedInfo = imageResult.transformeds
                ?.joinToString(separator = "\n") { transformed ->
                    transformed.replace("Transformed", "")
                }
            PhotoInfoItem("Transformeds: ", transformedInfo.orEmpty())
        } else if (imageResult is ImageResult.Error) {
            val throwableString = imageResult.throwable.toString()
            PhotoInfoItem("Throwable: ", throwableString)
        }
    }
}

@Composable
fun PhotoInfoItem(title: String? = null, content: String) {
    Column {
        if (title != null) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Text(
            text = content,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        )
    }
}