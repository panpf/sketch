package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(dataList.size) { item ->
                val data = dataList[item]
                DirectoryItem(data)
            }
        }
    }
}

@Composable
fun DirectoryItem(data: DirectoryItem) {
    val scope = rememberCoroutineScope()
    val appEvents = koinInject<AppEvents>()
    val context = LocalPlatformContext.current
    Box(Modifier.fillMaxWidth()) {
        Card(
            Modifier.fillMaxWidth()
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
            Row(Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .weight(1f)
                        .padding(16.dp),
                ) {
                    Text(
                        text = "${data.name}: ",
                        fontSize = 14.sp,
                        modifier = Modifier.alpha(0.6f)
                    )
                    Text(
                        text = data.path.orEmpty(),
                        fontSize = 16.sp,
                    )
                }

                Box(Modifier.align(Alignment.CenterVertically).padding(16.dp)) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_copy),
                        contentDescription = "Copy",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

data class DirectoryItem(val name: String, val path: String?)