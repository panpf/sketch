package me.panpf.sketch.sample.item

import android.content.Context
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.CheckMenu

class CheckMenuItem(parent: ViewGroup, val factory: Factory) : AssemblyItem<CheckMenu>(R.layout.list_item_check_box_menu, parent) {
    private val textView: TextView by bindView(R.id.text_checkBoxMenuItem)
    private val checkBox: CheckBox by bindView(R.id.checkBox_checkBoxMenuItem)

    override fun onConfigViews(context: Context) {
        itemView.setOnClickListener {
            data?.onClick(factory.adapter)
        }
    }

    override fun onSetData(i: Int, checkMenu: CheckMenu?) {
        textView.text = checkMenu?.title
        checkBox.isChecked = checkMenu?.isChecked ?: false
    }

    class Factory : AssemblyItemFactory<CheckMenu>() {

        override fun match(o: Any?): Boolean {
            return o is CheckMenu
        }

        override fun createAssemblyItem(viewGroup: ViewGroup): CheckMenuItem {
            return CheckMenuItem(viewGroup, this)
        }
    }
}
