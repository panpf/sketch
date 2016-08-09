package me.xiaopan.sketchsample.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class ViewUtils {
    public static Bitmap toBitmap(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
