package me.xiaopan.sketchsample.fragment;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchMonitor;
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

    @InjectView(R.id.button_bitmapPoolTestFragment_loop)
    Button loopButton;

    @InjectView(R.id.button_bitmapPoolTestFragment_sizeSame)
    Button sizeSameButton;

    @InjectView(R.id.button_bitmapPoolTestFragment_largeSize)
    Button largeSizeButton;

    @InjectView(R.id.button_bitmapPoolTestFragment_sizeNoSame)
    Button sizeNoSameButton;

    @InjectView(R.id.button_bitmapPoolTestFragment_otherFormat)
    Button otherFormatButton;

    int index = -1;

    Configuration configuration;
    AssetManager assetManager;

    private static Bitmap decodeImage(AssetManager manager, String fileName, BitmapFactory.Options options) {
        InputStream inputStream;
        if (fileName.startsWith("/")) {
            try {
                inputStream = new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                inputStream = manager.open(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        } finally {
            SketchUtils.close(inputStream);
        }

        return bitmap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configuration = Sketch.with(getActivity()).getConfiguration();
        assetManager = getActivity().getAssets();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                loop();
            }
        });

        sizeSameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testSizeSame();
            }
        });

        largeSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLargeSize();
            }
        });

        sizeNoSameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testSizeNoSame();
            }
        });

        otherFormatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testOtherFormat();
            }
        });

        loopButton.post(new Runnable() {
            @Override
            public void run() {
                loopButton.performClick();
            }
        });
    }

    private void loop() {
        new TestTask() {
            @Override
            protected void configOptions(BitmapFactory.Options options) {
                if (SketchUtils.sdkSupportInBitmap()) {
                    SketchUtils.setInBitmapFromPool(options, configuration.getBitmapPool());
                }
            }
        }.execute(images[index % images.length]);
    }

    private void testSizeSame() {
        new TestTask() {
            @Override
            protected void configOptions(BitmapFactory.Options options) {
                if (SketchUtils.sdkSupportInBitmap()) {
                    options.inBitmap = Bitmap.createBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
                    options.inMutable = true;
                }
                super.configOptions(options);
            }
        }.execute(images[index % images.length]);
    }

    private void testLargeSize() {
        new TestTask() {
            @Override
            protected void configOptions(BitmapFactory.Options options) {
                if (SketchUtils.sdkSupportInBitmap()) {
                    options.inBitmap = Bitmap.createBitmap(options.outWidth + 1, options.outHeight, options.inPreferredConfig);
                    options.inMutable = true;
                }
                super.configOptions(options);
            }
        }.execute(images[index % images.length]);
    }

    private void testSizeNoSame() {
        new TestTask() {
            @Override
            protected void configOptions(BitmapFactory.Options options) {
                if (SketchUtils.sdkSupportInBitmap()) {
                    options.inBitmap = Bitmap.createBitmap(options.outHeight, options.outWidth, options.inPreferredConfig);
                    options.inMutable = true;
                }
                super.configOptions(options);
            }
        }.execute(images[index % images.length]);
    }

    private void testOtherFormat() {

    }

    private class TestTask extends AsyncTask<String, Integer, Bitmap> {
        protected StringBuilder builder = new StringBuilder();

        @Override
        protected Bitmap doInBackground(String... params) {
            String fileName = params[0];

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;
            decodeImage(assetManager, fileName, options);

            if (options.outWidth <= 1 || options.outHeight <= 1) {
                return null;
            }

            options.inSampleSize = 1;   // 这很重要4.4以下必须得是1
            configOptions(options);

            builder.append("fileName: ").append(fileName);
            builder.append("\n").append("imageSize: ").append(options.outWidth).append("x").append(options.outHeight);
            builder.append("\n").append("inPreferredConfig: ").append(options.inPreferredConfig);
            builder.append("\n").append("inSampleSize: ").append(options.inSampleSize);

            int sizeInBytes = SketchUtils.getBitmapByteSize(options.outWidth, options.outHeight, options.inPreferredConfig);
            builder.append("\n").append("sizeInBytes: ").append(sizeInBytes);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (options.inBitmap != null) {
                    builder.append("\n")
                            .append("inBitmap: ")
                            .append(Integer.toHexString(options.inBitmap.hashCode()))
                            .append(", ").append(options.inBitmap.getWidth()).append("x").append(options.inBitmap.getHeight())
                            .append(", ").append(options.inBitmap.isMutable())
                            .append(", ").append(SketchUtils.getBitmapByteSize(options.inBitmap));
                } else {
                    builder.append("\n").append("inBitmap: ").append("null");
                }
            }

            Bitmap newBitmap = null;
            try {
                options.inJustDecodeBounds = false;
                newBitmap = decodeImage(assetManager, fileName, options);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();

                SketchMonitor monitor = Sketch.with(getActivity()).getConfiguration().getMonitor();
                SketchUtils.inBitmapThrow(e, options, monitor, configuration.getBitmapPool(), fileName, 0, 0);
            }

            if (newBitmap != null) {
                builder.append("\n").append("newBitmap: ")
                        .append(Integer.toHexString(newBitmap.hashCode()))
                        .append(", ").append(newBitmap.getWidth()).append("x").append(newBitmap.getHeight())
                        .append(", ").append(newBitmap.isMutable())
                        .append(", ").append(SketchUtils.getBitmapByteSize(newBitmap));
            } else {
                builder.append("\n").append("newBitmap: ").append("null");
            }

            return newBitmap;
        }

        protected void configOptions(BitmapFactory.Options options) {

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

            if (!SketchUtils.freeBitmapToPool(oldBitmap, configuration.getBitmapPool())) {
                Log.w("BitmapPoolTest", "recycle");
            }
        }
    }
}
