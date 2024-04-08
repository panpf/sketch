package com.github.panpf.sketch.sample.ui.common.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppendState {
    LOADING,
    ERROR,
    END,
}

@Composable
fun AppendState(state: AppendState, onClick: () -> Unit) {
    val message: String
    var click: (() -> Unit)? = null
    when(state) {
        AppendState.LOADING -> {
            message = "LOADING..."
        }

        AppendState.ERROR -> {
            message = "LOAD ERROR"
            click = onClick
        }

        AppendState.END -> {
            message = "THE END"
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
            textAlign = TextAlign.Center
        )
    }
}