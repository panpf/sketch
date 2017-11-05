package me.panpf.sketch.sample.adapter.itemfactory

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import me.xiaopan.assemblyadapter.AssemblyRecyclerItem
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.InfoMenu
import me.panpf.sketch.sample.bindView

class InfoMenuItemFactory : AssemblyRecyclerItemFactory<InfoMenuItemFactory.InfoMenuItem>() {

    override fun isTarget(o: Any): Boolean {
        return o is InfoMenu
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): InfoMenuItem {
        return InfoMenuItem(R.layout.list_item_info_menu, viewGroup)
    }

    inner class InfoMenuItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyRecyclerItem<InfoMenu>(itemLayoutId, parent) {
        val titleTextView: TextView by bindView(R.id.text_infoMenuItem_title)
        val infoTextView: TextView by bindView(R.id.text_infoMenuItem_info)

        override fun onConfigViews(context: Context) {
            getItemView().setOnClickListener {
                data.onClick(adapter)
            }
        }

        override fun onSetData(i: Int, infoMenu: InfoMenu) {
            titleTextView.text = infoMenu.title
            infoTextView.text = infoMenu.getInfo()
        }
    }
}
