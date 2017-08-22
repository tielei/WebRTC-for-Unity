package com.ibicha.webrtc.test;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;

/**
 * Created by charleszhang on 22/08/2017.
 */

public class Utils {
    /**
     * Save a bitmap contained in a Buffer to the specified path.
     * @param buf
     * @param filename
     * @param width
     * @param height
     */
    static void saveRgb2Bitmap(Buffer buf, String filename, int width, int height) {
        Log.d("OpenGLUtils", "Creating " + filename);
        BufferedOutputStream bos = null;
        try {

            File file = new File(filename);
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }

            bos = new BufferedOutputStream(new FileOutputStream(filename));
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(buf);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
            bmp.recycle();
        } catch (IOException e) {
            Log.e("OpenGLUtils", "Bitmap save failed.", e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
