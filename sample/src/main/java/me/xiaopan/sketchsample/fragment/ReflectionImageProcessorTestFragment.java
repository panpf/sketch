package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import butterknife.BindView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.ReflectionImageProcessor;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.SampleImageView;

@BindContentView(R.layout.fragment_reflection)
public class ReflectionImageProcessorTestFragment extends BaseFragment {
    @BindView(R.id.image_reflectionFragment)
    SampleImageView imageView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 缩小图片，处理速度更快，更少的内存消耗
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageView.getOptions().setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2);

        imageView.getOptions().setImageProcessor(new ReflectionImageProcessor());
        imageView.getOptions().setImageDisplayer(new TransitionImageDisplayer());
        imageView.displayImage(AssetImage.MEI_NV);
    }
}
