package me.xiaopan.spear.sample.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.xiaopan.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.spear.sample.adapter.InstalledAppListAdapter;
import me.xiaopan.spear.sample.bean.AppInfo;
import me.xiaopan.spear.sample.util.ScrollingPauseLoadManager;

/**
 * 已安装APP列表
 */
@InjectContentView(R.layout.fragment_installed_app)
public class InstalledAppFragment extends InjectFragment{
    @InjectView(R.id.recyclerView_installedApp_content) private RecyclerView contentRecyclerView;
    private InstalledAppListAdapter adapter = null;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        contentRecyclerView.setOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));

        if(adapter != null){
            contentRecyclerView.setAdapter(adapter);
            contentRecyclerView.scheduleLayoutAnimation();
        }else{
            loadAppList();
        }
    }

    private void loadAppList(){
        new AsyncTask<Integer, Integer, List<AppInfo>>(){
            private Context context = getActivity().getBaseContext();

            @Override
            protected List<AppInfo> doInBackground(Integer... params) {
                PackageManager packageManager = context.getPackageManager();
                List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
                List<AppInfo> appInfoList = new ArrayList<>(packageInfoList.size());
                for(PackageInfo packageInfo : packageInfoList){
                    AppInfo appInfo = new AppInfo();
                    appInfo.setName(String.valueOf(packageInfo.applicationInfo.loadLabel(packageManager)));
                    appInfo.setSortName(toPinYin(appInfo.getName()));
                    appInfo.setId(packageInfo.packageName);
                    appInfo.setVersionName(packageInfo.versionName);
                    appInfo.setApkFilePath(packageInfo.applicationInfo.publicSourceDir);
                    appInfo.setAppSize(Formatter.formatFileSize(context, new File(appInfo.getApkFilePath()).length()));
                    appInfoList.add(appInfo);
                }

                Collections.sort(appInfoList, new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo lhs, AppInfo rhs) {
                        return lhs.getSortName().compareToIgnoreCase(rhs.getSortName());
                    }
                });

                return appInfoList;
            }

            private String toPinYin(String text){
                StringBuilder stringBuilder = new StringBuilder();
                for(char c : text.toCharArray()){
                    String[] a = PinyinHelper.toHanyuPinyinStringArray(c);
                    if(a != null){
                        stringBuilder.append(a[0]);
                    }else{
                        stringBuilder.append(c);
                    }
                }
                return stringBuilder.toString();
            }

            @Override
            protected void onPostExecute(List<AppInfo> appInfoList) {
                if(getActivity() == null){
                    return;
                }

                adapter = new InstalledAppListAdapter(getActivity(), appInfoList);
                contentRecyclerView.setAdapter(adapter);
                contentRecyclerView.scheduleLayoutAnimation();
            }
        }.execute(0);
    }
}
