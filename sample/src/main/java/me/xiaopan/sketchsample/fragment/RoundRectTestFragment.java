package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.RoundRectImageProcessor;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_gaussian_blur)
public class RoundRectTestFragment extends MyFragment{
    @InjectView(R.id.image_gaussianBlurFragment)
    MyImageView imageView;

    @InjectView(R.id.seekBar_gaussianBlurFragment)
    SeekBar seekBar;

    @InjectView(R.id.text_gaussianBlurFragment)
    TextView progressTextView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView.getOptions().setImageDisplayer(new TransitionImageDisplayer());

        seekBar.setMax(100);
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
                apply();
            }
        });
        seekBar.setProgress(30);
        apply();
    }

    private void apply() {
        imageView.getOptions().setImageProcessor(new RoundRectImageProcessor(seekBar.getProgress()));
        imageView.displayAssetImage("bizhi1.jpg");
    }
}
