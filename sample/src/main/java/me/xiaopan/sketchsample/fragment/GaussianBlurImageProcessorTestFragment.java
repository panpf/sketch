package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.SampleImageView;

@BindContentView(R.layout.fragment_gaussian_blur)
public class GaussianBlurImageProcessorTestFragment extends BaseFragment {
    @BindView(R.id.image_gaussianBlurFragment)
    SampleImageView imageView;

    @BindView(R.id.seekBar_gaussianBlurFragment)
    SeekBar seekBar;

    @BindView(R.id.text_gaussianBlurFragment)
    TextView progressTextView;

    private int progress = 15;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 通过maxSize限制缩小读到内存的图片的尺寸，尺寸越小高斯模糊越快
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageView.getOptions().setMaxSize(metrics.widthPixels / 4, metrics.heightPixels / 4);

        imageView.getOptions().setImageDisplayer(new TransitionImageDisplayer());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressTextView.setText(String.format("%d/%d", seekBar.getProgress(), seekBar.getMax()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress = seekBar.getProgress();
                apply();
            }
        });

        seekBar.setMax(100);
        seekBar.setProgress(progress);

        apply();
    }

    private void apply() {
        imageView.getOptions().setImageProcessor(GaussianBlurImageProcessor.makeRadius(progress));
        imageView.displayImage(AssetImage.MEI_NV);
    }
}
