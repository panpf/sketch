package me.panpf.sketch.sample.bean

import android.content.Context
import android.os.AsyncTask
import android.text.format.Formatter
import me.panpf.adapter.AssemblyAdapter
import me.panpf.sketch.Sketch
import me.panpf.sketch.sample.event.CacheCleanEvent
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

class CacheInfoMenu(val context: Context, val type: String, title: String) : InfoMenu(title) {
    override fun getInfo(): String {
        when (type) {
            "Memory" -> {
                val memoryCache = Sketch.with(context).configuration.memoryCache
                val usedSizeFormat = Formatter.formatFileSize(context, memoryCache.size)
                val maxSizeFormat = Formatter.formatFileSize(context, memoryCache.maxSize)
                return "$usedSizeFormat/$maxSizeFormat"
            }
            "Disk" -> {
                val diskCache = Sketch.with(context).configuration.diskCache
                val usedSizeFormat = Formatter.formatFileSize(context, diskCache.size)
                val maxSizeFormat = Formatter.formatFileSize(context, diskCache.maxSize)
                return "$usedSizeFormat/$maxSizeFormat"
            }
            "BitmapPool" -> {
                val bitmapPool = Sketch.with(context).configuration.bitmapPool
                val usedSizeFormat = Formatter.formatFileSize(context, bitmapPool.size.toLong())
                val maxSizeFormat = Formatter.formatFileSize(context, bitmapPool.maxSize.toLong())
                return "$usedSizeFormat/$maxSizeFormat"
            }
            else -> return "Unknown Type"
        }
    }

    override fun onClick(adapter: AssemblyAdapter?) {
        when (type) {
            "Memory" -> {
                Sketch.with(context).configuration.memoryCache.clear()
                adapter?.notifyDataSetChanged()

                EventBus.getDefault().post(CacheCleanEvent())
            }
            "Disk" -> {
                CleanCacheTask(WeakReference(context), adapter).execute(0)
            }
            "BitmapPool" -> {
                Sketch.with(context).configuration.bitmapPool.clear()
                adapter?.notifyDataSetChanged()

                EventBus.getDefault().post(CacheCleanEvent())
            }
        }
    }

    class CleanCacheTask(val reference: WeakReference<Context>, val adapter: AssemblyAdapter?) : AsyncTask<Int, Int, Int>() {

        override fun doInBackground(vararg params: Int?): Int? {
            reference.get()?.let { Sketch.with(it).configuration.diskCache.clear() }

            return null
        }

        override fun onPostExecute(integer: Int?) {
            super.onPostExecute(integer)

            reference.get()?.let {
                adapter?.notifyDataSetChanged()
                EventBus.getDefault().post(CacheCleanEvent())
            }
        }
    }
}