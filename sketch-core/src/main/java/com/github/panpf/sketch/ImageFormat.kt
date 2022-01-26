package com.github.panpf.sketch

enum class ImageFormat(val mimeType: String) {
    JPEG("image/jpeg"),
    PNG("image/png"),
    WEBP("image/webp"),
    GIF("image/gif"),
    BMP("image/bmp"),
    HEIF("image/heif"),
    ;

    companion object {
        @JvmStatic
        fun valueOfMimeType(mimeType: String?): ImageFormat? = when {
            JPEG.mimeType.equals(mimeType, ignoreCase = true) -> JPEG
            PNG.mimeType.equals(mimeType, ignoreCase = true) -> PNG
            WEBP.mimeType.equals(mimeType, ignoreCase = true) -> WEBP
            GIF.mimeType.equals(mimeType, ignoreCase = true) -> GIF
            BMP.mimeType.equals(mimeType, ignoreCase = true) -> BMP
            HEIF.mimeType.equals(mimeType, ignoreCase = true) -> HEIF
            else -> null
        }
    }
}