package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.view.View;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.LargeImageView;

@InjectContentView(R.layout.fragment_large_image)
public class LargeImageFragment extends MyFragment{

    @InjectView(R.id.largeImage) private LargeImageView largeImageView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        largeImageView.displayAssetImage("large_image.jpg");
    }
}
