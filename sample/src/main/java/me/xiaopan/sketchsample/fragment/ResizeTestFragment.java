package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.view.View;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.ResizeImageProcessor;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_reflection)
public class ResizeTestFragment extends MyFragment{
    @InjectView(R.id.image_reflectionFragment)
    MyImageView imageView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView.getOptions().setImageProcessor(new ResizeImageProcessor());
        imageView.getOptions().setResize(350, 500);
        imageView.getOptions().setImageDisplayer(new TransitionImageDisplayer());
        imageView.displayAssetImage("bizhi1.jpg");
    }
}
