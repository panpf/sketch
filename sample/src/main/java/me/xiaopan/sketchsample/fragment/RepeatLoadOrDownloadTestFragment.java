package me.xiaopan.sketchsample.fragment;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.SampleImageView;

@BindContentView(R.layout.fragment_repeat_load_or_download_test)
public class RepeatLoadOrDownloadTestFragment extends BaseFragment {

    @BindView(R.id.image_repeatLoadOrDownloadTest_1)
    SampleImageView imageView1;
    @BindView(R.id.image_repeatLoadOrDownloadTest_2)
    SampleImageView imageView2;
    @BindView(R.id.image_repeatLoadOrDownloadTest_3)
    SampleImageView imageView3;
    @BindView(R.id.image_repeatLoadOrDownloadTest_4)
    SampleImageView imageView4;
    @BindView(R.id.image_repeatLoadOrDownloadTest_5)
    SampleImageView imageView5;
    @BindView(R.id.image_repeatLoadOrDownloadTest_6)
    SampleImageView imageView6;
    @BindView(R.id.image_repeatLoadOrDownloadTest_7)
    SampleImageView imageView7;
    @BindView(R.id.image_repeatLoadOrDownloadTest_8)
    SampleImageView imageView8;

    @BindView(R.id.image_repeatLoadOrDownloadTest_9)
    SampleImageView imageView9;
    @BindView(R.id.image_repeatLoadOrDownloadTest_10)
    SampleImageView imageView10;
    @BindView(R.id.image_repeatLoadOrDownloadTest_11)
    SampleImageView imageView11;
    @BindView(R.id.image_repeatLoadOrDownloadTest_12)
    SampleImageView imageView12;
    @BindView(R.id.image_repeatLoadOrDownloadTest_13)
    SampleImageView imageView13;
    @BindView(R.id.image_repeatLoadOrDownloadTest_14)
    SampleImageView imageView14;
    @BindView(R.id.image_repeatLoadOrDownloadTest_15)
    SampleImageView imageView15;
    @BindView(R.id.image_repeatLoadOrDownloadTest_16)
    SampleImageView imageView16;

    @BindView(R.id.image_repeatLoadOrDownloadTest_31)
    SampleImageView imageView31;
    @BindView(R.id.image_repeatLoadOrDownloadTest_32)
    SampleImageView imageView32;
    @BindView(R.id.image_repeatLoadOrDownloadTest_33)
    SampleImageView imageView33;
    @BindView(R.id.image_repeatLoadOrDownloadTest_34)
    SampleImageView imageView34;
    @BindView(R.id.image_repeatLoadOrDownloadTest_35)
    SampleImageView imageView35;
    @BindView(R.id.image_repeatLoadOrDownloadTest_36)
    SampleImageView imageView36;
    @BindView(R.id.image_repeatLoadOrDownloadTest_37)
    SampleImageView imageView37;
    @BindView(R.id.image_repeatLoadOrDownloadTest_38)
    SampleImageView imageView38;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadAppList();

        imageView9.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg");
        imageView10.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg");
        imageView11.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg");
        imageView12.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg");
        imageView13.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg");
        imageView14.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg");
        imageView15.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg");
        imageView16.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg");

        imageView31.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg");
        imageView32.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg");
        imageView33.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg");
        imageView34.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg");
        imageView35.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg");
        imageView36.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg");
        imageView37.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg");
        imageView38.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg");
    }

    private void loadAppList() {
        new AsyncTask<Integer, Integer, String>() {
            private Context context = getActivity().getBaseContext();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Integer... params) {
                PackageManager packageManager = context.getPackageManager();
                List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
                for (PackageInfo packageInfo : packageInfoList) {
                    if (!((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)) {
                        return packageInfo.applicationInfo.sourceDir;
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(String apkPath) {
                if (getActivity() == null || apkPath == null) {
                    return;
                }

                imageView1.displayImage(apkPath);
                imageView2.displayImage(apkPath);
                imageView3.displayImage(apkPath);
                imageView4.displayImage(apkPath);
                imageView5.displayImage(apkPath);
                imageView6.displayImage(apkPath);
                imageView7.displayImage(apkPath);
                imageView8.displayImage(apkPath);
            }
        }.execute(0);
    }
}
