package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.CircleImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.SampleImageView;

@BindContentView(R.layout.fragment_reflection)
public class CircleImageProcessorTestFragment extends BaseFragment {
    @BindView(R.id.image_reflectionFragment)
    SampleImageView imageView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 缩小图片，处理速度更快，更少的内存消耗
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageView.getOptions().setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
        layoutParams.leftMargin = layoutParams.topMargin = layoutParams.rightMargin = layoutParams.bottomMargin = SketchUtils.dp2px(getActivity(), 16);
        imageView.setLayoutParams(layoutParams);

        imageView.getOptions().setImageProcessor(CircleImageProcessor.getInstance());
        imageView.getOptions().setImageDisplayer(new TransitionImageDisplayer());
        imageView.displayImage(AssetImage.MEI_NV);
    }
}
