package me.panpf.sketch.sample.adapter.itemfactory

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.sketch.sample.R

class MenuTitleItemFactory : AssemblyItemFactory<String>() {
    override fun match(o: Any?): Boolean {
        return o is String
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): MenuTitleItem {
        return MenuTitleItem(R.layout.list_item_menu_title, viewGroup)
    }

    inner class MenuTitleItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyItem<String>(itemLayoutId, parent) {
        private val textView: TextView by bindView(R.id.text_menuTitleItem_title)

        override fun onConfigViews(context: Context) {

        }

        override fun onSetData(i: Int, title: String?) {
            textView.text = title
        }
    }
}
