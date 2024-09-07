package com.github.panpf.sketch.test.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.media.ThumbnailUtils

/**
 * copy from https://github.com/zangliguang/ImageSimilar/blob/master/app/src/main/java/tool/ImageHelper.java
 */


/**
 * 生成图片指纹
 */
fun produceFingerPrint(source: Bitmap?): String {
    val width = 8
    val height = 8
    // 第一步，缩小尺寸。
    // 将图片缩小到8x8的尺寸，总共64个像素。这一步的作用是去除图片的细节，只保留结构、明暗等基本信息，摒弃不同尺寸、比例带来的图片差异。
    val thumb = createThumbnail(source, width, height)
    // 第二步，简化色彩。
    // 将缩小后的图片，转为64级灰度。也就是说，所有像素点总共只有64种颜色。
    val pixels = IntArray(width * height)
    for (i in 0 until width) {
        for (j in 0 until height) {
            pixels[i * height + j] = rgbToGray(thumb.getPixel(i, j))
        }
    }

    // 第三步，计算平均值。
    // 计算所有64个像素的灰度平均值。
    val avgPixel = average(pixels)
    // 第四步，比较像素的灰度。
    // 将每个像素的灰度，与平均值进行比较。大于或等于平均值，记为1；小于平均值，记为0。
    val comps = IntArray(width * height)
    for (i in comps.indices) {
        if (pixels[i] >= avgPixel) {
            comps[i] = 1
        } else {
            comps[i] = 0
        }
    }

    // 第五步，计算哈希值。
    // 将上一步的比较结果，组合在一起，就构成了一个64位的整数，这就是这张图片的指纹。组合的次序并不重要，只要保证所有图片都采用同样次序就行了。
    val hashCode = StringBuilder()
    var i = 0
    while (i < comps.size) {
        val result = comps[i] * Math.pow(2.0, 3.0).toInt() + (comps[i + 1]
                * Math.pow(2.0, 2.0).toInt()) + (comps[i + 2]
                * Math.pow(2.0, 1.0).toInt()) + comps[i + 3]
        hashCode.append(binaryToHex(result))
        i += 4
    }
    recycleBitmap(thumb)

    // 得到指纹以后，就可以对比不同的图片，看看64位中有多少位是不一样的。
    return hashCode.toString()
}

/**
 * 计算"汉明距离"（Hamming distance）。 如果不相同的数据位不超过5，就说明两张图片很相似；如果大于10，就说明这是两张不同的图片。
 */
fun hammingDistance(sourceImageFingerPrint: String, otherImageFingerPrint: String): Int {
    var difference = 0
    val len = sourceImageFingerPrint.length
    for (i in 0 until len) {
        if (sourceImageFingerPrint[i] != otherImageFingerPrint[i]) {
            difference++
        }
    }
    return difference
}

/**
 * 创建缩略图
 */
private fun createThumbnail(source: Bitmap?, width: Int, height: Int): Bitmap {
    return ThumbnailUtils.extractThumbnail(source, width, height)
}

private fun recycleBitmap(thumb: Bitmap?) {
    if (thumb != null && !thumb.isRecycled) {
        thumb.recycle()
    }
}

/**
 * 灰度值计算
 *
 * @param pixels 像素
 * @return int 灰度值
 */
private fun rgbToGray(pixels: Int): Int {
    // int _alpha = (pixels >> 24) & 0xFF;
    val _red = Color.red(pixels)
    val _green = Color.green(pixels)
    val _blue = Color.blue(pixels)
    //        int _red = (pixels >> 16) & 0xFF;
//        int _green = (pixels >> 8) & 0xFF;
//        int _blue = (pixels) & 0xFF;
    return (0.3 * _red + 0.59 * _green + 0.11 * _blue).toInt()
}

/**
 * 计算数组的平均值
 *
 * @param pixels 数组
 * @return int 平均值
 */
private fun average(pixels: IntArray): Int {
    var m = 0f
    for (i in pixels.indices) {
        m += pixels[i].toFloat()
    }
    m = m / pixels.size
    return m.toInt()
}

/**
 * 二进制转为十六进制
 */
private fun binaryToHex(binary: Int): Char {
    var ch = ' '
    ch = when (binary) {
        0 -> '0'
        1 -> '1'
        2 -> '2'
        3 -> '3'
        4 -> '4'
        5 -> '5'
        6 -> '6'
        7 -> '7'
        8 -> '8'
        9 -> '9'
        10 -> 'a'
        11 -> 'b'
        12 -> 'c'
        13 -> 'd'
        14 -> 'e'
        15 -> 'f'
        else -> ' '
    }
    return ch
}