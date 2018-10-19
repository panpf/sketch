package me.panpf.sketch.sample.bean

import android.content.Context
import android.view.View
import me.panpf.adapter.AssemblyAdapter
import me.panpf.sketch.sample.AppConfig

class CheckMenu(private val context: Context, var title: String, private val key: AppConfig.Key,
                private val onCheckedChangedListener: OnCheckedChangedListener?, private val onClickListener: View.OnClickListener?) {

    val isChecked: Boolean
        get() = AppConfig.getBoolean(context, key)

    fun onClick(adapter: AssemblyAdapter?) {
        val newChecked = !isChecked

        onCheckedChangedListener?.onCheckedChangedBefore(newChecked)

        AppConfig.putBoolean(context, key, newChecked)
        adapter?.notifyDataSetChanged()

        onCheckedChangedListener?.onCheckedChanged(newChecked)
        onClickListener?.onClick(null)
    }

    interface OnCheckedChangedListener {
        fun onCheckedChangedBefore(checked: Boolean)

        fun onCheckedChanged(checked: Boolean)
    }
}
