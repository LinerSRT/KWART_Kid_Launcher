package com.sgtc.launcher.ClockSkin.View;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;


import java.util.ArrayList;
import java.util.List;

public class DigitalImageBuilder {
    private static float SCALE =  Resources.getSystem().getDisplayMetrics().widthPixels / 400f;
    private List<DigitalObject> digitalObjectList;

    public DigitalImageBuilder() {
        this.digitalObjectList = new ArrayList<>();
    }



    public void addSlices(List<?> imageSet, int[] indexes, int[] dividers){
        if(dividers != null){
            for (int value : indexes) {
                if(value == -1)
                    continue;
                for (int divider : dividers) {
                    if (value == divider) {
                        addSlice(new DigitalObject(
                                imageSet.get(value),
                                DigitalObject.ItemType.DIVIDER,
                                DigitalObject.DividerVisibility.VISIBLE
                        ));
                    } else {
                        addSlice(new DigitalObject(
                                imageSet.get(value),
                                DigitalObject.ItemType.SLICE,
                                DigitalObject.DividerVisibility.VISIBLE
                        ));
                    }
                    break;
                }
            }
        } else {
            for(int index:indexes) {
                if (index == -1)
                    continue;
                addSlice(new DigitalObject(
                        imageSet.get(index),
                        DigitalObject.ItemType.SLICE,
                        DigitalObject.DividerVisibility.VISIBLE
                ));
            }
        }
    }

    public void addSlices(List<?> imageSet, int[] indexes){
        addSlices(imageSet, indexes, null);
    }


    public void addSlice(DigitalObject digitalObject) {
        this.digitalObjectList.add(digitalObject);
    }

    public void clear() {
        this.digitalObjectList.clear();
    }

    public Bitmap buildImage() {
        int width = calculateSlicesWidth() + calculateDividersWidth();
        int height = calculateMaxHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int drawPosX = 0;
        for (DigitalObject digitalObject : digitalObjectList) {
            //TODO add instance check for other types images
            Drawable drawable = (Drawable) digitalObject.getResource();
            int drawableWidth = getItemWidth(drawable);
            int drawableHeight = getItemHeight(drawable);
            switch (digitalObject.getType()) {
                case DIVIDER:
                    switch (digitalObject.getDividerVisibility()) {
                        case INVISIBLE:
                            drawPosX += drawableWidth;
                            break;
                        case VISIBLE:
                            drawable.setBounds(drawPosX, 0, drawPosX + drawableWidth, drawableHeight);
                            drawable.draw(canvas);
                            drawPosX += drawableWidth;
                            break;
                        case GONE:
                            //TODO do not draw and not add margin
                            break;
                    }
                    break;
                case SLICE:
                    drawable.setBounds(drawPosX, 0, drawPosX + drawableWidth, drawableHeight);
                    drawable.draw(canvas);
                    drawPosX += drawableWidth;
                    break;
            }
        }
        return bitmap;

    }


    public void draw(Canvas canvas, int positionX, int positionY) {
        Bitmap result = buildImage();
        canvas.drawBitmap(result, Math.round(positionX - (result.getWidth() / 2f)), Math.round(positionY - (result.getHeight() / 2f)), null);
    }


    private int calculateSlicesWidth() {
        int slicesWidth = 0;
        for (DigitalObject digitalObject : digitalObjectList) {
            if (digitalObject.getType() == DigitalObject.ItemType.SLICE) {
                slicesWidth += getItemWidth(digitalObject.getResource());
            }
        }
        return slicesWidth;
    }


    private int calculateDividersWidth() {
        int dividersWidth = 0;
        for (DigitalObject digitalObject : digitalObjectList) {
            if (digitalObject.getType() == DigitalObject.ItemType.DIVIDER) {
                dividersWidth += getItemWidth(digitalObject.getResource());
            }
        }
        return dividersWidth;
    }

    private int calculateMaxHeight(){
        int height = 0;
        for (DigitalObject digitalObject : digitalObjectList) {
            height = Math.max(height, getItemHeight(digitalObject.getResource()));
        }
        return height;
    }

    private int getItemWidth(Object item) {
        if (item instanceof Drawable) {
            Drawable drawable = (Drawable) item;
            return Math.round(drawable.getIntrinsicWidth() * SCALE);
        } else {
            return 0;
        }
    }

    private int getItemHeight(Object item) {
        if (item instanceof Drawable) {
            Drawable drawable = (Drawable) item;
            return Math.round(drawable.getIntrinsicHeight() * SCALE);
        } else {
            return 0;
        }
    }
}
