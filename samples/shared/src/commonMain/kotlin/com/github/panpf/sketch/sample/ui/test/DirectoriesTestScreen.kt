package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.Res
import com.github.panpf.sketch.sample.ic_copy
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.util.copyToClipboard
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DirectoriesTestScreen() {
    ToolbarScaffold(title = "Directories Test") {
        val viewModel = koinViewModel<DirectoriesTestViewModel>()
        val dataList by viewModel.data.collectAsState()
        val windowsInfo = LocalWindowInfo.current
        val isLandscape = windowsInfo.containerSize.width > windowsInfo.containerSize.height
        val columns = if (isLandscape) {
            GridCells.Adaptive(400.dp)
        } else {
            GridCells.Fixed(1)
        }
        val itemMinHeight = if (isLandscape) 200.dp else 50.dp
        LazyVerticalGrid(
            columns = columns,
            modifier = Modifier.fillMaxSize()
        ) {
            items(dataList.size) { item ->
                val data = dataList[item]
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    DirectoryItem(data, Modifier.heightIn(min = itemMinHeight))
                }
            }
        }
    }
}

@Composable
fun DirectoryItem(data: DirectoryItem, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val appEvents = koinInject<AppEvents>()
    val context = LocalPlatformContext.current
    Box(Modifier.fillMaxWidth()) {
        Card(
            modifier.fillMaxWidth()
                .clip(shape = CardDefaults.shape)
                .clickable {
                    val path = data.path
                    if (path != null) {
                        copyToClipboard(context, path)
                        scope.launch {
                            appEvents.toastFlow.emit("Copied: '$path'")
                        }
                    }
                }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 52.dp),
            ) {
                Text(text = "${data.name}: ", fontSize = 14.sp)
                Text(
                    text = data.path.orEmpty(),
                    fontSize = 16.sp,
                )
            }
        }

        Box(Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Icon(
                painter = painterResource(Res.drawable.ic_copy),
                contentDescription = "Copy",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

data class DirectoryItem(val name: String, val path: String?)