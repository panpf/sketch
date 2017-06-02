package me.xiaopan.sketchsample.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.MaskImageProcessor;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.SampleImageView;

@BindContentView(R.layout.fragment_mask)
public class MaskImageProcessorTestFragment extends BaseFragment {
    @BindView(R.id.image_maskFragment)
    SampleImageView imageView;

    @BindView(R.id.seekBar_maskFragment)
    SeekBar seekBar;

    @BindView(R.id.text_maskFragment)
    TextView progressTextView;

    private int progress = 15;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 缩小图片，处理速度更快，更少的内存消耗
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageView.getOptions().setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2);

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
        int alpha = (int) (((float) progress / 100) * 255);
        int maskColor = Color.argb(alpha, 0, 0, 0);
        imageView.getOptions().setImageProcessor(new MaskImageProcessor(maskColor));
        imageView.displayImage(AssetImage.MASK);
    }
}
