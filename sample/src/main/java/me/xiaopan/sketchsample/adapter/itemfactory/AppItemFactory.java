package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketch.uri.ApkIconUriModel;
import me.xiaopan.sketch.uri.AppIconUriModel;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;
import me.xiaopan.sketchsample.bean.AppInfo;
import me.xiaopan.sketchsample.util.XpkIconUriModel;
import me.xiaopan.sketchsample.widget.SampleImageView;

public class AppItemFactory extends AssemblyRecyclerItemFactory<AppItemFactory.AppItem> {
    private AppItemListener listener;

    public AppItemFactory(AppItemListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof AppInfo;
    }

    @Override
    public AppItem createAssemblyItem(ViewGroup viewGroup) {
        return new AppItem(R.layout.list_item_app, viewGroup);
    }

    public interface AppItemListener {
        void onClickApp(int position, AppInfo appInfo);
    }

    public class AppItem extends BindAssemblyRecyclerItem<AppInfo> {
        @BindView(R.id.image_installedApp_icon)
        SampleImageView iconImageView;
        @BindView(R.id.text_installedApp_name)
        TextView nameTextView;
        @BindView(R.id.text_installedApp_info)
        TextView infoTextView;

        public AppItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {
            iconImageView.setOptions(ImageOptions.ROUND_RECT);
            iconImageView.setPage(SampleImageView.Page.APP_LIST);

            getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClickApp(getPosition(), getData());
                    }
                }
            });
        }

        @Override
        protected void onSetData(int i, AppInfo appInfo) {
            if (appInfo.isTempInstalled()) {
                iconImageView.displayImage(AppIconUriModel.makeUri(appInfo.getId(), appInfo.getVersionCode()));
            } else if (appInfo.isTempXPK()) {
                iconImageView.displayImage(XpkIconUriModel.makeUri(appInfo.getApkFilePath()));
            } else {
                iconImageView.displayImage(ApkIconUriModel.makeUri(appInfo.getApkFilePath()));
            }
            nameTextView.setText(appInfo.getName());
            infoTextView.setText(String.format("v%s  |  %s", appInfo.getVersionName(), appInfo.getFormattedAppSize()));
        }
    }
}
