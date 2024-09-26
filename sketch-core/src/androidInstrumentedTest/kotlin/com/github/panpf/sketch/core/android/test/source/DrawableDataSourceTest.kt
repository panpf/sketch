package com.github.panpf.sketch.core.android.test.source

class DrawableDataSourceTest {
    // TODO test

//    @Test
//    fun testConstructor() {
//        val context = getTestContext()
//
//        AssetDataSource(
//            context = context,
//            fileName = ResourceImages.jpeg.resourceName
//        ).apply {
//            assertEquals(ResourceImages.jpeg.resourceName, this.fileName)
//            assertEquals(LOCAL, this.dataFrom)
//        }
//    }
//
//    @Test
//    fun testKey() {
//        val context = getTestContext()
//        AssetDataSource(
//            context = context,
//            fileName = ResourceImages.jpeg.resourceName
//        ).apply {
//            assertEquals(
//                expected = newAssetUri(fileName = ResourceImages.jpeg.resourceName),
//                actual = key
//            )
//        }
//    }
//
//    @Test
//    fun testOpenSource() {
//        val context = getTestContext()
//
//        AssetDataSource(
//            context = context,
//            fileName = ResourceImages.jpeg.resourceName
//        ).apply {
//            openSource().asOrThrow<Closeable>().close()
//        }
//
//        assertFailsWith(FileNotFoundException::class) {
//            AssetDataSource(
//                context = context,
//                fileName = "not_found.jpeg"
//            ).apply {
//                openSource()
//            }
//        }
//    }
//
//    @Test
//    fun testGetFile() {
//        val (context, sketch) = getTestContextAndSketch()
//        AssetDataSource(
//            context = context,
//            fileName = ResourceImages.jpeg.resourceName
//        ).getFile(sketch).apply {
//            assertTrue(actual = toString().contains("/${DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME}/"))
//        }
//    }
//
//    @Test
//    fun testEqualsAndHashCode() {
//        val context = getTestContext()
//        val element1 = AssetDataSource(
//            context = context,
//            fileName = ResourceImages.jpeg.resourceName
//        )
//        val element11 = AssetDataSource(
//            context = context,
//            fileName = ResourceImages.jpeg.resourceName
//        )
//        val element2 = AssetDataSource(
//            context = context,
//            fileName = ResourceImages.png.resourceName
//        )
//
//        assertNotSame(element1, element11)
//        assertNotSame(element1, element2)
//        assertNotSame(element2, element11)
//
//        assertEquals(element1, element1)
//        assertEquals(element1, element11)
//        assertNotEquals(element1, element2)
//        assertNotEquals(element2, element11)
//        assertNotEquals(element1, null as Any?)
//        assertNotEquals(element1, Any())
//
//        assertEquals(element1.hashCode(), element1.hashCode())
//        assertEquals(element1.hashCode(), element11.hashCode())
//        assertNotEquals(element1.hashCode(), element2.hashCode())
//        assertNotEquals(element2.hashCode(), element11.hashCode())
//    }
//
//    @Test
//    fun testToString() {
//        val context = getTestContext()
//
//        AssetDataSource(
//            context = context,
//            fileName = ResourceImages.jpeg.resourceName
//        ).apply {
//            assertEquals(
//                "AssetDataSource('sample.jpeg')",
//                toString()
//            )
//        }
//
//        AssetDataSource(
//            context = context,
//            fileName = "not_found.jpeg"
//        ).apply {
//            assertEquals("AssetDataSource('not_found.jpeg')", toString())
//        }
//    }
}