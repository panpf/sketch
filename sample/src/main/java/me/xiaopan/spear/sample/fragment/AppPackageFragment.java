package me.xiaopan.spear.sample.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import me.xiaopan.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.spear.sample.adapter.AppPackageListAdapter;
import me.xiaopan.spear.sample.bean.AppInfo;
import me.xiaopan.spear.sample.util.ScrollingPauseLoadManager;

/**
 * 本地安装包页面
 */
@InjectContentView(R.layout.fragment_installed_app)
public class AppPackageFragment extends InjectFragment{
    @InjectView(R.id.recyclerView_installedApp_content) private RecyclerView contentRecyclerView;
    private AppPackageListAdapter adapter = null;

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
            private long time;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                time = System.currentTimeMillis();
            }

            @Override
            protected List<AppInfo> doInBackground(Integer... params) {
                if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                    return null;
                }

                PackageManager packageManager = context.getPackageManager();
                List<AppInfo> appInfoList = new ArrayList<>();

                Queue<File> dirs = new LinkedBlockingQueue<File>();
                dirs.add(Environment.getExternalStorageDirectory());
                File dir;
                File[] childDirs;
                while(!dirs.isEmpty()){
                    if(getActivity() == null){
                        break;
                    }

                    dir = dirs.poll();
                    if(dir == null){
                        continue;
                    }

                    childDirs = dir.listFiles();
                    if(childDirs == null || childDirs.length == 0){
                        continue;
                    }

                    for(File childFile : childDirs){
                        if(getActivity() == null){
                            break;
                        }

                        if(childFile.isDirectory()){
                            dirs.add(childFile);
                            continue;
                        }

                        if(!(childFile.getName().endsWith(".apk") || childFile.getName().endsWith(".APK"))){
                            continue;
                        }

                        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(childFile.getPath(), PackageManager.GET_ACTIVITIES);
                        if(packageInfo == null){
                            continue;
                        }
                        packageInfo.applicationInfo.sourceDir = childFile.getPath();
                        packageInfo.applicationInfo.publicSourceDir = childFile.getPath();

                        AppInfo appInfo = new AppInfo();
                        appInfo.setName(String.valueOf(packageInfo.applicationInfo.loadLabel(packageManager)));
                        appInfo.setSortName(toPinYin(appInfo.getName()));
                        appInfo.setId(packageInfo.packageName);
                        appInfo.setVersionName(packageInfo.versionName);
                        appInfo.setApkFilePath(childFile.getPath());
                        appInfo.setAppSize(Formatter.formatFileSize(context, childFile.length()));
                        appInfoList.add(appInfo);
                    }
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

                adapter = new AppPackageListAdapter(getActivity(), appInfoList);
                adapter.setUseTime(System.currentTimeMillis() - time);
                contentRecyclerView.setAdapter(adapter);
                contentRecyclerView.scheduleLayoutAnimation();
            }
        }.execute(0);
    }
}
