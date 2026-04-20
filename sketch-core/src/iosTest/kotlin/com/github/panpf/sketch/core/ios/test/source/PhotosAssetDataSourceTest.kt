package com.github.panpf.sketch.core.ios.test.source

import com.github.panpf.sketch.fetch.newPhotosAssetUri
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.PhotosAssetDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlinx.coroutines.test.runTest
import platform.Photos.PHAsset
import platform.Photos.PHAssetResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class PhotosAssetDataSourceTest {

    @Test
    fun testConstructor() {
        PhotosAssetDataSource(
            "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001",
            true,
            false,
            PHAsset(),
            PHAssetResource()
        ).apply {
            assertEquals("DB16113B-984A-4D12-B4D0-50FC46066781/L0/001", this.localIdentifier)
            assertEquals(true, this.preferredThumbnail)
            assertEquals(false, this.networkAccessAllowed)
            assertEquals(PHAsset(), this.asset)
            assertEquals(PHAssetResource(), this.resource)
        }

        PhotosAssetDataSource(
            "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001",
            false,
            true,
            PHAsset(),
            PHAssetResource()
        ).apply {
            assertEquals("DB16113B-984A-4D12-B4D0-50FC46066781/L0/001", this.localIdentifier)
            assertEquals(false, this.preferredThumbnail)
            assertEquals(true, this.networkAccessAllowed)
            assertEquals(PHAsset(), this.asset)
            assertEquals(PHAssetResource(), this.resource)
        }


        PhotosAssetDataSource(
            localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001",
            preferredThumbnail = true,
            networkAccessAllowed = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
    }

    @Test
    fun testKey() {
        PhotosAssetDataSource(
            localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001",
            preferredThumbnail = true,
            networkAccessAllowed = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        ).apply {
            assertEquals(
                expected = buildString {
                    append(newPhotosAssetUri(localIdentifier))
                    if (preferredThumbnail) append("#thumb")
                    if (!networkAccessAllowed) append("#onlyLocal")
                },
                actual = this.key
            )
        }
    }

    @Test
    fun testDataFrom() {
        PhotosAssetDataSource(
            localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001",
            preferredThumbnail = true,
            networkAccessAllowed = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        ).apply {
            assertEquals(DataFrom.LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testOpenSource() {
        val dataSource = PhotosAssetDataSource(
            localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001",
            preferredThumbnail = true,
            networkAccessAllowed = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        assertFailsWith(UnsupportedOperationException::class) {
            dataSource.openSource()
        }
    }

    @Test
    fun testGetFile() {
        val (_, sketch) = getTestContextAndSketch()
        val dataSource = PhotosAssetDataSource(
            localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001",
            preferredThumbnail = true,
            networkAccessAllowed = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        assertFailsWith(UnsupportedOperationException::class) {
            dataSource.getFile(sketch)
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val preferredThumbnail = true
        val networkAccessAllowed = false
        val asset = PHAsset()
        val asset2 = PHAsset()
        val resource = PHAssetResource()
        val resource2 = PHAssetResource()
        val source1 = PhotosAssetDataSource(
            localIdentifier,
            preferredThumbnail,
            networkAccessAllowed,
            asset,
            resource
        )
        val source12 = PhotosAssetDataSource(
            localIdentifier,
            preferredThumbnail,
            networkAccessAllowed,
            asset,
            resource
        )
        val source2 = PhotosAssetDataSource(
            localIdentifier + "hello",
            preferredThumbnail,
            networkAccessAllowed,
            asset,
            resource
        )
        val source3 = PhotosAssetDataSource(
            localIdentifier,
            !preferredThumbnail,
            networkAccessAllowed,
            asset,
            resource
        )
        val source4 = PhotosAssetDataSource(
            localIdentifier,
            preferredThumbnail,
            !networkAccessAllowed,
            asset,
            resource
        )
        val source5 = PhotosAssetDataSource(
            localIdentifier,
            preferredThumbnail,
            networkAccessAllowed,
            asset2,
            resource
        )
        val source6 = PhotosAssetDataSource(
            localIdentifier,
            preferredThumbnail,
            networkAccessAllowed,
            asset,
            resource2
        )

        assertEquals(expected = source1, actual = source12)
        assertEquals(expected = source1, actual = source5)
        assertEquals(expected = source1, actual = source6)
        assertEquals(expected = source5, actual = source6)
        assertNotEquals(illegal = source1, actual = source2)
        assertNotEquals(illegal = source1, actual = source3)
        assertNotEquals(illegal = source1, actual = source4)
        assertNotEquals(illegal = source2, actual = source3)
        assertNotEquals(illegal = source2, actual = source4)
        assertNotEquals(illegal = source3, actual = source4)
        assertNotEquals(illegal = source1, actual = null as Any?)
        assertNotEquals(illegal = source1, actual = Any())

        assertEquals(expected = source1.hashCode(), actual = source12.hashCode())
        assertEquals(expected = source1.hashCode(), actual = source5.hashCode())
        assertEquals(expected = source1.hashCode(), actual = source6.hashCode())
        assertNotEquals(illegal = source1.hashCode(), actual = source2.hashCode())
        assertNotEquals(illegal = source1.hashCode(), actual = source3.hashCode())
        assertNotEquals(illegal = source1.hashCode(), actual = source4.hashCode())
        assertNotEquals(illegal = source2.hashCode(), actual = source3.hashCode())
        assertNotEquals(illegal = source2.hashCode(), actual = source4.hashCode())
        assertNotEquals(illegal = source3.hashCode(), actual = source4.hashCode())
    }

    @Test
    fun testToString() = runTest {
        PhotosAssetDataSource(
            localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001",
            preferredThumbnail = true,
            networkAccessAllowed = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        ).apply {
            assertEquals(
                expected = "PhotosAssetDataSource(" +
                        "localIdentifier='$localIdentifier', " +
                        "preferredThumbnail=$preferredThumbnail, " +
                        "networkAccessAllowed=$networkAccessAllowed" +
                        ")",
                actual = this.toString()
            )
        }
    }
}