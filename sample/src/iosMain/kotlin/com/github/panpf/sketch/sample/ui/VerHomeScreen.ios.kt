@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.panpf.sketch.sample.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun VerHomeHeader() {
    Box(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Text(text = "Sketch4")
            },
        )
    }
}