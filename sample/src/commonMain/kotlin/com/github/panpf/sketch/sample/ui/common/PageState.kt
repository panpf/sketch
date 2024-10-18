package com.github.panpf.sketch.sample.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.resources.Res.drawable
import com.github.panpf.sketch.sample.resources.ic_error_baseline
import org.jetbrains.compose.resources.painterResource

@Stable
sealed interface PageState {

    @Stable
    data object Loading : PageState

    @Stable
    data class Empty(val message: String? = null, val onRetry: (() -> Unit)? = null) : PageState

    @Stable
    data class Error(val message: String? = null, val onRetry: (() -> Unit)? = null) : PageState
}

@Composable
fun PageState(pageState: PageState?, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    when (pageState) {
        is PageState.Loading -> {
            Box(
                modifier = modifier
                    .size(240.dp)
                    .background(colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(Modifier.size(30.dp))
            }
        }

        is PageState.Empty -> {
            Column(
                modifier = modifier
                    .size(240.dp)
                    .background(colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = pageState.message ?: "No Content",
                    color = colorScheme.onPrimaryContainer,
                    overflow = TextOverflow.Ellipsis,
                )

                if (pageState.onRetry != null) {
                    Spacer(modifier = Modifier.size(24.dp))
                    Button(
                        onClick = {
                            pageState.onRetry.invoke()
                        },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(text = "Reload")
                    }
                }
            }
        }

        is PageState.Error -> {
            Column(
                modifier = modifier
                    .size(240.dp)
                    .background(colorScheme.errorContainer, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(drawable.ic_error_baseline),
                    contentDescription = "icon",
                    tint = Color.White
                )

                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = pageState.message ?: "Load failure",
                    color = colorScheme.onErrorContainer,
                    overflow = TextOverflow.Ellipsis,
                )

                if (pageState.onRetry != null) {
                    Spacer(modifier = Modifier.size(24.dp))
                    Button(
                        onClick = {
                            pageState.onRetry.invoke()
                        },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(text = "Retry")
                    }
                }
            }
        }

        else -> {
            // Show nothing
        }
    }
}