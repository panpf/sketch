@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.panpf.sketch.sample.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.ui.theme.DarkModeSwitch

@Composable
actual fun VerHomeHeader() {
    Box(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Text(text = "Sketch4")
            },
        )

        val top = with(LocalDensity.current) { TopAppBarDefaults.windowInsets.getTop(this).toDp() }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(top = top)
                .height(50.dp)
                .padding(start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            DarkModeSwitch()
        }
    }
}