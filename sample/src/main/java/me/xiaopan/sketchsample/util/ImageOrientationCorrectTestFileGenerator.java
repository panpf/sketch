package me.xiaopan.sketchsample.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.ExifInterface;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.AssetImage;

public class ImageOrientationCorrectTestFileGenerator {

    private static final ImageOrientationCorrectTestFileGenerator instance = new ImageOrientationCorrectTestFileGenerator();

    private static final int VERSION = 5;
    private static final String[] configs = new String[]{
            String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_ROTATE_90", VERSION, -90, 1, ExifInterface.ORIENTATION_ROTATE_90),
            String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_ROTATE_180", VERSION, -180, 1, ExifInterface.ORIENTATION_ROTATE_180),
            String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_ROTATE_270", VERSION, -270, 1, ExifInterface.ORIENTATION_ROTATE_270),
            String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_FLIP_HORIZONTAL", VERSION, 0, -1, ExifInterface.ORIENTATION_FLIP_HORIZONTAL),
            String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_TRANSPOSE", VERSION, -90, -1, ExifInterface.ORIENTATION_TRANSPOSE),
            String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_FLIP_VERTICAL", VERSION, -180, -1, ExifInterface.ORIENTATION_FLIP_VERTICAL),
            String.format("%s_%d.jpg,%d,%d,%d", "TEST_FILE_NAME_TRANSVERSE", VERSION, -270, -1, ExifInterface.ORIENTATION_TRANSVERSE),
    };
    private static final String TAG_VERSION = "TAG_VERSION";

    private Config[] files = null;
    private AssetManager assetManager;

    public static ImageOrientationCorrectTestFileGenerator getInstance(Context context) {
        instance.init(context);
        return instance;
    }

    private void init(Context context) {
        if (files != null) {
            return;
        }

        if (assetManager == null) {
            assetManager = context.getAssets();
        }

        boolean changed = isChanged(context);
        if (changed) {
            updateVersion(context);
        }

        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir == null) {
            externalFilesDir = context.getFilesDir();
        }
        String dirPath = externalFilesDir.getPath() + File.separator + "TEST_ORIENTATION";

        files = new Config[configs.length];
        for (int w = 0; w < configs.length; w++) {
            String[] elements = configs[w].split(",");
            String filePath = String.format("%s%s%s", dirPath, File.separator, elements[0]);
            files[w] = new Config(filePath, Integer.parseInt(elements[1]), Integer.parseInt(elements[2]), Integer.parseInt(elements[3]));
        }

        if (changed) {
            File dir = new File(dirPath);
            if (dir.exists()) {
                SketchUtils.cleanDir(dir);
            }
        }
    }

    private boolean isChanged(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(TAG_VERSION, 0) != VERSION;
    }

    private void updateVersion(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(TAG_VERSION, VERSION).apply();
    }

    public String[] getFilePaths() {
        String[] filePaths = new String[files.length];
        for (int w = 0; w < configs.length; w++) {
            filePaths[w] = files[w].filePath;
        }
        return filePaths;
    }

    public void onAppStart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                InputStream inputStream;
                try {
                    inputStream = assetManager.open(UriScheme.ASSET.cropContent(AssetImage.MEI_NV));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                Bitmap sourceBitmap = BitmapFactory.decodeStream(inputStream, null, options);
                SketchUtils.close(inputStream);

                for (Config config : files) {
                    File file = new File(config.filePath);
                    generatorTestFile(file, sourceBitmap, config.degrees, config.xScale, config.orientation);
                }

                sourceBitmap.recycle();
            }
        }).start();
    }

    private void generatorTestFile(File file, Bitmap sourceBitmap, int rotateDegrees, int xScale, int orientation) {
        if (file.exists()) {
            return;
        }

        Bitmap newBitmap = transformBitmap(sourceBitmap, rotateDegrees, xScale);
        if (newBitmap == null || newBitmap.isRecycled()) {
            return;
        }

        file.getParentFile().mkdirs();

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            newBitmap.recycle();
            return;
        }
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        SketchUtils.close(outputStream);
        newBitmap.recycle();

        ExifInterface exifInterface;
        try {
            exifInterface = new ExifInterface(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
            return;
        }
        exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(orientation));
        try {
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
        }
    }

    private Bitmap transformBitmap(Bitmap sourceBitmap, int degrees, int xScale) {
        Matrix matrix = new Matrix();
        matrix.setScale(xScale, 1);
        matrix.postRotate(degrees);

        // 根据旋转角度计算新的图片的尺寸
        RectF newRect = new RectF(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
        matrix.mapRect(newRect);
        int newWidth = (int) newRect.width();
        int newHeight = (int) newRect.height();

        // 角度不能整除90°时新图片会是斜的，因此要支持透明度，这样倾斜导致露出的部分就不会是黑的
        Bitmap.Config config = sourceBitmap.getConfig() != null ? sourceBitmap.getConfig() : null;
        if (degrees % 90 != 0 && config != Bitmap.Config.ARGB_8888) {
            config = Bitmap.Config.ARGB_8888;
        }

        Bitmap result = Bitmap.createBitmap(newWidth, newHeight, config);

        matrix.postTranslate(-newRect.left, -newRect.top);

        final Canvas canvas = new Canvas(result);
        final Paint paint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(sourceBitmap, matrix, paint);

        return result;
    }

    private static class Config {
        String filePath;
        int degrees;
        int xScale;
        int orientation;

        public Config(String filePath, int degrees, int xScale, int orientation) {
            this.filePath = filePath;
            this.degrees = degrees;
            this.xScale = xScale;
            this.orientation = orientation;
        }
    }
}
