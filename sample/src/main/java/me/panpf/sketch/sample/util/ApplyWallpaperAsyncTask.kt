package me.panpf.sketch.sample.util

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

/**
 * 应用壁纸异步任务
 */
class ApplyWallpaperAsyncTask(context: Context, private val imageFile: File) : AsyncTask<Int, Int, Boolean>() {
    @SuppressLint("StaticFieldLeak")
    private val context: Context = context.applicationContext

    override fun doInBackground(vararg params: Int?): Boolean {
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(imageFile)
            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperManager.setStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return true
    }

    override fun onPostExecute(aBoolean: Boolean) {
        Toast.makeText(context, if (aBoolean) "设置壁纸成功" else "设置壁纸失败", Toast.LENGTH_LONG).show()
    }
}
