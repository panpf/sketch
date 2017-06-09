package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;
import me.xiaopan.sketchsample.bean.AppScanning;

public class AppScanningItemFactory extends AssemblyRecyclerItemFactory<AppScanningItemFactory.AppListHeaderItem> {
    @Override
    public boolean isTarget(Object o) {
        return o instanceof AppScanning;
    }

    @Override
    public AppListHeaderItem createAssemblyItem(ViewGroup viewGroup) {
        return new AppListHeaderItem(R.layout.list_item_app_scanning, viewGroup);
    }

    public class AppListHeaderItem extends BindAssemblyRecyclerItem<AppScanning> {
        @BindView(R.id.text_appScanningItem)
        TextView textView;

        @BindView(R.id.progress_appScanningItem)
        ProgressBar progressBar;

        public AppListHeaderItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {

        }

        @Override
        protected void onSetData(int i, AppScanning scanning) {
            if (scanning.running) {
                int progress = scanning.totalLength > 0 ? (int) ((float) scanning.completedLength / scanning.totalLength * 100) : 0;
                textView.setText(String.format("已发现%d个安装包, %d%%", scanning.count, progress));
                progressBar.setVisibility(View.VISIBLE);
            } else {
                textView.setText(String.format("共发现%d个安装包，用时%d秒", scanning.count, scanning.time / 1000));
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
