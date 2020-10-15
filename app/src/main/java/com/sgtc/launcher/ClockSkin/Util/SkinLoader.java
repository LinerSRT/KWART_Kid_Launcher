package com.sgtc.launcher.ClockSkin.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Xml;


import com.sgtc.launcher.ClockSkin.Model.ClockSkin;
import com.sgtc.launcher.ClockSkin.View.ClockEngineConstants;
import com.sgtc.launcher.ClockSkin.View.ClockEngineLayer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkinLoader {
    private Context context;

    public interface ISkinLoader{
        void onLoadFinished(List<ClockEngineLayer> layers);
        void onLoadFailed();
    }

    public SkinLoader(Context context){
        this.context = context;
    }

    public void load(ClockSkin clockSkin, ISkinLoader loader){
        List<ClockEngineLayer> clockSkinLayers = null;
        ClockEngineLayer clockSkinLayer = null;
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput((clockSkin.isExternalStorage())?new FileInputStream(new File(clockSkin.getManifestPath())):context.getAssets().open(clockSkin.getManifestPath()), "UTF-8");
            int eventType = xmlPullParser.getEventType();
            boolean isDone = false;
            String localName;
            while (!isDone) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        clockSkinLayers = new ArrayList<>();
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        isDone = true;
                        loader.onLoadFinished(clockSkinLayers);
                        break;

                    case XmlPullParser.START_TAG: {
                        localName = xmlPullParser.getName();
                        if (ClockEngineConstants.TAG_DRAWABLE.equals(localName)) {
                            clockSkinLayer = new ClockEngineLayer();
                        } else if (clockSkinLayer != null) {
                            switch (localName) {
                                case ClockEngineConstants.TAG_NAME:
                                    localName = xmlPullParser.nextText();
                                    int index = localName.lastIndexOf(".");
                                    if (localName.contains(".gif")) {
                                        try {
                                            InputStream inputStream = (clockSkin.isExternalStorage())?new FileInputStream(new File(clockSkin.getClockPath()+ File.separator + localName)):context.getAssets().open(clockSkin.getClockPath() + File.separator + localName);
                                            clockSkinLayer.setBackground(inputStream);
                                        } catch (FileNotFoundException e){
                                            e.printStackTrace();
                                            continue;
                                        }
                                    } else {
                                        if (index > 0) {
                                            if (ClockEngineConstants.TAG_DRAWABLE_FILE_TYPE.equalsIgnoreCase(localName.substring(index + 1))) {
                                                loadDrawableArrayV2(clockSkinLayer, localName, clockSkin.isExternalStorage(), clockSkin, loader);
                                            } else if (ClockEngineConstants.TAG_DRAWABLE_TYPE.equalsIgnoreCase(localName.substring(index + 1))) {
                                                try {
                                                    InputStream inputStream = (clockSkin.isExternalStorage())?new FileInputStream(new File(clockSkin.getClockPath() + File.separator + localName)):context.getAssets().open(clockSkin.getClockPath() + File.separator + localName);
                                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                                                    bitmap.setDensity(displayMetrics.densityDpi);
                                                    clockSkinLayer.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                                                } catch (FileNotFoundException e){
                                                    e.printStackTrace();
                                                    continue;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case ClockEngineConstants.TAG_CENTERX:
                                    clockSkinLayer.setPositionX(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_CENTERY:
                                    clockSkinLayer.setPositionY(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_ROTATE:
                                    clockSkinLayer.setRotate(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_MUL_ROTATE:
                                    clockSkinLayer.setMulRotate(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_OFFSET_ANGLE:
                                    clockSkinLayer.setAngle(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_ARRAY_TYPE:
                                    clockSkinLayer.setLayerType(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_COLOR:
                                    clockSkinLayer.setColor(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_START_ANGLE:
                                    clockSkinLayer.setStartAngle(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_DIRECTION:
                                    clockSkinLayer.setDirection(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_TEXT_SIZE:
                                    clockSkinLayer.setTextsize(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_COLOR_ARRAY:
                                    clockSkinLayer.setColorArray(xmlPullParser.nextText());
                                    break;
                                case ClockEngineConstants.TAG_COUNT:
                                    int count = Integer.parseInt(xmlPullParser.nextText());
                                    ArrayList<ClockEngineLayer.DrawableInfo> infoArrayList = new ArrayList<>();
                                    for (int i = 0; i < count; i++) {
                                        ClockEngineLayer.DrawableInfo drawableInfo = new ClockEngineLayer.DrawableInfo();
                                        drawableInfo.setDrawable(clockSkinLayer.getBackground());
                                        drawableInfo.setDrawableScale(1);
                                        drawableInfo.setRotateSpeed(new Random().nextFloat() / 2);
                                        drawableInfo.setDrawableX(new Random().nextInt(400));
                                        drawableInfo.setDrawableY(new Random().nextInt(400));
                                        infoArrayList.add(drawableInfo);
                                    }
                                    clockSkinLayer.setClockEngineInfos(infoArrayList);
                                    break;
                                case ClockEngineConstants.TAG_DURATION:
                                    int i = Integer.parseInt(xmlPullParser.nextText());
                                    clockSkinLayer.setDuration(i);
                                    break;
                                case ClockEngineConstants.TAG_FRAMERATE:
                                    clockSkinLayer.setFramerateClockSkin((float) xmlPullParser.next());
                                    break;
                                case ClockEngineConstants.TAG_VALUE_TYPE:
                                    clockSkinLayer.setValusType(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_PROGRESS_DILIVER_COUNT:
                                    clockSkinLayer.setProfressDiliverCount(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_PROGRESS_DILIVER_ARC:
                                    clockSkinLayer.setProgressDiliverArc(Float.parseFloat(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_PROGRESS_RADIUS:
                                    clockSkinLayer.setCircleRadius(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_PROGRESS_STROKEN:
                                    clockSkinLayer.setCircleStroken(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                                case ClockEngineConstants.TAG_PICTURE_SHADOW:
                                    Bitmap bmp;
                                    if (clockSkin.isExternalStorage()) {
                                        String drawable = clockSkin.getClockPath() + xmlPullParser.nextText();
                                        bmp = BitmapFactory.decodeFile(drawable);
                                    } else {
                                        try {
                                            InputStream in = context.getAssets().open(clockSkin.getClockPath() + File.separator + xmlPullParser.nextText());
                                            bmp = BitmapFactory.decodeStream(in);
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                            bmp = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                                        }
                                    }
                                    DisplayMetrics dm = context.getResources().getDisplayMetrics();
                                    try {
                                        bmp.setDensity(dm.densityDpi);
                                    } catch (RuntimeException e) {
                                        e.printStackTrace();
                                    }
                                    BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bmp);
                                    clockSkinLayer.setShadowDrawable(bitmapDrawable);
                                    break;
                                case ClockEngineConstants.TAG_PACKAGE_NAME:
                                    clockSkinLayer.setPackageName(xmlPullParser.nextText());
                                    break;
                                case ClockEngineConstants.TAG_CLASS_NAME:
                                    clockSkinLayer.setClassName(xmlPullParser.nextText());
                                    break;
                                case ClockEngineConstants.TAG_RANGE:
                                    clockSkinLayer.setRange(Integer.parseInt(xmlPullParser.nextText()));
                                    break;
                            }
                        }
                    }
                    break;
                    case XmlPullParser.END_TAG: {
                        localName = xmlPullParser.getName();
                        if (ClockEngineConstants.TAG_DRAWABLE.equals(localName)) {
                            if (clockSkinLayer != null) {
                                if (clockSkinLayer.getLayerType() == ClockEngineConstants.ARRAY_ROTATE_ANIMATION) {
                                    clockSkinLayer.setCurrentAngle(clockSkinLayer.getStartAngle());
                                    if (clockSkinLayer.getDuration() != 0) {
                                        clockSkinLayer.setRotateSpeed(clockSkinLayer.getAngle() * 1.0f / clockSkinLayer.getDuration());
                                    }
                                    clockSkinLayer.setDirection(1);
                                }
                                try {
                                    if (clockSkinLayers != null) {
                                        clockSkinLayers.add(clockSkinLayer);
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                    loader.onLoadFailed();
                                    return;
                                }
                                clockSkinLayer = null;
                            }
                        }

                    }
                    break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

    }

    private void loadDrawableArrayV2(ClockEngineLayer paramDrawableInfo, String arrayName, boolean isExStorage, ClockSkin clockSkin, ISkinLoader loader) {
        int eventType;
        String itemName;
        ArrayList<Drawable> drawables = null;
        ArrayList<Integer> durations = null;
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            InputStream inputStream;
            if(isExStorage){
                try {
                    inputStream = new FileInputStream(new File( clockSkin.getClockPath() + File.separator + arrayName));
                } catch (FileNotFoundException e){
                    loader.onLoadFailed();
                    e.printStackTrace();
                    return;
                }
                xmlPullParser.setInput(inputStream, "UTF-8");
            }else{
                try {
                    inputStream = context.getAssets().open(clockSkin.getClockPath() + File.separator + arrayName);
                } catch (FileNotFoundException e){
                    loader.onLoadFailed();
                    e.printStackTrace();
                    return;
                }
                xmlPullParser.setInput(inputStream, "UTF-8");
            }
            eventType = xmlPullParser.getEventType();
            boolean isDone = false;
            while (!isDone) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        if (drawables == null) {
                            drawables = new ArrayList<>();
                        }
                        if (durations == null) {
                            durations = new ArrayList<>();
                        }
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        isDone = true;
                        paramDrawableInfo.setDrawableArrays(drawables);
                        paramDrawableInfo.setDurationArrays(durations);
                        break;
                    case XmlPullParser.START_TAG:
                        itemName = xmlPullParser.getName();
                        if (ClockEngineConstants.TAG_IMAGE.equals(itemName)) {
                            Bitmap bmp;
                            if(isExStorage){
                                String ss = clockSkin.getClockPath()+File.separator+ xmlPullParser.nextText();
                                try ( FileInputStream is = new FileInputStream(new File(ss)) ) {
                                    bmp = BitmapFactory.decodeStream( is );
                                } catch (Exception e){
                                    e.printStackTrace();
                                    bmp = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                                }
                            }else{
                                try ( InputStream in = context.getAssets().open(clockSkin.getClockPath() + File.separator + xmlPullParser.nextText()) ) {
                                    bmp = BitmapFactory.decodeStream( in );
                                } catch (Exception e){
                                    e.printStackTrace();
                                    bmp = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                                }
                            }
                            DisplayMetrics dm = context.getResources().getDisplayMetrics();
                            bmp.setDensity(dm.densityDpi);
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bmp);
                            if (drawables != null) {
                                drawables.add(bitmapDrawable);
                            }
                        } else if (ClockEngineConstants.TAG_NAME.equals(itemName)) {
                            Bitmap bmp;
                            if(isExStorage){
                                String ss = clockSkin.getClockPath() +File.separator+ xmlPullParser.nextText();
                                bmp = BitmapFactory.decodeFile(ss);
                            }else{
                                InputStream in = context.getAssets().open(clockSkin.getClockPath() +File.separator+ xmlPullParser.nextText());
                                bmp = BitmapFactory.decodeStream(in);

                            }
                            DisplayMetrics dm = context.getResources().getDisplayMetrics();
                            bmp.setDensity(dm.densityDpi);
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bmp);
                            if (drawables != null) {
                                drawables.add(bitmapDrawable);
                            }
                        } else if (ClockEngineConstants.TAG_DURATION.equals(itemName)) {
                            int i = Integer.valueOf(xmlPullParser.nextText());
                            //lockDrawUtils.setAnimationTimeCount(clockDrawUtils.getAnimationTimeCount()+i);
                            if (durations != null) {
                                durations.add(i);
                            }
                            paramDrawableInfo.setDurationArrays(durations);
                        }else if(ClockEngineConstants.TAG_CHILD_FOLDER.equals(itemName)){
                            paramDrawableInfo.setChildrenFolderName(xmlPullParser.nextText());
                        }
                        break;
                }
                eventType = xmlPullParser.next();
            }
            inputStream.close();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

}
