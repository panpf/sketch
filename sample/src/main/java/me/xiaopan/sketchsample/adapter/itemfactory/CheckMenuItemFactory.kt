package me.xiaopan.sketchsample.adapter.itemfactory

import android.content.Context
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import me.xiaopan.assemblyadapter.AssemblyRecyclerItem
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.bean.CheckMenu
import me.xiaopan.ssvt.bindView

class CheckMenuItemFactory : AssemblyRecyclerItemFactory<CheckMenuItemFactory.CheckMenuItem>() {

    override fun isTarget(o: Any): Boolean {
        return o is CheckMenu
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): CheckMenuItem {
        return CheckMenuItem(R.layout.list_item_check_box_menu, viewGroup)
    }

    inner class CheckMenuItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyRecyclerItem<CheckMenu>(itemLayoutId, parent) {
        val textView: TextView by bindView(R.id.text_checkBoxMenuItem)
        val checkBox: CheckBox by bindView(R.id.checkBox_checkBoxMenuItem)

        override fun onConfigViews(context: Context) {
            getItemView().setOnClickListener {
                data.onClick(adapter)
            }
        }

        override fun onSetData(i: Int, checkMenu: CheckMenu) {
            textView.text = checkMenu.title
            checkBox.isChecked = checkMenu.isChecked
        }
    }
}
