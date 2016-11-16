package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;

public class AppListHeaderItemFactory extends AssemblyRecyclerItemFactory<AppListHeaderItemFactory.AppListHeaderItem>{
    @Override
    public boolean isTarget(Object o) {
        return o instanceof String;
    }

    @Override
    public AppListHeaderItem createAssemblyItem(ViewGroup viewGroup) {
        return new AppListHeaderItem(R.layout.list_item_app_list_header, viewGroup);
    }

    public class AppListHeaderItem extends BindAssemblyRecyclerItem<String> {
        @BindView(R.id.text_appListHeaderItem)
        TextView textView;

        public AppListHeaderItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {

        }

        @Override
        protected void onSetData(int i, String s) {
            textView.setText(s);
        }
    }
}
