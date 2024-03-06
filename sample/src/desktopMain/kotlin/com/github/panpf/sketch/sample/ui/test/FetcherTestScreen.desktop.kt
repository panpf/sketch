package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.resources.AssetImages

actual suspend fun buildFetcherTestItems(context: PlatformContext): List<FetcherTestItem> {
    return buildList {
        add(FetcherTestItem(title = "HTTP", AssetImages.HTTP))
        add(FetcherTestItem(title = "HTTPS", AssetImages.HTTPS))
        add(FetcherTestItem(title = "RESOURCES", newResourceUri("sample.jpeg")))
        add(FetcherTestItem(title = "BASE64", AssetImages.BASE64_IMAGE))
        // TODO File, more...
    }
}