package me.panpf.sketch.sample.item

import android.view.ViewGroup
import android.widget.TextView
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.sketch.sample.R

class MenuTitleItem(parent: ViewGroup) : AssemblyItem<String>(R.layout.list_item_menu_title, parent) {
    private val textView: TextView by bindView(R.id.text_menuTitleItem_title)

    override fun onSetData(i: Int, title: String?) {
        textView.text = title
    }

    class Factory : AssemblyItemFactory<String>() {
        override fun match(o: Any?): Boolean {
            return o is String
        }

        override fun createAssemblyItem(viewGroup: ViewGroup): MenuTitleItem {
            return MenuTitleItem(viewGroup)
        }
    }
}
