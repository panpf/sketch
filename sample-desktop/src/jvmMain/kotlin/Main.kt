import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.panpf.sketch.sample.ui.Page
import com.github.panpf.sketch.sample.ui.navigation.NavigationContainer
import com.github.panpf.sketch.sample.ui.theme.AppTheme
import com.github.panpf.sketch.sample.ui.util.EventBus
import kotlinx.coroutines.launch

fun main() = application {
    val coroutineScope = rememberCoroutineScope()
    Window(
        title = "Sketch3",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(size = DpSize(1000.dp, 800.dp)),
        onKeyEvent = {
            coroutineScope.launch {
                EventBus.keyEvent.emit(it)
            }
            false
        }
    ) {
        AppTheme {
            NavigationContainer(Page.Main)
        }
    }
}