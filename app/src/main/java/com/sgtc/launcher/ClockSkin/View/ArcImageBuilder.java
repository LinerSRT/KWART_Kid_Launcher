package com.sgtc.launcher.ClockSkin.View;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;


public class ArcImageBuilder {
    private static float SCALE =  Resources.getSystem().getDisplayMetrics().widthPixels / 400f;
    public static int DEFAULT_RADIUS = Resources.getSystem().getDisplayMetrics().widthPixels * 2 / 13;
    public static int DEFAULT_STROKE_WIDTH = 7;
    private int width;
    private int height;
    private String arcAccentColor;
    private String arcBackgroundColor;
    private ArcType arcType;
    private ArcBackground arcBackground;
    private float arcRadius;
    private int arcStrokeWidth;
    private float arcStartAngle;
    private float arcAngle;
    private Bitmap result;
    private Paint paint;
    private RectF rectF;

    public static class ArcConfigBuilder{
        private String arcAccentColor;
        private String arcBackgroundColor;
        private ArcType arcType;
        private ArcBackground arcBackground;
        private float arcRadius;
        private int arcStrokeWidth;
        private float arcStartAngle;
        private float arcAngle;

        public ArcConfigBuilder() {
            arcAccentColor = "#990000";
            arcBackgroundColor = "#000000";
            arcType = ArcType.NORMAL;
            arcBackground = ArcBackground.COLOR;
            arcRadius = DEFAULT_RADIUS;
            arcStrokeWidth = DEFAULT_STROKE_WIDTH;
            arcStartAngle = 270f;
            arcAngle = 0;
        }

        public ArcConfigBuilder setArcAccentColor(String value) {
            this.arcAccentColor = value;
            return this;
        }

        public ArcConfigBuilder setArcBackgroundColor(String value) {
            this.arcBackgroundColor = value;
            return this;
        }

        public ArcConfigBuilder setArcType(ArcType value) {
            this.arcType = value;
            return this;
        }

        public ArcConfigBuilder setArcBackground(ArcBackground value) {
            this.arcBackground = value;
            return this;
        }

        public ArcConfigBuilder setArcRadius(float value) {
            this.arcRadius = value;
            return this;
        }

        public ArcConfigBuilder setArcStrokeWidth(int value) {
            this.arcStrokeWidth = value;
            return this;
        }

        public ArcConfigBuilder setArcStartAngle(float value) {
            this.arcStartAngle = value;
            return this;
        }

        public ArcConfigBuilder setArcAngle(float value) {
            this.arcAngle = value;
            return this;
        }

        public ArcImageBuilder build(){
            return new ArcImageBuilder(this);
        }
    }

    public ArcImageBuilder(ArcConfigBuilder configBuilder){
        this.arcAccentColor = configBuilder.arcAccentColor;
        this.arcBackgroundColor = configBuilder.arcBackgroundColor;
        this.arcType = configBuilder.arcType;
        this.arcBackground = configBuilder.arcBackground;
        this.arcRadius = configBuilder.arcRadius;
        this.arcStrokeWidth = configBuilder.arcStrokeWidth;
        this.arcStartAngle = configBuilder.arcStartAngle;
        this.arcAngle = configBuilder.arcAngle;
        width = Math.round(arcRadius * 2);
        height = Math.round(arcRadius * 2);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(arcStrokeWidth);
        paint.setAlpha(255);
        paint.setColor(Color.parseColor("#"+arcAccentColor));
        rectF = new RectF(arcStrokeWidth * SCALE, arcStrokeWidth * SCALE, width - (arcStrokeWidth * SCALE), height - (arcStrokeWidth * SCALE));
        createArc();
    }

    private void createArc(){
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        switch (arcType){
            case NORMAL:
                switch (arcBackground){
                    case NONE:
                        //TODO Do nothing
                        break;
                    case COLOR:
                        paint.setColor(Color.parseColor("#"+arcBackgroundColor));
                        canvas.drawCircle(width / 2f, height / 2f, arcRadius - (arcStrokeWidth * SCALE), paint);
                        paint.setColor(Color.parseColor("#"+arcAccentColor));
                        break;
                    case IMAGE:
                        //TODO not implemented
                        break;
                }
                canvas.drawArc(rectF, arcStartAngle, arcAngle, false, paint);
                break;
            case SLICED:

                break;
        }
        this.result = bitmap;
    }

    public Bitmap getResult() {
        return result;
    }

    public void draw(Canvas canvas, int positionX, int positionY) {
        canvas.drawBitmap(result, Math.round(positionX - (result.getWidth() / 2f)), Math.round(positionY - (result.getHeight() / 2f)), null);

    }


    public Bitmap addToCenter(Bitmap bitmap) {
        if (result != null) {
            Bitmap output = Bitmap.createBitmap(result);
            Canvas canvas = new Canvas(output);
            canvas.drawBitmap(bitmap, Math.round((result.getWidth() / 2f) - (bitmap.getWidth() / 2f)), Math.round((result.getHeight() / 2f) - (bitmap.getHeight() / 2f)), null);
            result = output;
            return output;
        }
        return null;
    }

    public Bitmap addToCenter(String text, int textColor, int textSize) {
        if (result != null) {
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
            Bitmap output = Bitmap.createBitmap(result);
            Canvas canvas = new Canvas(output);
            Rect rect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), rect);
            canvas.drawText(text, (result.getWidth() / 2f) - rect.width()/2f, (result.getHeight() / 2f)+rect.height()/2f, textPaint);
            result = output;
            return output;
        }
        return null;
    }


    public enum ArcType{
        NORMAL,
        SLICED
    }

    public enum ArcBackground{
        NONE,
        COLOR,
        IMAGE
    }


}
