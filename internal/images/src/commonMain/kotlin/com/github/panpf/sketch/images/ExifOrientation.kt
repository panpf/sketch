package com.github.panpf.sketch.images

interface ExifOrientation {

    companion object {
        /**
         * No orientation defined
         *
         * * flip horizontally: false
         * * rotate: 0
         */
        const val UNDEFINED = 0

        /**
         * Orientation is normal
         *
         * * flip horizontally: false
         * * rotate: 0
         */
        const val NORMAL = 1

        /**
         * Indicates the image is left right reversed mirror.
         *
         * * flip horizontally: true
         * * rotate: 0
         */
        const val FLIP_HORIZONTAL = 2

        /**
         * Indicates the image is rotated by 180 degree clockwise.
         *
         * * flip horizontally: false
         * * rotate: 180
         */
        const val ROTATE_180 = 3

        /**
         * Indicates the image is upside down mirror, it can also be represented by flip horizontally firstly and rotate 180 degree clockwise.
         *
         * * flip horizontally: true
         * * rotate: 180
         */
        const val FLIP_VERTICAL = 4

        /**
         * Indicates the image is flipped about top-left <--> bottom-right axis, it can also be
         * represented by flip horizontally firstly and rotate 270 degree clockwise.
         *
         * * flip horizontally: true
         * * rotate: 270
         */
        const val TRANSPOSE = 5

        /**
         * Indicates the image is rotated by 90 degree clockwise.
         *
         * * flip horizontally: false
         * * rotate: 90
         */
        const val ROTATE_90 = 6

        /**
         * Indicates the image is flipped about top-right <--> bottom-left axis, it can also be
         * represented by flip horizontally firstly and rotate 90 degree clockwise.
         * * flip horizontally: true
         * * rotate: 90
         */
        const val TRANSVERSE = 7

        /**
         * Indicates the image is rotated by 270 degree clockwise.
         *
         * * flip horizontally: false
         * * rotate: 270
         */
        const val ROTATE_270 = 8

        fun name(exifOrientation: Int): String =
            when (exifOrientation) {
                UNDEFINED -> "UNDEFINED"
                NORMAL -> "NORMAL"
                FLIP_HORIZONTAL -> "FLIP_HORIZONTAL"
                ROTATE_180 -> "ROTATE_180"
                FLIP_VERTICAL -> "FLIP_VERTICAL"
                TRANSPOSE -> "TRANSPOSE"
                ROTATE_90 -> "ROTATE_90"
                TRANSVERSE -> "TRANSVERSE"
                ROTATE_270 -> "ROTATE_270"
                else -> throw IllegalArgumentException("Invalid exifOrientation: $exifOrientation")
            }

        fun valueOf(name: String): Int =
            when (name) {
                "UNDEFINED" -> UNDEFINED
                "NORMAL" -> NORMAL
                "FLIP_HORIZONTAL" -> FLIP_HORIZONTAL
                "ROTATE_180" -> ROTATE_180
                "FLIP_VERTICAL" -> FLIP_VERTICAL
                "TRANSPOSE" -> TRANSPOSE
                "ROTATE_90" -> ROTATE_90
                "TRANSVERSE" -> TRANSVERSE
                "ROTATE_270" -> ROTATE_270
                else -> throw IllegalArgumentException("Unknown ExifOrientation name: $name")
            }

        val values = intArrayOf(
            UNDEFINED,
            NORMAL,
            FLIP_HORIZONTAL,
            ROTATE_180,
            FLIP_VERTICAL,
            TRANSPOSE,
            ROTATE_90,
            TRANSVERSE,
            ROTATE_270,
        )
    }
}