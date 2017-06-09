package me.xiaopan.sketchsample.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {
    public static String readAssetFile(Context context, String assetFileName) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(assetFileName)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder builder = new StringBuilder();

        char[] buffer = new char[4 * 1024];
        int readLength;
        while (true) {
            try {
                readLength = reader.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return null;
            }
            if (readLength >= 0) {
                builder.append(buffer, 0, readLength);
            } else {
                break;
            }
        }
        try {
            reader.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return builder.toString();
    }

    public static String subSuffix(String fileNameOrPath) {
        if (fileNameOrPath == null) {
            return null;
        }

        int dotIndex = fileNameOrPath.lastIndexOf('.');
        if (dotIndex == -1) {
            return null;
        }

        return fileNameOrPath.substring(dotIndex);
    }
}
