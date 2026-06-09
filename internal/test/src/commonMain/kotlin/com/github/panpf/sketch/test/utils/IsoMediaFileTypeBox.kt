package com.github.panpf.sketch.test.utils

class IsoMediaFileTypeBox(
    val majorBrand: String,
    val minorVersion: Int = 0,
    val compatibleBrands: List<String> = emptyList()
) {

    constructor(majorBrand: String, compatibleBrands: List<String>) : this(
        majorBrand,
        0,
        compatibleBrands
    )

    init {
        require(majorBrand.length == 4) { "Major brand must be exactly 4 ASCII characters." }
        for (brand in compatibleBrands) {
            require(brand.length == 4) { "Compatible brand '$brand' must be exactly 4 ASCII characters." }
        }
    }

    val bytes: ByteArray by lazy {
        // 1. Calculate the total size: Size(4) + Type(4) + MajorBrand(4) + MinorVersion(4) + (Number of Brands * 4)
        val totalSize = 16 + (compatibleBrands.size * 4)
        val byteArray = ByteArray(totalSize)
        var offset = 0

        // 2. Write Box Size (4 bytes, big endian)
        byteArray[offset++] = (totalSize shr 24).toByte()
        byteArray[offset++] = (totalSize shr 16).toByte()
        byteArray[offset++] = (totalSize shr 8).toByte()
        byteArray[offset++] = totalSize.toByte()

        // 3. Write Box Type ("ftyp", 4 bytes)
        writeAsciiString(byteArray, offset, "ftyp")
        offset += 4

        // 4. Write Major Brand (4 bytes)
        writeAsciiString(byteArray, offset, majorBrand)
        offset += 4

        // 5. Write Minor Version (4 bytes, big endian)
        byteArray[offset++] = (minorVersion shr 24).toByte()
        byteArray[offset++] = (minorVersion shr 16).toByte()
        byteArray[offset++] = (minorVersion shr 8).toByte()
        byteArray[offset++] = minorVersion.toByte()

        // 6. Write Compatible Brands in a loop (4 bytes each)
        for (brand in compatibleBrands) {
            writeAsciiString(byteArray, offset, brand)
            offset += 4
        }

        byteArray
    }

    private fun writeAsciiString(target: ByteArray, offset: Int, value: String) {
        for (i in value.indices) {
            target[offset + i] = value[i].code.toByte()
        }
    }

    override fun toString(): String {
        return "IsoMediaFileTypeBox(majorBrand='$majorBrand', minorVersion='$minorVersion', compatibleBrands=$compatibleBrands)"
    }
}
