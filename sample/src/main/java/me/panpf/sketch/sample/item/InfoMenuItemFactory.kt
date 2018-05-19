package me.panpf.sketch.sample.item

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.InfoMenu

class InfoMenuItemFactory : AssemblyItemFactory<InfoMenu>() {

    override fun match(o: Any?): Boolean {
        return o is InfoMenu
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): InfoMenuItem {
        return InfoMenuItem(R.layout.list_item_info_menu, viewGroup)
    }

    inner class InfoMenuItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyItem<InfoMenu>(itemLayoutId, parent) {
        private val titleTextView: TextView by bindView(R.id.text_infoMenuItem_title)
        private val infoTextView: TextView by bindView(R.id.text_infoMenuItem_info)

        override fun onConfigViews(context: Context) {
            itemView.setOnClickListener {
                data?.onClick(adapter)
            }
        }

        override fun onSetData(i: Int, infoMenu: InfoMenu?) {
            titleTextView.text = infoMenu?.title
            infoTextView.text = infoMenu?.getInfo()
        }
    }
}
