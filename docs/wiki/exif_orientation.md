# Exif Orientation

Translations: [简体中文](exif_orientation_zh.md)

Sketch supports restoring the orientation of images based on their Exif information. This feature is
forcibly turned on and cannot be turned off.

## Implement

On Android, rely on `androidx.exifinterface:exifinterface` to read the exif information and then
restore the orientation of the image during decoding

On non-Android devices, it relies on Skia's own support for Exif.