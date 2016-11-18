package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;

public class ItemTitleItemFactory extends AssemblyRecyclerItemFactory<ItemTitleItemFactory.ItemTitleItem> {

    @Override
    public boolean isTarget(Object o) {
        return o instanceof String;
    }

    @Override
    public ItemTitleItem createAssemblyItem(ViewGroup viewGroup) {
        return new ItemTitleItem(R.layout.list_item_title, viewGroup);
    }

    public class ItemTitleItem extends BindAssemblyRecyclerItem<String> {
        @BindView(R.id.text_titleItem)
        TextView textView;

        public ItemTitleItem(int itemLayoutId, ViewGroup parent) {
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
