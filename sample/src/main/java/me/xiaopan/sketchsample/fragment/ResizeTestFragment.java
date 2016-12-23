package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_resize)
public class ResizeTestFragment extends MyFragment {
    @InjectView(R.id.image_resizeFragment)
    MyImageView imageView;

    @InjectView(R.id.seekBar_resizeFragment_width)
    SeekBar widthSeekBar;

    @InjectView(R.id.text_resizeFragment_width)
    TextView widthProgressTextView;

    @InjectView(R.id.seekBar_resizeFragment_height)
    SeekBar heightSeekBar;

    @InjectView(R.id.text_resizeFragment_height)
    TextView heightProgressTextView;

    private int widthProgress = 50;
    private int heightProgress = 50;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                widthProgress = widthSeekBar.getProgress();
                apply();
            }
        });
        widthSeekBar.setProgress(widthProgress);

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
                heightProgress = heightSeekBar.getProgress();
                apply();
            }
        });
        heightSeekBar.setProgress(heightProgress);

        apply();
    }

    private void apply() {
        int width = (int) ((widthProgress / 100f) * 1000);
        int height = (int) ((heightProgress / 100f) * 1000);

        imageView.getOptions().setResize(width, height);
        imageView.displayAssetImage("bizhi1.jpg");
    }
}
