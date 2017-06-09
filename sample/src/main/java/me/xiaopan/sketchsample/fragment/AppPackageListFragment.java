package me.xiaopan.sketchsample.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.zip.ZipFile;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.assemblyadapter.FixedRecyclerItemInfo;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.itemfactory.AppItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.AppScanningItemFactory;
import me.xiaopan.sketchsample.bean.AppInfo;
import me.xiaopan.sketchsample.bean.AppScanning;
import me.xiaopan.sketchsample.util.FileScanner;
import me.xiaopan.sketchsample.util.FileUtils;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.util.XpkInfo;
import me.xiaopan.sketchsample.widget.HintView;

/**
 * 本地安装包页面
 */
@BindContentView(R.layout.fragment_recycler)
public class AppPackageListFragment extends BaseFragment implements AppItemFactory.AppItemListener {
    @BindView(R.id.refresh_recyclerFragment)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.recycler_recyclerFragment_content)
    RecyclerView recyclerView;

    @BindView(R.id.hint_recyclerFragment)
    HintView hintView;

    private AssemblyRecyclerAdapter adapter = null;
    private FileScanner fileScanner;
    private FixedRecyclerItemInfo scanningItemInfo;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout.setEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));

        if (adapter != null) {
            recyclerView.setAdapter(adapter);
            recyclerView.scheduleLayoutAnimation();
        } else {
            loadAppList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fileScanner.isRunning()) {
            fileScanner.cancel();
        }
    }

    private void loadAppList() {
        new AsyncTask<String, Integer, String[]>() {
            @Override
            protected void onPreExecute() {
                fileScanner = new FileScanner(new MyFileChecker(getContext()), new MyScanListener());
                fileScanner.setDirFilter(new MyDirFilter());

                AssemblyRecyclerAdapter adapter = new AssemblyRecyclerAdapter(new ArrayList());
                adapter.addItemFactory(new AppItemFactory(AppPackageListFragment.this));
                scanningItemInfo = adapter.addHeaderItem(new AppScanningItemFactory(), new AppScanning());

                recyclerView.setAdapter(adapter);
                AppPackageListFragment.this.adapter = adapter;
            }

            @Override
            protected String[] doInBackground(String... params) {
                return SketchUtils.getAllAvailableSdcardPath(getContext());
            }

            @Override
            protected void onPostExecute(String[] files) {
                if (files == null || files.length == 0) {
                    new MyScanListener().onCompleted();
                } else {
                    fileScanner.execute(files);
                }
            }
        }.execute("");
    }

    @Override
    public void onClickApp(int position, AppInfo appInfo) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(new File(appInfo.getApkFilePath())), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyScanListener implements FileScanner.ScanListener {
        private long startTime;

        @Override
        public void onStarted() {
            startTime = System.currentTimeMillis();

            AppScanning appScanning = (AppScanning) scanningItemInfo.getData();
            appScanning.running = true;

            scanningItemInfo.setData(appScanning);
        }

        @Override
        public void onScanDir(File dir) {

        }

        @Override
        public void onFindFile(FileScanner.FileItem fileItem) {
            if (fileItem instanceof AppInfo) {
                //noinspection unchecked
                adapter.getDataList().add(fileItem);

                AppScanning appScanning = (AppScanning) scanningItemInfo.getData();
                appScanning.count = adapter.getDataCount();

                scanningItemInfo.setData(appScanning);

                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onUpdateProgress(int totalLength, int completedLength) {
            AppScanning appScanning = (AppScanning) scanningItemInfo.getData();

            appScanning.totalLength = totalLength;
            appScanning.completedLength = completedLength;

            scanningItemInfo.setData(appScanning);
        }

        @Override
        public void onCompleted() {
            AppScanning appScanning = (AppScanning) scanningItemInfo.getData();
            appScanning.running = false;

            appScanning.time = System.currentTimeMillis() - startTime;

            scanningItemInfo.setData(appScanning);
        }

        @Override
        public void onCanceled() {

        }
    }

    private class MyDirFilter implements FileScanner.DirFilter {

        @Override
        public boolean accept(File dir) {
            String fileNameLowerCase = dir.getName().toLowerCase();

            String keyword = ".";
            if (fileNameLowerCase.startsWith(keyword)) {
                return false;
            }

            keyword = "tuniuapp";
            if (keyword.equalsIgnoreCase(fileNameLowerCase)) {
                return false;
            }

            keyword = "cache";
            if (keyword.equalsIgnoreCase(fileNameLowerCase) || fileNameLowerCase.endsWith(keyword)) {
                return false;
            }

            keyword = "log";
            if (keyword.equalsIgnoreCase(fileNameLowerCase) || fileNameLowerCase.endsWith(keyword)) {
                return false;
            }

            keyword = "dump";
            //noinspection RedundantIfStatement
            if (keyword.equalsIgnoreCase(fileNameLowerCase) || fileNameLowerCase.endsWith(keyword)) {
                return false;
            }

            return true;
        }
    }

    private class MyFileChecker implements FileScanner.FileChecker {
        private Context context;

        public MyFileChecker(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        public FileScanner.FileItem accept(File pathname) {
            // 是文件的话根据后缀名判断是APK还是XPK
            String fileNameLowerCase = pathname.getName().toLowerCase();
            if (pathname.isFile()) {
                String suffix = FileUtils.subSuffix(fileNameLowerCase);
                if (".apk".equalsIgnoreCase(suffix)) {
                    return parseFromApk(context, pathname);
                } else if (".xpk".equalsIgnoreCase(suffix)) {
                    return parseFromXpk(pathname);
                } else {
                    return null;
                }
            }

            return null;
        }

        @Override
        public void onFinished() {
        }

        private AppInfo parseFromApk(Context context, File file) {
            PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
            if (packageInfo == null) {
                return null;
            }
            packageInfo.applicationInfo.sourceDir = file.getPath();
            packageInfo.applicationInfo.publicSourceDir = file.getPath();

            AppInfo appInfo = new AppInfo(false);
            appInfo.setName(String.valueOf(packageInfo.applicationInfo.loadLabel(context.getPackageManager())));
            appInfo.setId(packageInfo.packageName);
            appInfo.setVersionName(packageInfo.versionName);
            appInfo.setApkFilePath(file.getPath());
            appInfo.setAppSize(file.length());
            appInfo.setFormattedAppSize(Formatter.formatFileSize(context, appInfo.getAppSize()));

            return appInfo;
        }

        private AppInfo parseFromXpk(File file) {
            try {
                AppInfo appInfo = new AppInfo(false);
                XpkInfo xpkInfo = XpkInfo.getXPKManifestDom(new ZipFile(file));
                if (xpkInfo == null) {
                    throw new Exception();
                }

                appInfo.setName(xpkInfo.getAppName());
                appInfo.setId(xpkInfo.getPackageName());
                appInfo.setVersionName(xpkInfo.getVersionName());
                appInfo.setApkFilePath(file.getPath());
                appInfo.setAppSize(file.length());
                appInfo.setFormattedAppSize(Formatter.formatFileSize(context, appInfo.getAppSize()));

                return appInfo;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
