package com.github.panpf.sketch.sample.ui.common.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.ui.common.compose.LoadResult.ERROR

@Composable
fun LoadState(result: LoadResult, modifier: Modifier = Modifier) {
    if (result == ERROR) {
        Column(
            modifier = modifier
                .size(200.dp)
                .background(Color(0xEE2E2E2E), RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = "icon",
                tint = Color.White
            )

            Spacer(modifier = Modifier.size(6.dp))
            Text(text = "Display failure", color = Color.White)

            Spacer(modifier = Modifier.size(24.dp))
            Button(
                onClick = {
                    // todo Retry cannot be implemented because AsyncImage does not support
                },
                shape = RoundedCornerShape(50)
            ) {
                Text(text = "Retry")
            }
        }
    }
}

@Preview
@Composable
fun LoadStatePreview() {
    LoadState(result = ERROR)
}

enum class LoadResult {
    INITIALIZED,
    LOADING,
    ERROR,
    SUCCESS
}