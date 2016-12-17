package me.xiaopan.sketchsample.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;

@InjectContentView(R.layout.fragment_bitmap_pool_test)
public class BitmapPoolTestFragment extends MyFragment {
    private static final String[] images = new String[]{
            "masichun1.jpg",
            "masichun2.jpg",
            "masichun3.jpg",
            "bizhi1.jpg",
            "bizhi2.jpg",
            "bizhi3.jpg",
    };

    @InjectView(R.id.image_bitmapPoolTestFragment)
    ImageView imageView;

    @InjectView(R.id.text_bitmapPoolTestFragment)
    TextView textView;

    int index = 0;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final BitmapPool bitmapPool = Sketch.with(getActivity()).getConfiguration().getBitmapPool();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Integer, Integer, Bitmap>() {
                    private StringBuilder builder = new StringBuilder();

                    @Override
                    protected Bitmap doInBackground(Integer... params) {
                        String fileName = images[params[0]];

                        BitmapFactory.Options options = new BitmapFactory.Options();

                        options.inJustDecodeBounds = true;
                        readImage(fileName, options);

                        builder.append("imageName=").append(fileName).append("\n");
                        builder.append("outWidth=").append(options.outWidth).append("\n");
                        builder.append("outHeight=").append(options.outHeight).append("\n");
                        builder.append("inPreferredConfig=").append(options.inPreferredConfig).append("\n");

                        if (SketchUtils.sdkSupportInBitmap()) {
                            int sizeInBytes = SketchUtils.getBitmapByteSize(options.outWidth, options.outHeight, options.inPreferredConfig);
                            builder.append("sizeInBytes=").append(sizeInBytes).append("\n");
                            if (SketchUtils.setInBitmapFromPool(options, bitmapPool)) {
                                builder.append("inBitmap=")
                                        .append(Integer.toHexString(options.inBitmap.hashCode()))
                                        .append(", ")
                                        .append(SketchUtils.getBitmapByteSize(options.inBitmap))
                                        .append("\n");
                            } else {
                                builder.append("inBitmap=").append("no").append("\n");
                            }
                        }

                        options.inJustDecodeBounds = false;
                        Bitmap newBitmap = readImage(fileName, options);

                        builder.append("newBitmap=")
                                .append(Integer.toHexString(newBitmap.hashCode()))
                                .append(", ")
                                .append(SketchUtils.getBitmapByteSize(newBitmap))
                                .append("\n");

                        return newBitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);

                        Bitmap oldBitmap = null;
                        BitmapDrawable oldDrawable = (BitmapDrawable) imageView.getDrawable();
                        if (oldDrawable != null) {
                            oldBitmap = oldDrawable.getBitmap();
                        }
                        imageView.setImageBitmap(bitmap);
                        textView.setText(builder.toString());

                        if (!SketchUtils.freeBitmapToPool(oldBitmap, bitmapPool)) {
                            Log.w("BitmapPoolTest", "recycle");
                        }
                    }
                }.execute(index++ % images.length);
            }
        });

        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.performClick();
            }
        });
    }

    private Bitmap readImage(String fileName, BitmapFactory.Options options) {
        InputStream inputStream = null;
        try {
            inputStream = getActivity().getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        SketchUtils.close(inputStream);
        return bitmap;
    }
}
