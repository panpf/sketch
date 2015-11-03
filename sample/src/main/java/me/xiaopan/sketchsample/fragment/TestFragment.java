package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectParentMember;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.DisplayOptions;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectParentMember
@InjectContentView(R.layout.fragment_test)
public class TestFragment extends MyFragment{
    @InjectView(R.id.image1)
    MyImageView imageView1;
    @InjectView(R.id.image2)
    MyImageView imageView2;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView1.setDisplayOptions(new DisplayOptions().setLoadingImage(R.drawable.image_loading).setImageDisplayer(new TransitionImageDisplayer()));
        imageView2.setDisplayOptions(new DisplayOptions().setImageDisplayer(new TransitionImageDisplayer()));

        imageView1.displayImage("http://h.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513d6999b9df3f6d55fbb2fbd984.jpg");
        imageView2.displayImage("http://h.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513d6999b9df3f6d55fbb2fbd984.jpg");

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("", "");
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("", "");
            }
        });
    }
}
