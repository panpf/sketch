package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;
import me.xiaopan.sketchsample.bean.Star;
import me.xiaopan.sketchsample.widget.MyImageView;

public class StarCatalogItemFactory extends AssemblyRecyclerItemFactory<StarCatalogItemFactory.StarCatalogItem> {

    private int iconSize;
    private OnClickStarListener onClickStarListener;

    public StarCatalogItemFactory(OnClickStarListener onClickStarListener) {
        this.onClickStarListener = onClickStarListener;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof Star;
    }

    @Override
    public StarCatalogItem createAssemblyItem(ViewGroup viewGroup) {
        return new StarCatalogItem(R.layout.list_item_star_catalog, viewGroup);
    }

    public interface OnClickStarListener {
        void onClickImage(Star star);
    }

    public class StarCatalogItem extends BindAssemblyRecyclerItem<Star> {
        @BindView(R.id.image_starCatalogItem_icon)
        MyImageView iconImageView;

        @BindView(R.id.text_starCatalogItem_name)
        TextView nameTextView;

        public StarCatalogItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {
            if (iconSize == 0) {
                int space = (int) context.getResources().getDimension(R.dimen.home_category_margin_border);
                int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
                int itemWidth = (screenWidth - (space * 2)) / 3;
                iconSize = itemWidth - (space * 2);
            }

            iconImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickStarListener != null) {
                        onClickStarListener.onClickImage(getData());
                    }
                }
            });

            iconImageView.setOptionsByName(ImageOptions.CIRCULAR_STROKE);

            iconImageView.setImageShape(SketchImageView.ImageShape.CIRCLE);

            ViewGroup.LayoutParams params = iconImageView.getLayoutParams();
            params.width = iconSize;
            params.height = iconSize;
            iconImageView.setLayoutParams(params);

            params = nameTextView.getLayoutParams();
            params.width = iconSize;
            nameTextView.setLayoutParams(params);

            iconImageView.setUseInList(true);
        }

        @Override
        protected void onSetData(int i, Star star) {
            nameTextView.setText(star.name);
            iconImageView.displayImage(star.avatarUrl);
        }
    }
}
