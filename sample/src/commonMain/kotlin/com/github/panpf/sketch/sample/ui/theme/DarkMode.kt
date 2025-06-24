package com.github.panpf.sketch.sample.ui.theme

import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.DarkMode
import com.github.panpf.sketch.sample.platformSupportedDarkModes
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_auto_mode
import com.github.panpf.sketch.sample.resources.ic_dark_mode
import com.github.panpf.sketch.sample.resources.ic_light_mode
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun DarkModeSwitch(modifier: Modifier = Modifier) {
    val appSettings: AppSettings = koinInject()
    val darkMode by appSettings.darkMode.collectAsState()
    val nextDarkMode by remember {
        derivedStateOf {
            val platformSupportedDarkModes = platformSupportedDarkModes()
            val index = platformSupportedDarkModes.indexOf(darkMode)
            val nextDarkModeIndex = (index + 1) % platformSupportedDarkModes.size
            platformSupportedDarkModes[nextDarkModeIndex]
        }
    }
    IconButton(
        onClick = {
            appSettings.darkMode.value = nextDarkMode
        },
        modifier = modifier
    ) {
        val icon = when (nextDarkMode) {
            DarkMode.SYSTEM -> Res.drawable.ic_auto_mode
            DarkMode.LIGHT -> Res.drawable.ic_light_mode
            DarkMode.DARK -> Res.drawable.ic_dark_mode
        }
        Icon(
            painter = painterResource(icon),
            contentDescription = "Dark mode",
            tint = NavigationRailItemDefaults.colors().unselectedIconColor,
            modifier = Modifier.size(20.dp)
        )
    }
}