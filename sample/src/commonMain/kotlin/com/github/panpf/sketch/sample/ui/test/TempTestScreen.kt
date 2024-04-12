package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.compose.fetch.newComposeResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold

class TempTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "TempTest") {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text("In development...")
//            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    AsyncImage(
                        request = ImageRequest(
                            LocalPlatformContext.current,
                            newComposeResourceUri("files/liuyifei.jpg")
                        ) {
                            memoryCachePolicy(DISABLED)
                            resultCachePolicy(DISABLED)
                        },
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    )
                }
            }
        }
    }
}