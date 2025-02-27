# Exif Orientation

翻译：[English](exif_orientation.md)

Sketch 支持根据图片的 Exif 信息恢复图片的方向，此功能强制开启，并且无法关闭

在 Android 上依赖 `androidx.exifinterface:exifinterface` 读取 exif 信息然后再在解码时恢复图片的方向

在非 Android 上则依赖 Skia 自带的对 Exif 的支持 