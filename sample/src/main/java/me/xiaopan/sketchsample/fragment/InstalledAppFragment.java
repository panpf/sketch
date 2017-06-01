package me.xiaopan.sketchsample.fragment;

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
@BindContentView(R.layout.fragment_installed_app)
public class InstalledAppFragment extends BaseFragment {
    @BindView(R.id.recyclerView_installedApp_content)
    RecyclerView contentRecyclerView;

    @BindView(R.id.hint_installedApp_hint)
    HintView hintView;

    private AssemblyRecyclerAdapter adapter = null;

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

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                hintView.loading("正在加载已安装列表，请稍后...");
            }

            @Override
            protected List<AppInfo> doInBackground(Integer... params) {
                PackageManager packageManager = context.getPackageManager();
                List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
                List<AppInfo> appInfoList = new ArrayList<AppInfo>(packageInfoList.size());
                for (PackageInfo packageInfo : packageInfoList) {
                    AppInfo appInfo = new AppInfo(true);
                    appInfo.setName(String.valueOf(packageInfo.applicationInfo.loadLabel(packageManager)));
                    appInfo.setSortName(toPinYin(appInfo.getName()));
                    appInfo.setId(packageInfo.packageName);
                    appInfo.setVersionName(packageInfo.versionName);
                    appInfo.setApkFilePath(packageInfo.applicationInfo.publicSourceDir);
                    appInfo.setAppSize(Formatter.formatFileSize(context, new File(appInfo.getApkFilePath()).length()));
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

                List<Object> dataList = new ArrayList<Object>((appInfoList != null ? appInfoList.size() : 0) + 1);
                dataList.add(String.format("您的设备上共安装了%d款应用", appInfoList != null ? appInfoList.size() : 0));
                if (appInfoList != null) {
                    dataList.addAll(appInfoList);
                }
                AssemblyRecyclerAdapter adapter = new AssemblyRecyclerAdapter(dataList);
                adapter.addItemFactory(new AppItemFactory());
                adapter.addItemFactory(new AppListHeaderItemFactory());
                contentRecyclerView.setAdapter(adapter);
                contentRecyclerView.scheduleLayoutAnimation();
            }
        }.execute(0);
    }
}
