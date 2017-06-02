package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.request.Resize;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.SampleImageView;

@BindContentView(R.layout.fragment_resize)
public class ResizeImageProcessorTestFragment extends BaseFragment {
    @BindView(R.id.image_resizeFragment)
    SampleImageView imageView;

    @BindView(R.id.seekBar_resizeFragment_width)
    SeekBar widthSeekBar;

    @BindView(R.id.text_resizeFragment_width)
    TextView widthProgressTextView;

    @BindView(R.id.seekBar_resizeFragment_height)
    SeekBar heightSeekBar;

    @BindView(R.id.text_resizeFragment_height)
    TextView heightProgressTextView;

    @BindView(R.id.button_resizeFragment_fixStart)
    View fixStartButton;

    @BindView(R.id.button_resizeFragment_fixCenter)
    View fixCenterButton;

    @BindView(R.id.button_resizeFragment_fixEnd)
    View fixEndButton;

    @BindView(R.id.button_resizeFragment_fixXY)
    View fixXYButton;

    @BindView(R.id.button_resizeFragment_center)
    View centerButton;

    @BindView(R.id.button_resizeFragment_centerCrop)
    View centerCropButton;

    @BindView(R.id.button_resizeFragment_centerInside)
    View centerInsideButton;

    @BindView(R.id.button_resizeFragment_matrix)
    View matrixButton;

    private int widthProgress = 50;
    private int heightProgress = 50;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_CENTER;
    private View currentCheckedButton;

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
                apply(currentCheckedButton);
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
                apply(currentCheckedButton);
            }
        });
        heightSeekBar.setProgress(heightProgress);

        fixStartButton.setTag(ImageView.ScaleType.FIT_START);
        fixCenterButton.setTag(ImageView.ScaleType.FIT_CENTER);
        fixEndButton.setTag(ImageView.ScaleType.FIT_END);
        fixXYButton.setTag(ImageView.ScaleType.FIT_XY);
        centerButton.setTag(ImageView.ScaleType.CENTER);
        centerCropButton.setTag(ImageView.ScaleType.CENTER_CROP);
        centerInsideButton.setTag(ImageView.ScaleType.CENTER_INSIDE);
        matrixButton.setTag(ImageView.ScaleType.MATRIX);

        View.OnClickListener buttonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleType = (ImageView.ScaleType) v.getTag();
                apply(v);
            }
        };
        fixStartButton.setOnClickListener(buttonOnClickListener);
        fixCenterButton.setOnClickListener(buttonOnClickListener);
        fixEndButton.setOnClickListener(buttonOnClickListener);
        fixXYButton.setOnClickListener(buttonOnClickListener);
        centerButton.setOnClickListener(buttonOnClickListener);
        centerCropButton.setOnClickListener(buttonOnClickListener);
        centerInsideButton.setOnClickListener(buttonOnClickListener);
        matrixButton.setOnClickListener(buttonOnClickListener);

        if (currentCheckedButton == null) {
            currentCheckedButton = fixCenterButton;
        }
        apply(currentCheckedButton);
    }

    private void apply(View button) {
        int width = (int) ((widthProgress / 100f) * 1000);
        int height = (int) ((heightProgress / 100f) * 1000);

        imageView.getOptions().setResize(new Resize(width, height, scaleType));
        imageView.displayImage(AssetImage.MEI_NV);

        if (currentCheckedButton != null) {
            currentCheckedButton.setEnabled(true);
        }
        button.setEnabled(false);
        currentCheckedButton = button;
    }
}
