package com.github.panpf.sketch.sample.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Stable
sealed interface AppendState {

    @Stable
    data object Loading : AppendState

    @Stable
    data class Error(val retry: (() -> Unit)? = null) : AppendState

    @Stable
    data object End : AppendState
}

@Composable
fun AppendState(state: AppendState?) {
    when (state) {
        is AppendState.Loading -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(Modifier.size(20.dp))
                Spacer(Modifier.size(10.dp))
                Text(
                    text = "LOADING...",
                    fontSize = 12.sp,
                )
            }
        }

        is AppendState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clickable { state.retry?.invoke() },
                contentAlignment = Alignment.Center,
            ) {
                if (state.retry != null) {
                    Text(
                        text = "LOAD ERROR. CLICK RETRY!",
                        fontSize = 12.sp,
                    )
                } else {
                    Text(
                        text = "LOAD ERROR",
                        fontSize = 12.sp,
                    )
                }
            }
        }

        is AppendState.End -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "THE END",
                    fontSize = 12.sp,
                )
            }
        }

        else -> {

        }
    }
}