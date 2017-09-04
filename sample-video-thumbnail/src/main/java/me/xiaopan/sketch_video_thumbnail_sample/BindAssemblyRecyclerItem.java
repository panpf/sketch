package me.xiaopan.sketch_video_thumbnail_sample;

import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItem;

public abstract class BindAssemblyRecyclerItem<T> extends AssemblyRecyclerItem<T> {
    public BindAssemblyRecyclerItem(int itemLayoutId, ViewGroup parent) {
        super(itemLayoutId, parent);
    }

    public BindAssemblyRecyclerItem(View itemView) {
        super(itemView);
    }

    @Override
    protected void onFindViews() {
        ButterKnife.bind(this, getItemView());
    }
}
