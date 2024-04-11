package com.github.panpf.sketch.sample.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AutoLinkText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val uriHandler = LocalUriHandler.current
    val colorScheme = MaterialTheme.colorScheme
    val annotatedString = remember(text) {
        val pattern = "((http|https)://)?[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(/\\S*)?".toRegex()
        val matches = pattern.findAll(text)
        val spanStyle =
            SpanStyle(color = colorScheme.primary, textDecoration = TextDecoration.Underline)
        val annotatedString = Builder(text)
        matches.forEach { matchResult ->
            annotatedString.addStyle(spanStyle, matchResult.range.first, matchResult.range.last + 1)
            annotatedString.addStringAnnotation(
                tag = "URL",
                annotation = matchResult.value,
                start = matchResult.range.first,
                end = matchResult.range.last + 1
            )
        }
        annotatedString.toAnnotatedString()
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    uriHandler.openUri(annotation.item)
                }
        }
    )
}