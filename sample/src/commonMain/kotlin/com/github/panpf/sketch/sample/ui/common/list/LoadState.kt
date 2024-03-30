@file:OptIn(ExperimentalResourceApi::class)

package com.github.panpf.sketch.sample.ui.common.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.compose.AsyncImageState
import com.github.panpf.sketch.request.LoadState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import sketch.sample.generated.resources.Res.drawable
import sketch.sample.generated.resources.ic_error_baseline

@Composable
fun LoadState(imageState: AsyncImageState, modifier: Modifier = Modifier) {
    if (imageState.loadState is LoadState.Error) {
        Column(
            modifier = modifier
                .size(200.dp)
                .background(Color(0xEE2E2E2E), RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(drawable.ic_error_baseline),
                contentDescription = "icon",
                tint = Color.White
            )

            Spacer(modifier = Modifier.size(6.dp))
            Text(text = "Display failure", color = Color.White)

            Spacer(modifier = Modifier.size(24.dp))
            Button(
                onClick = {
                    imageState.restart()
                },
                shape = RoundedCornerShape(50)
            ) {
                Text(text = "Retry")
            }
        }
    }
}

//@Preview
//@Composable
//fun LoadStatePreview() {
//    LoadState(rememberAsyncImageState())
//}