package com.github.panpf.sketch3.common.cache.disk

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.text.format.Formatter
import com.github.panpf.sketch3.util.DiskLruCache
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class CacheDirCreator(private val context: Context) {

    fun getSafeCacheDir(dirName: String): File {
        val appCacheDir = getAppCacheDir(context)
        return File(appCacheDir, appendProcessName(context, dirName))
    }

    /**
     * 创建缓存目录，会优先在 sdcard 上创建
     *
     * @param dirName            目录名称
     * @param minSpaceSize       最小空间
     * @param cleanOnNoSpace     空间不够用时就尝试清理一下
     * @param cleanOldCacheFiles 清除旧的缓存文件
     * @param expandNumber       当 dirName 无法使用时就会尝试 dirName1、dirName2、dirName3...
     * @return 你应当以返回的目录为最终可用的目录
     */
    @Throws(IOException::class)
    fun buildCacheDir(
        dirName: String,
        minSpaceSize: Long,
        cleanOnNoSpace: Boolean,
        cleanOldCacheFiles: Boolean,
        expandNumber: Int
    ): File {
        val appCacheDirs = listOfNotNull(context.externalCacheDir, context.cacheDir)
        val diskCacheDirName = appendProcessName(context, dirName)
        var noSpaceException: IOException? = null
        var unableCreateFileException: IOException? = null
        var diskCacheDir: File? = null
        var expandCount: Int
        for (appCacheDir in appCacheDirs) {
            expandCount = 0
            while (expandCount <= expandNumber) {
                val currentDirName = diskCacheDirName + if (expandCount > 0) expandCount else ""
                diskCacheDir = File(appCacheDir, currentDirName)

                // 如果当前已存在的文件夹不是 DiskLruCache 的缓存目录就清空
                if (diskCacheDir.exists() && cleanOldCacheFiles) {
                    val journalFile = File(diskCacheDir, DiskLruCache.JOURNAL_FILE)
                    if (!journalFile.exists()) {
                        cleanDir(diskCacheDir)
                    }
                }

                // 目录不存在就创建，创建结果返回 false 后检查还是不存在就说明创建失败
                if (!diskCacheDir.exists() && !diskCacheDir.mkdirs() && !diskCacheDir.exists()) {
                    expandCount++
                    continue
                }

                // 检查空间，少于 minSpaceSize 就不能用了
                var availableBytes = getAvailableBytes(diskCacheDir)
                if (availableBytes < minSpaceSize) {
                    // 空间不够用的时候直接清空，然后再次计算可用空间
                    if (cleanOnNoSpace) {
                        cleanDir(diskCacheDir)
                        availableBytes = getAvailableBytes(diskCacheDir)
                    }

                    // 依然不够用，那不好意思了
                    if (availableBytes < minSpaceSize) {
                        val availableFormatted =
                            Formatter.formatFileSize(context, availableBytes)
                        val minSpaceFormatted = Formatter.formatFileSize(context, minSpaceSize)
                        noSpaceException =
                            IOException("Not enough storage space. Need " + availableFormatted + ", with only " + minSpaceFormatted + " in " + diskCacheDir.path)
                        break
                    }
                }

                // 创建文件测试
                try {
                    if (testCreateFile(diskCacheDir)) {
                        return diskCacheDir
                    } else {
                        unableCreateFileException =
                            IOException("Unable create file in " + diskCacheDir.path)
                        expandCount++
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    unableCreateFileException =
                        IOException(e.javaClass.simpleName + ": " + e.message)
                    expandCount++
                }
            }
        }
        when {
            noSpaceException != null -> {
                throw noSpaceException
            }
            unableCreateFileException != null -> {
                throw unableCreateFileException
            }
            else -> {
                throw IOException("Unable create dir: " + if (diskCacheDir != null) diskCacheDir.path else "null")
            }
        }
    }

    private fun getAppCacheDir(context: Context): File? {
        var appCacheDir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            appCacheDir = context.externalCacheDir
        }
        if (appCacheDir == null) {
            appCacheDir = context.cacheDir
        }
        return appCacheDir
    }

    private fun getProcessName(context: Context): String? {
        val pid = Process.myPid()
        val runningApps =
            ((context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?)?.runningAppProcesses)
                ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName
            }
        }
        return null
    }

    private fun getSimpleProcessName(context: Context): String? {
        val processName = getProcessName(context) ?: return null
        val packageName = context.packageName
        val lastIndex = processName.lastIndexOf(packageName)
        return if (lastIndex != -1) processName.substring(lastIndex + packageName.length) else null
    }

    private fun appendProcessName(context: Context, dirName: String): String {
        // 目录名字加上进程名字的后缀，不同的进程不同目录，以兼容多进程
        var newDirName = dirName
        val simpleProcessName = getSimpleProcessName(context)
        if (simpleProcessName != null) {
            try {
                newDirName += URLEncoder.encode(simpleProcessName, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }
        return newDirName
    }

    private fun cleanDir(dir: File?): Boolean {
        if (dir == null || !dir.exists() || !dir.isDirectory) {
            return true
        }
        val files = dir.listFiles()
        var cleanSuccess = true
        if (files != null) {
            for (tempFile in files) {
                if (tempFile.isDirectory) {
                    cleanSuccess = cleanSuccess and cleanDir(tempFile)
                }
                cleanSuccess = cleanSuccess and tempFile.delete()
            }
        }
        return cleanSuccess
    }

    private fun getAvailableBytes(dir: File): Long {
        if (!dir.exists() && !dir.mkdirs()) {
            return 0
        }
        val dirStatFs: StatFs = try {
            StatFs(dir.path)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return 0
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            dirStatFs.availableBytes
        } else {
            dirStatFs.availableBlocks.toLong() * dirStatFs.blockSize
        }
    }

    private fun testCreateFile(cacheDir: File): Boolean {
        var parentDir: File? = cacheDir
        while (parentDir != null) {
            // 先向上找到一个已存在的目录
            if (!parentDir.exists()) {
                parentDir = cacheDir.parentFile
                continue
            }

            // 然后尝试创建文件
            val file = File(parentDir, "create_test.temp")

            // 已存在就先删除，删除失败就抛异常
            if (file.exists() && !file.delete()) {
                throw Exception("Delete old test file failed: " + file.path)
            }
            file.createNewFile()
            return if (file.exists()) {
                if (file.delete()) {
                    true
                } else {
                    throw Exception("Delete test file failed: " + file.path)
                }
            } else {
                false
            }
        }
        return false
    }
}