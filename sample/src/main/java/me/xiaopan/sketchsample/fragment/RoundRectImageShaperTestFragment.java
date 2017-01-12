package me.xiaopan.sketchsample.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.shaper.RoundRectImageShaper;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_round_rect_image_shaper)
public class RoundRectImageShaperTestFragment extends MyFragment {
    @InjectView(R.id.image_roundRectImageShaperFragment)
    MyImageView imageView;

    @InjectView(R.id.seekBar_roundRectImageShaperFragment_radius)
    SeekBar radiusSeekBar;

    @InjectView(R.id.text_roundRectImageShaperFragment_radius)
    TextView radiusProgressTextView;

    @InjectView(R.id.seekBar_roundRectImageShaperFragment_stroke)
    SeekBar strokeSeekBar;

    @InjectView(R.id.text_roundRectImageShaperFragment_stroke)
    TextView strokeProgressTextView;

    private int radiusProgress = 20;
    private int strokeProgress = 5;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView.getOptions().setImageDisplayer(new TransitionImageDisplayer());

        radiusSeekBar.setMax(100);
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusProgressTextView.setText(String.format("%d/%d", progress, 100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                radiusProgress = radiusSeekBar.getProgress();
                apply();
            }
        });
        radiusSeekBar.setProgress(radiusProgress);

        strokeSeekBar.setMax(100);
        strokeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                strokeProgressTextView.setText(String.format("%d/%d", progress, 100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                strokeProgress = strokeSeekBar.getProgress();
                apply();
            }
        });
        strokeSeekBar.setProgress(strokeProgress);

        apply();
    }

    private void apply() {
        RoundRectImageShaper imageShaper = new RoundRectImageShaper(radiusProgress).setStroke(Color.WHITE, strokeProgress);

        imageView.getOptions().setImageShaper(imageShaper);
        imageView.displayImage(AssetImage.SAMPLE_JPG);
    }
}