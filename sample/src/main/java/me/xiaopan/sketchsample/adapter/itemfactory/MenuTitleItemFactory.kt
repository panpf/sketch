package me.xiaopan.sketchsample.adapter.itemfactory

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import me.xiaopan.assemblyadapter.AssemblyRecyclerItem
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory
import me.xiaopan.sketchsample.R
import me.xiaopan.ssvt.bindView

class MenuTitleItemFactory : AssemblyRecyclerItemFactory<MenuTitleItemFactory.MenuTitleItem>() {
    override fun isTarget(o: Any): Boolean {
        return o is String
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): MenuTitleItem {
        return MenuTitleItem(R.layout.list_item_menu_title, viewGroup)
    }

    inner class MenuTitleItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyRecyclerItem<String>(itemLayoutId, parent) {
        val textView: TextView by bindView(R.id.text_menuTitleItem_title)

        override fun onConfigViews(context: Context) {

        }

        override fun onSetData(i: Int, title: String) {
            textView.text = title
        }
    }
}
