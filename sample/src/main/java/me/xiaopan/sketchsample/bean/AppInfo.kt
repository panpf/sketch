package me.xiaopan.sketchsample.bean

import me.xiaopan.sketchsample.util.FileScanner

/**
 * App信息
 */
class AppInfo(val isTempInstalled: Boolean) : FileScanner.FileItem {
    var name: String? = null
    var packageName: String? = null
    var id: String? = null
    var versionName: String? = null
    var formattedAppSize: String? = null
    var sortName: String? = null
    var apkFilePath: String? = null
    var versionCode: Int = 0
    var appSize: Long = 0
    var isTempXPK: Boolean = false

    override fun getFilePath(): String? {
        return apkFilePath
    }

    override fun getFileLength(): Long {
        return appSize
    }
}
