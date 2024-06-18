package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import sketch_root.sample.generated.resources.Res
import sketch_root.sample.generated.resources.ic_image_outline

class TempTestScreen : BaseScreen() {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "TempTest") {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text("In development...")
//            }
//            LazyColumn(modifier = Modifier.fillMaxSize()) {
//                item {
//                    AsyncImage(
//                        request = ImageRequest(
//                            LocalPlatformContext.current,
//                            newComposeResourceUri(Res.getUri("files/liuyifei.jpg"))
//                        ) {
//                            memoryCachePolicy(DISABLED)
//                            resultCachePolicy(DISABLED)
//                        },
//                        contentDescription = null,
//                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
//                    )
//                }
//            }
            Column {
                Image(
                    painter = painterResource(resource = Res.drawable.ic_image_outline),
                    contentDescription = null,
                    contentScale = ContentScale.None,
                    modifier = Modifier.size(200.dp).background(Color.Cyan)
                )

                Image(
                    painter = rememberIconPainter(
                        icon = Res.drawable.ic_image_outline,
                        background = colorScheme.primaryContainer,
                        iconTint = colorScheme.onPrimaryContainer,
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp).background(Color.Cyan)
                )
            }
        }
    }
}