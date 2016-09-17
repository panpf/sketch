package me.xiaopan.sketchsample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.OptionsType;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.bean.AppInfo;
import me.xiaopan.sketchsample.util.Settings;
import me.xiaopan.sketchsample.widget.MyImageView;

/**
 * 已安装APP列表适配器
 */
public class InstalledAppListAdapter extends RecyclerView.Adapter {
    private List<AppInfo> appInfoList;
    private Settings settings;

    public InstalledAppListAdapter(Context context, List<AppInfo> appInfoList) {
        this.appInfoList = appInfoList;
        this.settings = Settings.with(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app_list_header, parent, false));
        } else {
            AppInfoViewHolder appInfoViewHolder = new AppInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app, parent, false));
            appInfoViewHolder.iconSketchImageView.setOptionsByName(OptionsType.APP_ICON);
            appInfoViewHolder.iconSketchImageView.setImageShape(SketchImageView.ImageShape.ROUNDED_RECT);
            appInfoViewHolder.iconSketchImageView.setImageShapeCornerRadius(SketchUtils.dp2px(parent.getContext(), 10));
            appInfoViewHolder.iconSketchImageView.setAutoApplyGlobalAttr(false);
            return appInfoViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.appCountTextView.setText("您的设备上共安装了" + (appInfoList != null ? appInfoList.size() : 0) + "款应用");
        } else {
            AppInfo appInfo = appInfoList.get(position - 1);
            AppInfoViewHolder appInfoViewHolder = (AppInfoViewHolder) holder;

            appInfoViewHolder.iconSketchImageView.displayInstalledAppIcon(appInfo.getId(), appInfo.getVersionCode());

            appInfoViewHolder.nameTextView.setText(appInfo.getName());
            appInfoViewHolder.infoTextView.setText("v" + appInfo.getVersionName() + "  |  " + appInfo.getAppSize());

            appInfoViewHolder.iconSketchImageView.setShowPressedStatus(settings.isShowPressedStatus());
            appInfoViewHolder.iconSketchImageView.setShowImageFrom(settings.isShowImageFromFlag());
        }
    }

    @Override
    public int getItemCount() {
        return appInfoList != null ? appInfoList.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    public static class AppInfoViewHolder extends RecyclerView.ViewHolder {
        private MyImageView iconSketchImageView;
        private TextView nameTextView;
        private TextView infoTextView;

        public AppInfoViewHolder(View itemView) {
            super(itemView);

            iconSketchImageView = (MyImageView) itemView.findViewById(R.id.image_installedApp_icon);
            nameTextView = (TextView) itemView.findViewById(R.id.text_installedApp_name);
            infoTextView = (TextView) itemView.findViewById(R.id.text_installedApp_info);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView appCountTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            appCountTextView = (TextView) itemView;
        }
    }
}
