package com.sgtc.launcher.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Bitmaps {

    public static Bitmap toBitmap(Drawable drawable, float scale) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null)
                return bitmapDrawable.getBitmap();
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(Math.round(drawable.getIntrinsicWidth() * scale), Math.round(drawable.getIntrinsicHeight() * scale), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap toBitmap(InputStream inputStream) {
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        bitmap.setDensity(Resources.getSystem().getDisplayMetrics().densityDpi);
        return bitmap;
    }


    public static Bitmap toBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static byte[] toBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap fromView(View view) {
//        view.clearFocus();
//        view.setPressed(false);
//        boolean willNotCache = view.willNotCacheDrawing();
//        view.setWillNotCacheDrawing(false);
//        int color = view.getDrawingCacheBackgroundColor();
//        view.setDrawingCacheBackgroundColor(0);
//        if (color != 0) {
//            view.destroyDrawingCache();
//        }
//        view.buildDrawingCache();
//        Bitmap cacheBitmap = view.getDrawingCache();
//        if (cacheBitmap == null) {
//            return null;
//        }
//        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
//        view.destroyDrawingCache();
//        view.setWillNotCacheDrawing(willNotCache);
//        view.setDrawingCacheBackgroundColor(color);
//
//        return bitmap;
        Bitmap b = Bitmap.createBitmap(view.getLayoutParams().width, view.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(c);
        return b;
    }
}
