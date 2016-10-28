package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;
import me.xiaopan.sketchsample.bean.InfoMenu;

public class InfoMenuItemFactory extends AssemblyRecyclerItemFactory<InfoMenuItemFactory.InfoMenuItem> {

    @Override
    public boolean isTarget(Object o) {
        return o instanceof InfoMenu;
    }

    @Override
    public InfoMenuItem createAssemblyItem(ViewGroup viewGroup) {
        return new InfoMenuItem(R.layout.list_item_info_menu, viewGroup);
    }

    public class InfoMenuItem extends BindAssemblyRecyclerItem<InfoMenu> {
        @BindView(R.id.text_infoMenuItem_title)
        TextView titleTextView;

        @BindView(R.id.text_infoMenuItem_info)
        TextView infoTextView;

        public InfoMenuItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @OnClick(R.id.layout_infoMenuItem_root)
        void clickItem() {
            getData().onClick(getAdapter());
        }

        @Override
        protected void onConfigViews(Context context) {

        }

        @Override
        protected void onSetData(int i, InfoMenu infoMenu) {
            titleTextView.setText(infoMenu.title);
            infoTextView.setText(infoMenu.getInfo());
        }
    }
}
