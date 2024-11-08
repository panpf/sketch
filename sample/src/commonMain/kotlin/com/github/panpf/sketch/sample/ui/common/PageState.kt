package com.github.panpf.sketch.sample.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.sample.resources.Res
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
fun PageState(pageState: PageState?, modifier: Modifier = Modifier.fillMaxSize()) {
    val colorScheme = MaterialTheme.colorScheme
    when (pageState) {
        is PageState.Loading -> {
            Box(
                modifier.clickable(
                    onClick = {},
                    indication = null,  // Remove ripple effect on click
                    interactionSource = remember { MutableInteractionSource() }
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .background(colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                        .align(Alignment.Center)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(Modifier.size(30.dp))
                }
            }
        }

        is PageState.Empty -> {
            Box(
                modifier.clickable(
                    onClick = {},
                    indication = null,  // Remove ripple effect on click
                    interactionSource = remember { MutableInteractionSource() }
                )
            ) {
                Column(
                    modifier = Modifier
                        .size(240.dp)
                        .background(colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                        .align(Alignment.Center)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = pageState.message ?: "No Content",
                        color = colorScheme.onPrimaryContainer,
                        fontSize = 12.sp,
                        maxLines = 4,
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
        }

        is PageState.Error -> {
            Box(
                modifier.clickable(
                    onClick = {},
                    indication = null,  // Remove ripple effect on click
                    interactionSource = remember { MutableInteractionSource() }
                )
            ) {
                Column(
                    modifier = Modifier
                        .size(240.dp)
                        .background(colorScheme.errorContainer, RoundedCornerShape(16.dp))
                        .align(Alignment.Center)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_error_baseline),
                        contentDescription = "icon",
                        tint = colorScheme.onErrorContainer,
                    )

                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = pageState.message ?: "Load failure",
                        color = colorScheme.onErrorContainer,
                        fontSize = 12.sp,
                        maxLines = 4,
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
        }

        else -> {
            // Show nothing
        }
    }
}