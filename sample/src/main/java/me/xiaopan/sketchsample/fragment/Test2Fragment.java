package me.xiaopan.sketchsample.fragment;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.drawable.ShapeBitmapDrawable;
import me.xiaopan.sketch.shaper.CircleImageShaper;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;

@InjectContentView(R.layout.fragment_test2)
public class Test2Fragment extends MyFragment{
    @InjectView(R.id.image_test)
    private ImageView imageView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        RoundRectShapeImage imageShaper = new RoundRectShapeImage(20);
//        RoundRectImageShaper imageShaper = new RoundRectImageShaper(20).setStrokeColor(Color.WHITE).setStrokeWidth(5);

//        CircleImageShaper imageShaper = new CircleImageShaper();
        CircleImageShaper imageShaper = new CircleImageShaper().setStroke(Color.WHITE, 5);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.image_test);
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher);

//        ShapeBitmapDrawable shapeBitmapDrawable = new ShapeBitmapDrawable(bitmapDrawable, new FixedSize(500, 300), shapeImage);
//        ShapeBitmapDrawable shapeBitmapDrawable = new ShapeBitmapDrawable(bitmapDrawable, new FixedSize(300, 300));
        ShapeBitmapDrawable shapeBitmapDrawable = new ShapeBitmapDrawable(bitmapDrawable, imageShaper);

        imageView.setImageDrawable(shapeBitmapDrawable);
    }
}
