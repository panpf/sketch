package me.xiaopan.sketchsample.fragment;

import android.content.Context;
import android.content.Intent;
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

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.itemfactory.AppItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.AppListHeaderItemFactory;
import me.xiaopan.sketchsample.bean.AppInfo;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.widget.HintView;

/**
 * 已安装APP列表
 */
@BindContentView(R.layout.fragment_recycler)
public class InstalledAppFragment extends BaseFragment implements AppItemFactory.AppItemListener {
    @BindView(R.id.recycler_recyclerFragment_content)
    RecyclerView recyclerView;

    @BindView(R.id.hint_recyclerFragment)
    HintView hintView;

    private AssemblyRecyclerAdapter adapter = null;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));

        if (adapter != null) {
            recyclerView.setAdapter(adapter);
            recyclerView.scheduleLayoutAnimation();
        } else {
            loadAppList();
        }
    }

    private void loadAppList() {
        new AsyncTask<Integer, Integer, List<AppInfo>>() {
            private Context context = getActivity().getBaseContext();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                hintView.loading(null);
            }

            @Override
            protected List<AppInfo> doInBackground(Integer... params) {
                PackageManager packageManager = context.getPackageManager();
                List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
                List<AppInfo> appInfoList = new ArrayList<>(packageInfoList.size());
                for (PackageInfo packageInfo : packageInfoList) {
                    AppInfo appInfo = new AppInfo(true);
                    appInfo.setName(String.valueOf(packageInfo.applicationInfo.loadLabel(packageManager)));
                    appInfo.setPackageName(packageInfo.packageName);
                    appInfo.setSortName(toPinYin(appInfo.getName()));
                    appInfo.setId(packageInfo.packageName);
                    appInfo.setVersionName(packageInfo.versionName);
                    appInfo.setApkFilePath(packageInfo.applicationInfo.publicSourceDir);
                    appInfo.setFormattedAppSize(Formatter.formatFileSize(context, new File(appInfo.getApkFilePath()).length()));
                    appInfo.setVersionCode(packageInfo.versionCode);
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

            @Override
            protected void onPostExecute(List<AppInfo> appInfoList) {
                if (getActivity() == null) {
                    return;
                }

                hintView.hidden();

                List<Object> dataList = new ArrayList<>((appInfoList != null ? appInfoList.size() : 0) + 1);
                dataList.add(String.format("您的设备上共安装了%d款应用", appInfoList != null ? appInfoList.size() : 0));
                if (appInfoList != null) {
                    dataList.addAll(appInfoList);
                }
                AssemblyRecyclerAdapter adapter = new AssemblyRecyclerAdapter(dataList);
                adapter.addItemFactory(new AppItemFactory(InstalledAppFragment.this));
                adapter.addItemFactory(new AppListHeaderItemFactory());
                recyclerView.setAdapter(adapter);
                recyclerView.scheduleLayoutAnimation();
            }
        }.execute(0);
    }

    @Override
    public void onClickApp(int position, AppInfo appInfo) {
        Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(appInfo.getPackageName());
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
