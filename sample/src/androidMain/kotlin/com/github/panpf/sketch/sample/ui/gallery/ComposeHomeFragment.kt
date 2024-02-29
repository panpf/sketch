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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScaleTransition
import com.github.panpf.sketch.sample.ui.base.BaseComposeFragment
import com.github.panpf.sketch.sample.ui.base.StatusBarTextStyle
import com.github.panpf.sketch.sample.ui.screen.PhotoListScreen
import com.github.panpf.sketch.sample.ui.screen.PhotoPagerScreen

class ComposeHomeFragment : BaseComposeFragment() {

    @Composable
    override fun ComposeContent() {
        var screenState by remember { mutableStateOf<Screen?>(null) }
        LaunchedEffect(Unit) {
            snapshotFlow { screenState }.collect {
                statusBarTextStyle = if (it is PhotoPagerScreen) {
                    StatusBarTextStyle.White
                } else {
                    StatusBarTextStyle.Black
                }
            }
        }
        Navigator(PhotoListScreen) { navigator ->
            ScaleTransition(navigator = navigator)
            screenState = navigator.lastItem
        }
    }
}