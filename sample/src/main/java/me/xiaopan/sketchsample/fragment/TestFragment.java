package me.xiaopan.sketchsample.fragment;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectParentMember;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectParentMember
@InjectContentView(R.layout.fragment_test)
public class TestFragment extends MyFragment {

    @InjectView(R.id.image1)
    private MyImageView imageView1;
    @InjectView(R.id.image2)
    private MyImageView imageView2;
    @InjectView(R.id.image3)
    private MyImageView imageView3;
    @InjectView(R.id.image4)
    private MyImageView imageView4;
    @InjectView(R.id.image5)
    private MyImageView imageView5;
    @InjectView(R.id.image6)
    private MyImageView imageView6;
    @InjectView(R.id.image7)
    private MyImageView imageView7;
    @InjectView(R.id.image8)
    private MyImageView imageView8;

    @InjectView(R.id.image9)
    private MyImageView imageView9;
    @InjectView(R.id.image10)
    private MyImageView imageView10;
    @InjectView(R.id.image11)
    private MyImageView imageView11;
    @InjectView(R.id.image12)
    private MyImageView imageView12;
    @InjectView(R.id.image13)
    private MyImageView imageView13;
    @InjectView(R.id.image14)
    private MyImageView imageView14;
    @InjectView(R.id.image15)
    private MyImageView imageView15;
    @InjectView(R.id.image16)
    private MyImageView imageView16;

    @InjectView(R.id.image31)
    private MyImageView imageView31;
    @InjectView(R.id.image32)
    private MyImageView imageView32;
    @InjectView(R.id.image33)
    private MyImageView imageView33;
    @InjectView(R.id.image34)
    private MyImageView imageView34;
    @InjectView(R.id.image35)
    private MyImageView imageView35;
    @InjectView(R.id.image36)
    private MyImageView imageView36;
    @InjectView(R.id.image37)
    private MyImageView imageView37;
    @InjectView(R.id.image38)
    private MyImageView imageView38;

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
