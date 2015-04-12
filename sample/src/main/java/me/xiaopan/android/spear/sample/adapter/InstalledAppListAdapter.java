package me.xiaopan.android.spear.sample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.bean.AppInfo;
import me.xiaopan.android.spear.sample.widget.MyImageView;

/**
 * 已安装APP列表适配器
 */
public class InstalledAppListAdapter extends RecyclerView.Adapter{
    private List<AppInfo> appInfoList;

    public InstalledAppListAdapter(Context context, List<AppInfo> appInfoList) {
        this.appInfoList = appInfoList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0){
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app_list_header, parent, false));
        }else{
            AppInfoViewHolder appInfoViewHolder = new AppInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app, parent, false));
            appInfoViewHolder.iconSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_1);
            return appInfoViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.appCountTextView.setText("您的设备上共安装了"+(appInfoList!=null?appInfoList.size():0)+"款应用");
        }else{
            AppInfo appInfo = appInfoList.get(position-1);
            AppInfoViewHolder appInfoViewHolder = (AppInfoViewHolder) holder;

            appInfoViewHolder.iconSpearImageView.displayImage(appInfo.getApkFilePath());

            appInfoViewHolder.nameTextView.setText(appInfo.getName());
            appInfoViewHolder.infoTextView.setText("v"+appInfo.getVersionName()+"  |  "+appInfo.getAppSize());
        }
    }

    @Override
    public int getItemCount() {
        return appInfoList!=null?appInfoList.size()+1:0;
    }

    @Override
    public int getItemViewType(int position) {
        return position==0?0:1;
    }

    public static class AppInfoViewHolder extends RecyclerView.ViewHolder{
        private MyImageView iconSpearImageView;
        private TextView nameTextView;
        private TextView infoTextView;

        public AppInfoViewHolder(View itemView) {
            super(itemView);

            iconSpearImageView = (MyImageView) itemView.findViewById(R.id.image_installedApp_icon);
            nameTextView = (TextView) itemView.findViewById(R.id.text_installedApp_name);
            infoTextView = (TextView) itemView.findViewById(R.id.text_installedApp_info);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder{
        private TextView appCountTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            appCountTextView = (TextView) itemView;
        }
    }
}
