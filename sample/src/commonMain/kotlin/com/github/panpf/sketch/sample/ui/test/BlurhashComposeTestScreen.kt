package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.fetch.newBlurhashUri
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.image.DelayDecodeInterceptor
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.state.rememberBlurhashStateImage
import com.github.panpf.sketch.util.Size

class BlurhashComposeTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "Blurhash") {
            Column(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(NavigationBarDefaults.windowInsets)
                    .verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Blurhash placeholder example",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                val context = LocalPlatformContext.current
                val uri = ResourceImages.jpeg.uri
                val stateImage =
                    rememberBlurhashStateImage("d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG\$\$-o", Size(100, 100))
                val request = ImageRequest(context, uri) {
                    memoryCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    placeholder(stateImage)
                    crossfade(true)
                    components {
                        addDecodeInterceptor(DelayDecodeInterceptor(2000))
                    }
                }
                // Example blurhash placeholder
                MyAsyncImage(
                    request = request,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.height(300.dp).width(200.dp).align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.size(20.dp))

                // Example blurhash strings
                val blurhash1 = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"
                val blurhash2 = "UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2"
                val blurhash3 = "L9HL7nxu00WB~qj[ayfQ00WB~qj["

                Text(
                    text = "BlurhashPainter Examples",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Direct BlurhashPainter usage
                Text(
                    text = "Direct BlurhashPainter",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
//                rememberPainterStateImage()
//                Image(
//                    painter = BlurhashPainter(
//                        blurhash = blurhash1,
//                        size = androidx.compose.ui.geometry.Size(200f, 200f)
//                    ),
//                    contentDescription = "Blurhash example 1",
//                    modifier = Modifier
//                        .size(150.dp)
//                        .border(2.dp, Color.Gray)
//                        .padding(2.dp)
//                )

//                Image(
//                    painter = BlurhashPainter(
//                        blurhash = blurhash1,
//                        size = androidx.compose.ui.geometry.Size(200f, 200f)
//                    ),
//                    contentDescription = "Blurhash example 1",
//                    modifier = Modifier
//                        .size(150.dp)
//                        .border(2.dp, Color.Gray)
//                        .padding(2.dp)
//                )
//
//                Spacer(Modifier.size(20.dp))

                // Blurhash URI usage
                Text(
                    text = "Blurhash URI with AsyncImage",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                AsyncImage(
                    uri = newBlurhashUri(blurhash2),
                    contentDescription = "Blurhash URI example",
                    modifier = Modifier.size(150.dp).border(2.dp, Color.Gray).padding(2.dp)
                )

                Spacer(Modifier.size(20.dp))

                // Blurhash state image example
                Text(
                    text = "Blurhash State Image",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                AsyncImage(
                    request = ComposableImageRequest("invalid_url") {
                        placeholder(rememberBlurhashStateImage(blurhash1, Size(100, 100)))
                        error(rememberBlurhashStateImage(blurhash3, Size(100, 100)))
                    },
                    contentDescription = "Blurhash state image example",
                    modifier = Modifier.size(150.dp).border(2.dp, Color.Gray).padding(2.dp)
                )

                Spacer(Modifier.size(20.dp))

                // Different blurhash ratios
                Text(
                    text = "Different Blurhash Ratios",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                listOf(
                    "LEHLh[WB2yk8pyoJadR*.7kCMdnj",
                    "LGF5?xYk^6#M@-5c,1J5@[or[Q6.",
                    "L6PZfSi_.AyE_3t7t7R**0o#DgR4",
                    "LKN]Rv%2Tw=w]~RBVZRi};RPxuwH",
                    "fEHLh[WB2yk8\$NxupyoJadR*=ss:.7kCMdnjx]S2S#M|%1%2ENRiSis.slNHW:WB",
                    "fHF5?xYk^6#M9wKS@-5b,1J5O[V=@[or[k6.O[TL};FxngOZE3NgjMFxS#OtcXnz",
                    "f6PZfSi_.AyE8^m+_3t7t7R*WBs,*0o#DgR4.Tt,_3R*D%xt%MIpMcV@%itSI9R5",
                    "fKN]Rv%2Tw=wR6cE]~RBVZRip0W9};RPxuwH%3s8tLOtxZ%gixtQI.ENa0NZIVt6",
                ).forEach { blurhash ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AsyncImage(
                            request = ComposableImageRequest("invalid_url") {
                                placeholder(rememberBlurhashStateImage(blurhash, Size((1000 * 16f / 9).toInt(), 1000)))
//                                error(rememberBlurhashStateImage(blurhash3))
                            },
                            contentDescription = "Blurhash variation",
                            modifier = Modifier.weight(1f).aspectRatio(16f / 9).border(1.dp, Color.LightGray)
                                .padding(1.dp)
                        )
                        AsyncImage(
                            request = ComposableImageRequest("invalid_url") {
                                placeholder(rememberBlurhashStateImage(blurhash, Size(1000, 1000)))
//                                error(rememberBlurhashStateImage(blurhash3))
                            },
                            contentDescription = "Blurhash variation",
                            modifier = Modifier.weight(1f).aspectRatio(1f).border(1.dp, Color.LightGray).padding(1.dp)
                        )
                        AsyncImage(
                            request = ComposableImageRequest("invalid_url") {
                                placeholder(rememberBlurhashStateImage(blurhash, Size(1000, (1000 * 16f / 9).toInt())))
//                                error(rememberBlurhashStateImage(blurhash3))
                            },
                            contentDescription = "Blurhash variation",
                            modifier = Modifier.weight(1f).aspectRatio(9f / 16).border(1.dp, Color.LightGray)
                                .padding(1.dp)
                        )
                    }
                    Spacer(Modifier.size(8.dp))
                }

                Spacer(Modifier.size(20.dp))

            }
        }
    }
}