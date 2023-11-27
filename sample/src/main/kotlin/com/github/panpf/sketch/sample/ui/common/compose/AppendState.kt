package com.github.panpf.sketch.sample.ui.common.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import com.github.panpf.sketch.sample.R

@Composable
fun AppendState(loadState: LoadState, onClick: () -> Unit) {
    val message: String
    var click: (() -> Unit)? = null
    when (loadState) {
        is LoadState.Loading -> {
            message = stringResource(R.string.text_loading)
        }
        is LoadState.Error -> {
            message = stringResource(R.string.text_load_error)
            click = onClick
        }
        is LoadState.NotLoading -> {
            message = if (loadState.endOfPaginationReached) {
                stringResource(R.string.text_load_end)
            } else {
                stringResource(R.string.text_loading)
            }
        }
        else -> {
            message = "Unknown"
        }
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clickable { click?.invoke() }
    ) {
        Text(
            text = message,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 12.sp,
            color = colorResource(R.color.text_normal),
            textAlign = TextAlign.Center
        )
    }
}