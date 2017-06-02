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
import me.xiaopan.sketch.process.RotateImageProcessor;
import me.xiaopan.sketch.process.RoundRectImageProcessor;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.SampleImageView;

@BindContentView(R.layout.fragment_wrapped)
public class WrappedImageProcessorTestFragment extends BaseFragment {
    @BindView(R.id.image_wrappedFragment)
    SampleImageView imageView;

    @BindView(R.id.seekBar_wrappedFragment_width)
    SeekBar widthSeekBar;

    @BindView(R.id.text_wrappedFragment_width)
    TextView widthProgressTextView;

    @BindView(R.id.seekBar_wrappedFragment_height)
    SeekBar heightSeekBar;

    @BindView(R.id.text_wrappedFragment_height)
    TextView heightProgressTextView;

    @BindView(R.id.button_wrappedFragment)
    View rotateButton;

    private int roundRectRadiusProgress = 30;
    private int maskAlphaProgress = 45;
    private int rotateProgress = 45;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 缩小图片，处理速度更快，更少的内存消耗
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageView.getOptions().setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2);

        imageView.getOptions().setImageDisplayer(new TransitionImageDisplayer());

        widthSeekBar.setMax(100);
        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 20) {
                    widthSeekBar.setProgress(20);
                    return;
                }

                int width = (int) ((widthSeekBar.getProgress() / 100f) * 1000);
                widthProgressTextView.setText(String.format("%d/%d", width, 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                roundRectRadiusProgress = widthSeekBar.getProgress();
                apply();
            }
        });
        widthSeekBar.setProgress(roundRectRadiusProgress);

        heightSeekBar.setMax(100);
        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 20) {
                    heightSeekBar.setProgress(20);
                    return;
                }
                int height = (int) ((heightSeekBar.getProgress() / 100f) * 1000);
                heightProgressTextView.setText(String.format("%d/%d", height, 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                maskAlphaProgress = heightSeekBar.getProgress();
                apply();
            }
        });
        heightSeekBar.setProgress(maskAlphaProgress);

        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateProgress += 45;
                apply();
            }
        });

        apply();
    }

    private void apply() {
        RoundRectImageProcessor roundRectImageProcessor = new RoundRectImageProcessor(roundRectRadiusProgress);
        RotateImageProcessor rotateImageProcessor = new RotateImageProcessor(rotateProgress, roundRectImageProcessor);

        int alpha = (int) (((float) maskAlphaProgress / 100) * 255);
        int maskColor = Color.argb(alpha, 0, 0, 0);

        imageView.getOptions().setImageProcessor(new MaskImageProcessor(maskColor, rotateImageProcessor));
        imageView.displayImage(AssetImage.MEI_NV);
    }
}
