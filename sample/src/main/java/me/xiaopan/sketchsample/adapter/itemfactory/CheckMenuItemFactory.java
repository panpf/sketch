package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;
import me.xiaopan.sketchsample.bean.CheckMenu;

public class CheckMenuItemFactory extends AssemblyRecyclerItemFactory<CheckMenuItemFactory.CheckMenuItem> {

    @Override
    public boolean isTarget(Object o) {
        return o instanceof CheckMenu;
    }

    @Override
    public CheckMenuItem createAssemblyItem(ViewGroup viewGroup) {
        return new CheckMenuItem(R.layout.list_item_check_box_menu, viewGroup);
    }

    public class CheckMenuItem extends BindAssemblyRecyclerItem<CheckMenu> {
        @BindView(R.id.text_checkBoxMenuItem)
        TextView textView;

        @BindView(R.id.checkBox_checkBoxMenuItem)
        CheckBox checkBox;

        public CheckMenuItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @OnClick(R.id.layout_checkBoxMenuItem_root)
        void clickItem() {
            getData().onClick(getAdapter());
        }

        @Override
        protected void onConfigViews(Context context) {

        }

        @Override
        protected void onSetData(int i, CheckMenu checkMenu) {
            textView.setText(checkMenu.title);
            checkBox.setChecked(checkMenu.isChecked());
        }
    }
}
