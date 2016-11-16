package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.ViewGroup;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;
import me.xiaopan.sketchsample.widget.MyImageView;

public class StarHeaderItemFactory extends AssemblyRecyclerItemFactory<StarHeaderItemFactory.StarHeaderItem>{

    @Override
    public boolean isTarget(Object o) {
        return o instanceof String;
    }

    @Override
    public StarHeaderItem createAssemblyItem(ViewGroup viewGroup) {
        return new StarHeaderItem(R.layout.list_item_heade_image, viewGroup);
    }

    public class StarHeaderItem extends BindAssemblyRecyclerItem<String> {
        @BindView(R.id.image_headImageItem)
        MyImageView headImageView;

        public StarHeaderItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
            fullSpanInStaggeredGrid();
        }

        @Override
        protected void onConfigViews(Context context) {
            headImageView.setOptionsByName(ImageOptions.RECT);

            ViewGroup.LayoutParams headerParams = headImageView.getLayoutParams();
            headerParams.width = context.getResources().getDisplayMetrics().widthPixels;
            headerParams.height = (int) (headerParams.width / 3.2f);
            headImageView.setLayoutParams(headerParams);
        }

        @Override
        protected void onSetData(int i, String s) {
            headImageView.displayImage(s);
        }
    }
}
