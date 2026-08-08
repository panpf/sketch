package com.github.panpf.sketch.sample.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.panpf.sketch.sample.ui.test.DirectoryItem

@Preview
@Composable
fun DirectoryItemPreview() {
    DirectoryItem(DirectoryItem("userHome", "/Users/xxx"))
}