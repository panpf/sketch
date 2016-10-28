package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;

public class MenuTitleItemFactory extends AssemblyRecyclerItemFactory<MenuTitleItemFactory.MenuTitleItem> {
    @Override
    public boolean isTarget(Object o) {
        return o instanceof String;
    }

    @Override
    public MenuTitleItem createAssemblyItem(ViewGroup viewGroup) {
        return new MenuTitleItem(R.layout.list_item_menu_title, viewGroup);
    }

    public class MenuTitleItem extends BindAssemblyRecyclerItem<String> {
        @BindView(R.id.text_menuTitleItem_title)
        TextView textView;

        public MenuTitleItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {

        }

        @Override
        protected void onSetData(int i, String title) {
            textView.setText(title);
        }
    }
}
