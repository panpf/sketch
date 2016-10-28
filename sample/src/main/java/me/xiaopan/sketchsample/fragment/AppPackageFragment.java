package me.xiaopan.sketchsample.fragment;

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
import java.util.zip.ZipFile;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.AppPackageListAdapter;
import me.xiaopan.sketchsample.bean.AppInfo;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.util.XpkInfo;
import me.xiaopan.sketchsample.widget.HintView;

/**
 * 本地安装包页面
 */
@InjectContentView(R.layout.fragment_installed_app)
public class AppPackageFragment extends MyFragment {
    @InjectView(R.id.recyclerView_installedApp_content)
    private RecyclerView contentRecyclerView;
    @InjectView(R.id.hint_installedApp_hint)
    private HintView hintView;
    private AppPackageListAdapter adapter = null;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        contentRecyclerView.setOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));

        if (adapter != null) {
            contentRecyclerView.setAdapter(adapter);
            contentRecyclerView.scheduleLayoutAnimation();
        } else {
            loadAppList();
        }
    }

    private void loadAppList() {
        new AsyncTask<Integer, Integer, List<AppInfo>>() {
            private Context context = getActivity().getBaseContext();
            private long time;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                hintView.loading("正在扫描本地APK文件，请稍后...");
                time = System.currentTimeMillis();
            }

            @Override
            protected List<AppInfo> doInBackground(Integer... params) {
                if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    return null;
                }

                PackageManager packageManager = context.getPackageManager();
                List<AppInfo> appInfoList = new ArrayList<AppInfo>();

                Queue<File> dirs = new LinkedBlockingQueue<File>();
                dirs.add(Environment.getExternalStorageDirectory());
                File dir;
                File[] childDirs;
                while (!dirs.isEmpty()) {
                    if (getActivity() == null) {
                        break;
                    }

                    dir = dirs.poll();
                    if (dir == null) {
                        continue;
                    }

                    childDirs = dir.listFiles();
                    if (childDirs == null || childDirs.length == 0) {
                        continue;
                    }

                    for (File childFile : childDirs) {
                        if (getActivity() == null) {
                            break;
                        }

                        if (childFile.isDirectory()) {
                            dirs.add(childFile);
                            continue;
                        }

                        String fileName = childFile.getName();
                        if (SketchUtils.checkSuffix(fileName, ".apk")) {
                            AppInfo appInfo = parseFromApk(context, childFile);
                            if (appInfo != null) {
                                appInfoList.add(appInfo);
                            }
                        } else if (SketchUtils.checkSuffix(fileName, ".xpk")) {
                            AppInfo appInfo = parseFromXpk(childFile);
                            if (appInfo != null) {
                                appInfoList.add(appInfo);
                            }
                        }
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

            private String toPinYin(String text) {
                StringBuilder stringBuilder = new StringBuilder();
                for (char c : text.toCharArray()) {
                    String[] a = PinyinHelper.toHanyuPinyinStringArray(c);
                    if (a != null) {
                        stringBuilder.append(a[0]);
                    } else {
                        stringBuilder.append(c);
                    }
                }
                return stringBuilder.toString();
            }

            private AppInfo parseFromApk(Context context, File file) {
                PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
                if (packageInfo == null) {
                    return null;
                }
                packageInfo.applicationInfo.sourceDir = file.getPath();
                packageInfo.applicationInfo.publicSourceDir = file.getPath();

                AppInfo appInfo = new AppInfo();
                appInfo.setName(String.valueOf(packageInfo.applicationInfo.loadLabel(context.getPackageManager())));
                appInfo.setSortName(toPinYin(appInfo.getName()));
                appInfo.setId(packageInfo.packageName);
                appInfo.setVersionName(packageInfo.versionName);
                appInfo.setApkFilePath(file.getPath());
                appInfo.setAppSize(Formatter.formatFileSize(context, file.length()));

                return appInfo;
            }

            private AppInfo parseFromXpk(File file) {
                try {
                    AppInfo appInfo = new AppInfo();
                    XpkInfo xpkInfo = XpkInfo.getXPKManifestDom(new ZipFile(file));
                    if (xpkInfo == null) {
                        throw new Exception();
                    }

                    appInfo.setName(xpkInfo.getAppName());
                    appInfo.setSortName(toPinYin(appInfo.getName()));
                    appInfo.setId(xpkInfo.getPackageName());
                    appInfo.setVersionName(xpkInfo.getVersionName());
                    appInfo.setApkFilePath(file.getPath());
                    appInfo.setAppSize(Formatter.formatFileSize(context, file.length()));

                    return appInfo;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<AppInfo> appInfoList) {
                if (getActivity() == null) {
                    return;
                }

                hintView.hidden();
                adapter = new AppPackageListAdapter(appInfoList);
                adapter.setUseTime(System.currentTimeMillis() - time);
                contentRecyclerView.setAdapter(adapter);
                contentRecyclerView.scheduleLayoutAnimation();
            }
        }.execute(0);
    }
}
