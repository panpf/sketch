/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.fragment.findNavController
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.ui.base.BaseComposeFragment
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.page.PhotoListPage
import com.github.panpf.sketch.sample.ui.page.buildPhotoPagerParams
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ComposeHomeFragment : BaseComposeFragment() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ComposeContent() {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Column {
                            Text(text = "Sketch3")
                            Text(text = "Compose", fontSize = 15.sp)
                        }
                    },
                    windowInsets = WindowInsets(0, 0, 0, 0)
                )

                val appSettings = LocalContext.current.appSettingsService
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .height(50.dp)
                        .clickable { appSettings.composePage.value = false }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(id = com.github.panpf.sketch.sample.R.drawable.ic_android),
                        contentDescription = "Android Page",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            PhotoListPage { items: List<Photo>, position: Int ->
                val params = buildPhotoPagerParams(items, position)
                findNavController().navigate(
                    NavMainDirections.actionPhotoPagerComposeFragment(
                        imageDetailJsonArray = Json.encodeToString(params.imageList),
                        totalCount = params.totalCount,
                        startPosition = params.startPosition,
                        initialPosition = params.initialPosition
                    ),
                )
            }
        }
    }
}