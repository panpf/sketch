package com.github.panpf.sketch.sample.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.sample.appSettings

@Composable
@OptIn(ExperimentalMaterial3Api::class)
actual fun VerHomeHeader() {
    Box(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Column {
                    Text(text = "Sketch4")
                    Text(text = "Compose", fontSize = 15.sp)
                }
            },
        )

        val appSettings = LocalContext.current.appSettings
        val top = with(LocalDensity.current) { TopAppBarDefaults.windowInsets.getTop(this).toDp() }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(top = top)
                .height(50.dp)
                .clickable { appSettings.composePage.value = false }
                .padding(start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = com.github.panpf.sketch.sample.R.drawable.ic_android),
                contentDescription = "Android Page",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}