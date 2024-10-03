package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_image_outline
import com.github.panpf.sketch.sample.resources.ic_image_outline_big
import com.github.panpf.sketch.sample.resources.sample_elephant
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold

class IconPainterTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "IconPainter") {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.size(20.dp))

                Image(
                    painter = rememberIconPainter(
                        icon = Res.drawable.ic_image_outline,
                        background = Color.Green,
                        iconTint = Color(0xFF775740)
                    ),
                    contentDescription = "example",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .border(2.dp, Color.Red)
                        .padding(2.dp)
                )

                Spacer(Modifier.size(20.dp))

                Image(
                    painter = rememberIconPainter(
                        icon = Res.drawable.ic_image_outline_big,
                        background = Color.Green,
                        iconTint = Color(0xFF775740)
                    ),
                    contentDescription = "example",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .border(2.dp, Color.Red)
                        .padding(2.dp)
                )

                Spacer(Modifier.size(20.dp))

                Image(
                    painter = rememberIconPainter(
                        icon = Res.drawable.ic_image_outline,
                        background = Res.drawable.sample_elephant,
                        iconTint = Color(0xFF775740)
                    ),
                    contentDescription = "example",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .border(2.dp, Color.Red)
                        .padding(2.dp)
                )

                Spacer(Modifier.size(20.dp))
            }
        }
    }
}