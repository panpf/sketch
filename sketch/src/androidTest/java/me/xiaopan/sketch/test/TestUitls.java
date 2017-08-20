package me.xiaopan.sketch.test;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestUitls {

    public static void copyFile(InputStream inputStream, File dest) throws IOException {
        File destParent = dest.getParentFile();
        if (!destParent.exists() && !destParent.mkdirs()) {
            String errorMessage = String.format("error happened creating parent dir for file %s", dest);
            Log.e("ProviderTestRule", errorMessage);
            throw new IOException(errorMessage);
        } else {
            FileOutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[8 * 1024];
            int readLength;
            try {
                while (true) {
                    readLength = inputStream.read(buffer);
                    if (readLength <= 0) {
                        break;
                    }
                    out.write(buffer, 0, readLength);
                }
            } finally {
                out.close();
            }
        }
    }
}
