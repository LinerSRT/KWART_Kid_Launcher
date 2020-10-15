package com.sgtc.launcher.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;


import com.sgtc.launcher.LauncherApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Image {
    public static Bitmap drawableToBitmap(Drawable drawable, float scale) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null)
                return bitmapDrawable.getBitmap();
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(Math.round(drawable.getIntrinsicWidth()*scale), Math.round(drawable.getIntrinsicHeight()*scale), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap loadBitmap(String path, boolean isExternal) throws IOException {
        return streamToBitmap(loadStream(path, isExternal));
    }

    public static Bitmap streamToBitmap(InputStream inputStream){
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        DisplayMetrics displayMetrics = LauncherApplication.getContext().getResources().getDisplayMetrics();
        bitmap.setDensity(displayMetrics.densityDpi);
        return bitmap;
    }

    public static InputStream loadStream(String path, boolean isExternal) throws IOException{
        return (isExternal)?new FileInputStream(new File(path)): LauncherApplication.getContext().getAssets().open(path);
    }

    public static byte[] bitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap bytesToBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
