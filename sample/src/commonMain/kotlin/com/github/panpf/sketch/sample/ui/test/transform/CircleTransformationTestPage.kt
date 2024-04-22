package com.github.panpf.sketch.sample.ui.test.transform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.transform.CircleCropTransformation

@Composable
fun CircleTransformationTestPage() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        var scale by remember { mutableStateOf(Scale.CENTER_CROP) }
        MyAsyncImage(
            request = ImageRequest(LocalPlatformContext.current, MyImages.jpeg.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(CircleCropTransformation(scale))
            },
            contentDescription = "image",
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.size(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = scale == Scale.START_CROP,
                onClick = { scale = Scale.START_CROP },
            )
            Text(text = "START", fontSize = 12.sp)

            Spacer(Modifier.size(4.dp))
            RadioButton(
                selected = scale == Scale.CENTER_CROP,
                onClick = { scale = Scale.CENTER_CROP },
            )
            Text(text = "CENTER", fontSize = 12.sp)

            Spacer(Modifier.size(4.dp))
            RadioButton(
                selected = scale == Scale.END_CROP,
                onClick = { scale = Scale.END_CROP },
            )
            Text(text = "END", fontSize = 12.sp)

            Spacer(Modifier.size(4.dp))
            RadioButton(
                selected = scale == Scale.FILL,
                onClick = { scale = Scale.FILL },
            )
            Text(text = "FILL", fontSize = 12.sp)
        }
    }
}