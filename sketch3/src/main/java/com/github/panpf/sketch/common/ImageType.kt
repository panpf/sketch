package com.github.panpf.sketch.common

enum class ImageType(val mimeType: String) {
    JPEG("image/jpeg"),
    PNG("image/png"),
    WEBP("image/webp"),
    GIF("image/gif"),
    BMP("image/bmp");
//    HEIC("image/heic"),
//    HEIF("image/heif"),

    companion object {
        @JvmStatic
        fun valueOfMimeType(mimeType: String?): ImageType? = when {
            JPEG.mimeType.equals(mimeType, ignoreCase = true) -> JPEG
            PNG.mimeType.equals(mimeType, ignoreCase = true) -> PNG
            WEBP.mimeType.equals(mimeType, ignoreCase = true) -> WEBP
            GIF.mimeType.equals(mimeType, ignoreCase = true) -> GIF
            BMP.mimeType.equals(mimeType, ignoreCase = true) -> BMP
            else -> null
        }
    }
}