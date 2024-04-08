package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.compose.fetch.newComposeResourceUri
import com.github.panpf.sketch.images.MyImages

actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<FetcherTestItem> {
//    val fileUriTestFile = getFileUriTestFile(MyImages.jpeg)
//    val fileUriTestFile2 = getFileUriTestFile(MyImages.bmp)
    return buildList {
        add(FetcherTestItem(title = "HTTP", MyImages.HTTP))
        add(FetcherTestItem(title = "HTTPS", MyImages.HTTPS))
//        add(FetcherTestItem(title = "FILE_URI", newFileUri(fileUriTestFile)))
//        add(FetcherTestItem(title = "FILE_PATH", fileUriTestFile2.toString()))
//        add(FetcherTestItem(title = "RES_KOTLIN", newKotlinResourceUri("sample.jpeg")))
        add(FetcherTestItem(title = "RES_COMPOSE", newComposeResourceUri("files/liuyifei.jpg")))
        add(FetcherTestItem(title = "BASE64", MyImages.BASE64_IMAGE))
    }
}

//private suspend fun getFileUriTestFile(image: MyImage): Path =
//    withContext(Dispatchers.IO) {
//        val appDataDir = getCachesDirectory().toPath()
//        val resourceImage = image as MyResourceImage
//        val imageFile = appDataDir.resolve(resourceImage.fileName)
//        val fileSystem = FileSystem.SYSTEM
//        if (!fileSystem.exists(imageFile)) {
//            fileSystem.createDirectories(imageFile.parent!!)
//            val resourcePath = NSBundle.mainBundle.resourcePath!!.toPath()
//            val resourceFile =
//                resourcePath.resolve("compose-resources").resolve(resourceImage.fileName)
//            fileSystem.source(resourceFile).buffer().use { input ->
//                fileSystem.sink(imageFile).buffer().use { output ->
//                    output.writeAll(input)
//                }
//            }
//        }
//        imageFile
//    }


//private fun getCachesDirectory(): String {
//    val paths =
//        NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
//    return paths.first() as String
//}