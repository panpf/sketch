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
public class TestFragment extends MyFragment{
//    @InjectView(R.id.image1)
//    MyImageView imageView1;
//    @InjectView(R.id.image2)
//    MyImageView imageView2;

    @InjectView(R.id.image1) private MyImageView imageView1;
    @InjectView(R.id.image2) private MyImageView imageView2;
    @InjectView(R.id.image3) private MyImageView imageView3;
    @InjectView(R.id.image4) private MyImageView imageView4;
    @InjectView(R.id.image5) private MyImageView imageView5;
    @InjectView(R.id.image6) private MyImageView imageView6;
    @InjectView(R.id.image7) private MyImageView imageView7;
    @InjectView(R.id.image8) private MyImageView imageView8;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        imageView1.setOptions(new DisplayOptions().setLoadingImage(R.drawable.image_loading).setImageDisplayer(new TransitionImageDisplayer()));
//        imageView2.setOptions(new DisplayOptions().setImageDisplayer(new TransitionImageDisplayer()));
//
//        imageView1.displayImage("http://h.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513d6999b9df3f6d55fbb2fbd984.jpg");
//        imageView2.displayImage("http://h.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513d6999b9df3f6d55fbb2fbd984.jpg");
//
//        imageView1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("", "");
//            }
//        });
//
//        imageView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("", "");
//            }
//        });

        loadAppList();
    }

    private void loadAppList(){
        new AsyncTask<Integer, Integer, String>(){
            private Context context = getActivity().getBaseContext();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Integer... params) {
                PackageManager packageManager = context.getPackageManager();
                List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
                for(PackageInfo packageInfo : packageInfoList){
                    if(!((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)){
                        return packageInfo.applicationInfo.sourceDir;
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(String apkPath) {
                if(getActivity() == null || apkPath == null){
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
