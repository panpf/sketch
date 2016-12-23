package me.xiaopan.sketchsample.fragment;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.drawable.ShapeBitmapDrawable;
import me.xiaopan.sketch.shaper.CircleImageShaper;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_circle_image_shaper)
public class CircleImageShaperTestFragment extends MyFragment {
    @InjectView(R.id.image_circleImageShaperFragment)
    MyImageView imageView;

    @InjectView(R.id.seekBar_circleImageShaperFragment_stroke)
    SeekBar strokeSeekBar;

    @InjectView(R.id.text_circleImageShaperFragment_stroke)
    TextView strokeProgressTextView;

    private int strokeProgress = 5;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        imageView.getOptions().setImageDisplayer(new TransitionImageDisplayer());

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
        CircleImageShaper imageShaper = new CircleImageShaper().setStroke(Color.WHITE, strokeProgress);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.image_test);
        ShapeBitmapDrawable shapeBitmapDrawable = new ShapeBitmapDrawable(bitmapDrawable, imageShaper);
        imageView.setImageDrawable(shapeBitmapDrawable);
    }
}