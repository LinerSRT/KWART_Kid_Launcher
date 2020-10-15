package com.sgtc.launcher.ClockSkin.View;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.sgtc.launcher.ClockSkin.Model.ClockSkin;
import com.sgtc.launcher.ClockSkin.Util.DataUnit;
import com.sgtc.launcher.ClockSkin.Util.SkinLoader;
import com.sgtc.launcher.ClockSkin.Util.TimeUnit;
import com.sgtc.launcher.Config;
import com.sgtc.launcher.LauncherApplication;
import com.sgtc.launcher.R;
import com.sgtc.launcher.util.PM;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;


public class Clock extends View implements Runnable {
    private static long startAnimationTime = 0;
    private long animationTimeCount = 0;
    private static float SCALE = Resources.getSystem().getDisplayMetrics().widthPixels / 400f;
    private static List<ClockEngineLayer> clockSkinLayers = null;
    private static int frame = 0;
    private static long lastTime = 0;
    private final ValueAnimator animationHide = ValueAnimator.ofPropertyValuesHolder(PropertyValuesHolder.ofFloat("Alpha", 1f, 0.6f));
    private final ValueAnimator animationShow = ValueAnimator.ofPropertyValuesHolder(PropertyValuesHolder.ofFloat("Alpha", 0.6f, 1f));

    private Context context;
    private Thread thread;
    private boolean isRunning = false;
    private Movie backgroundRender;
    private long renderStart = 0;
    private int renderCurrentTime = 0;
    private float renderLeft = 0f;
    private float renderTop = 0f;
    private float renderScale;
    private Handler handler;
    private boolean isHWAccelerated = true;
    private boolean loadSuccess = false;
    private Drawable errorLoadDrawable;
    private DigitalImageBuilder digitalImageBuilder = new DigitalImageBuilder();

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public Clock(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        errorLoadDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_warning_24);
        handler = new Handler();
    }

    public void selectSkin(final ClockSkin skin) {
        stopDraw();
        clockSkinLayers = null;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                new SkinLoader(context).load(skin, new SkinLoader.ISkinLoader() {
                    @Override
                    public void onLoadFinished(List<ClockEngineLayer> layers) {
                        clockSkinLayers = layers;
                        if (clockSkinLayers != null) {
                            if (clockSkinLayers.get(0).getBackground() instanceof InputStream) {
                                isHWAccelerated = true;
                                backgroundRender = Movie.decodeStream((InputStream) clockSkinLayers.get(0).getBackground());
                                if (backgroundRender != null) {
                                    int movieWidth = backgroundRender.width();
                                    int movieHeight = backgroundRender.height();
                                    renderScale = (float) Resources.getSystem().getDisplayMetrics().widthPixels / (float) (Math.min(movieHeight, movieWidth));
                                    renderLeft = (((((float) Resources.getSystem().getDisplayMetrics().widthPixels - (movieWidth * renderScale))) / 2f)) / renderScale;
                                    if (Resources.getSystem().getDisplayMetrics().heightPixels < Resources.getSystem().getDisplayMetrics().widthPixels) {
                                        renderTop = (((((float) Resources.getSystem().getDisplayMetrics().widthPixels - (movieHeight * renderScale))) / 2f)) / renderScale;
                                    } else {
                                        renderTop = (((((float) Resources.getSystem().getDisplayMetrics().heightPixels - (movieHeight * renderScale))) / 2f)) / renderScale;
                                    }
                                }
                            } else {
                                isHWAccelerated = false;
                            }
                        }
                        loadSuccess = true;
                        startDraw();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSkin();
                            }
                        });
                    }

                    @Override
                    public void onLoadFailed() {
                        loadSuccess = false;
                        startDraw();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSkin();
                            }
                        });
                    }
                });
            }
        });
        thread.start();
    }

    public void selectSkin(String clockSkinPath) {
        for (ClockSkin skin : LauncherApplication.getClockSkinList()) {
            if (skin.getClockPath().equals(clockSkinPath)) {
                selectSkin(skin);
            }
        }
    }

    public void selectLastUsedSkin() {
        stopDraw();
        clockSkinLayers = null;
        List<ClockSkin> clockSkins = LauncherApplication.getClockSkinList();
        if (clockSkins.isEmpty()) {
            selectLastUsedSkin();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectLastUsedSkin();
                }
            }, 1000);
        }
        selectSkin(clockSkins.get((Integer) PM.get(Config.WATCHFACE_SELECTED_INDEX, 0)));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showSkin();
            }
        });
    }


    public void onTouch(int x, int y) {
        if (clockSkinLayers != null)
            for (ClockEngineLayer item : clockSkinLayers) {
                if (item.getLayerType() == ClockEngineConstants.ARRAY_TOUCH) {
                    int centerXTouch = item.getPositionX();
                    int centerYTouch = item.getPositionY();
                    int touchRange = item.getRange();
                    if (x < (centerXTouch + touchRange) && x > (centerXTouch - touchRange)) {
                        if (y < (centerYTouch + touchRange) && y > (centerYTouch - touchRange)) {
                            Intent intent = new Intent();
                            intent.setClassName(item.getPackageName(), item.getClassName());
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (!loadSuccess) {
            canvas.save();
            //clockDrawUtils.drawImage(canvas, clockDrawUtils.zoomDrawable(errorLoadDrawable, 2), getWidth() / 2, getHeight() / 2);
            canvas.restore();
        } else {
            if (clockSkinLayers != null && clockSkinLayers.size() > 0) {
                try {
                    for (ClockEngineLayer clockEngineLayer : clockSkinLayers) {
                        switch (clockEngineLayer.getRotate()) {
                            case ClockEngineConstants.ROTATE_NONE: {
                                if (clockEngineLayer.getLayerType() > 0) {
                                    switch (clockEngineLayer.getLayerType()) {
                                        case ClockEngineConstants.ARRAY_YEAR_MONTH_DAY:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        TimeUnit.year / 1000,
                                                        TimeUnit.year / 100 % 10,
                                                        TimeUnit.year / 10 % 10,
                                                        TimeUnit.year % 10,
                                                        10,
                                                        TimeUnit.month / 10,
                                                        TimeUnit.month % 10,
                                                        10,
                                                        TimeUnit.day / 10,
                                                        TimeUnit.day % 10

                                                }, new int[]{4, 7});
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_MONTH_DAY:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        TimeUnit.month / 10,
                                                        TimeUnit.month % 10,
                                                        10,
                                                        TimeUnit.day / 10,
                                                        TimeUnit.day % 10
                                                }, new int[]{2});
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;

                                        case ClockEngineConstants.ARRAY_MONTH:
                                            int value = TimeUnit.month;
                                            if(TimeUnit.month == 0)
                                                value += 1;
                                            if ((clockEngineLayer.getDrawableArrays() != null) && clockEngineLayer.getDrawableArrays().size() > (value - 1)) {
                                                drawImage(canvas, clockEngineLayer.getDrawableArrays().get(value - 1), clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_MONTH_NEW:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && clockEngineLayer.getDrawableArrays().size() > 0) {
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        (TimeUnit.month + 1) / 10,
                                                        (TimeUnit.month) % 10
                                                });
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_DAY:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && clockEngineLayer.getDrawableArrays().size() > 0) {
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        TimeUnit.day / 10,
                                                        TimeUnit.day % 10
                                                });
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_DAY_NEW:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && clockEngineLayer.getDrawableArrays().size() > 0) {
                                                drawImage(canvas, clockEngineLayer.getDrawableArrays().get(TimeUnit.day - 1), clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;

                                        case ClockEngineConstants.ARRAY_WEEKDAY:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && clockEngineLayer.getDrawableArrays().size() >= TimeUnit.dayOfWeek - 1) {
                                                drawImage(canvas, clockEngineLayer.getDrawableArrays().get(((TimeUnit.dayOfWeek == 0) ? 0 : TimeUnit.dayOfWeek - 1)), clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_HOUR_MINUTE:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                Drawable amPm = null;
                                                if (!DateFormat.is24HourFormat(context) && clockEngineLayer.getDrawableArrays().size() == 13) {
                                                    amPm = clockEngineLayer.getDrawableArrays().get(TimeUnit.hour >= 12 ? 12 : 11);
                                                    TimeUnit.hour = TimeUnit.hour % 12;
                                                    if (TimeUnit.hour == 0) {
                                                        TimeUnit.hour = 12;
                                                    }
                                                }
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        TimeUnit.hour / 10,
                                                        TimeUnit.hour % 10,

                                                });
                                                digitalImageBuilder.addSlice(new DigitalObject(
                                                        clockEngineLayer.getDrawableArrays().get(10),
                                                        DigitalObject.ItemType.DIVIDER,
                                                        (TimeUnit.second % 2 == 0) ? DigitalObject.DividerVisibility.VISIBLE : DigitalObject.DividerVisibility.INVISIBLE
                                                ));
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        TimeUnit.minute / 10,
                                                        TimeUnit.minute % 10,

                                                });
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_HOUR:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        TimeUnit.hour / 10,
                                                        TimeUnit.hour % 10,

                                                });
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_MINUTE: {
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        TimeUnit.minute / 10,
                                                        TimeUnit.minute % 10,

                                                });
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                        }
                                        break;
                                        case ClockEngineConstants.ARRAY_SECOND: {
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        TimeUnit.second / 10,
                                                        TimeUnit.second % 10,

                                                });
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                        }
                                        break;
                                        case ClockEngineConstants.ARRAY_SECOND_NEW: {
                                            //ArcImageBuilder arcImageBuilder = new ArcImageBuilder(Resources.getSystem().getDisplayMetrics().widthPixels /2);
                                            //arcImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                        }
                                        break;

                                        case ClockEngineConstants.ARRAY_WEATHER: {
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                drawImage(canvas, clockEngineLayer.getDrawableArrays().get(DataUnit.weatherIcon), clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                        }
                                        break;
                                        case ClockEngineConstants.ARRAY_TEMPERATURE: {
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlice(new DigitalObject(
                                                        clockEngineLayer.getDrawableArrays().get(10),
                                                        DigitalObject.ItemType.DIVIDER,
                                                        (DataUnit.weatherTemp < 0) ? DigitalObject.DividerVisibility.VISIBLE : DigitalObject.DividerVisibility.GONE
                                                ));
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        Math.abs(DataUnit.weatherTemp / 10),
                                                        Math.abs(DataUnit.weatherTemp % 10),
                                                        11

                                                }, new int[]{2});
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                        }
                                        break;
                                        case ClockEngineConstants.ARRAY_STEPS: {
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                int index = Math.min(Math.max(DataUnit.steps, 0), 99999);
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        index / 10000,
                                                        index / 1000 % 10,
                                                        index / 100 % 10,
                                                        index / 10 % 10,
                                                        index % 10

                                                });
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                        }
                                        break;
                                        case ClockEngineConstants.ARRAY_STEPS_NEW: {
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                int index = Math.min(Math.max(DataUnit.steps, 0), 99999);
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        ((index / 10000) == 0) ? -1 : index / 10000,
                                                        ((index / 1000 % 10) == 0) ? -1 : index / 1000 % 10,
                                                        ((index / 100 % 10) == 0) ? -1 : index / 100 % 10,
                                                        ((index / 10 % 10) == 0) ? -1 : index / 10 % 10,
                                                        index % 10
                                                });
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                        }
                                        break;
                                        case ClockEngineConstants.ARRAY_KCAL_NEW: {
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                int index = Math.min(Math.max(DataUnit.calories, 0), 99999);
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        ((index / 10000) == 0) ? -1 : index / 10000,
                                                        ((index / 1000 % 10) == 0) ? -1 : index / 1000 % 10,
                                                        ((index / 100 % 10) == 0) ? -1 : index / 100 % 10,
                                                        ((index / 10 % 10) == 0) ? -1 : index / 10 % 10,
                                                        index % 10,
                                                        clockEngineLayer.getDrawableArrays().size() - 1,
                                                        index * 10 % 10

                                                });
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                        }
                                        break;
                                        case ClockEngineConstants.ARRAY_BATTERY: {
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                digitalImageBuilder.clear();
                                                int index1 = ((DataUnit.batteryLevel / 100) == 0) ? -1 : DataUnit.batteryLevel / 100;
                                                int index2 = ((DataUnit.batteryLevel / 10) == 0) ? -1 : DataUnit.batteryLevel / 10;
                                                int index3 = DataUnit.batteryLevel % 10;
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        index1,
                                                        index2,
                                                        index3,
                                                        (clockEngineLayer.getDrawableArrays().size() > 11) ? 11 : -1
                                                }, (clockEngineLayer.getDrawableArrays().size() > 11) ? new int[]{3} : null);
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                        }
                                        break;
                                        case ClockEngineConstants.ARRAY_SPECIAL_SECOND:
                                            if (clockEngineLayer.getColorArray() != null) {
                                                drawSpecialSecond(canvas, clockEngineLayer.getColorArray(), TimeUnit.minute, TimeUnit.second);
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_YEAR:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                digitalImageBuilder.clear();
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        TimeUnit.year / 1000,
                                                        TimeUnit.year / 100 % 10,
                                                        TimeUnit.year / 10 % 10,
                                                        TimeUnit.year % 10
                                                });
                                                digitalImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_BATTERY_WITH_CIRCLE:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)
                                                    && clockEngineLayer.getColorArray() != null) {
                                                ArcImageBuilder arcImageBuilder = new ArcImageBuilder.ArcConfigBuilder()
                                                        .setArcAngle(DataUnit.batteryLevel / 100f * 360.0F)
                                                        .setArcType(ArcImageBuilder.ArcType.NORMAL)
                                                        .setArcBackground(ArcImageBuilder.ArcBackground.COLOR)
                                                        .setArcAccentColor(clockEngineLayer.getColorArray().split(",")[1])
                                                        .setArcBackgroundColor(clockEngineLayer.getColorArray().split(",")[0]).build();
                                                digitalImageBuilder.clear();
                                                int index1 = (DataUnit.batteryLevel < 10) ? 10 : DataUnit.batteryLevel / 100;
                                                int index2 = (DataUnit.batteryLevel < 10) ? 10 : (DataUnit.batteryLevel / 10) % 10;
                                                int index3 = DataUnit.batteryLevel % 10;
                                                int index4 = (clockEngineLayer.getDrawableArrays().size() > 11) ? 11 : -1;
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(), new int[]{
                                                        (index1 == 0) ? -1 : index1,
                                                        (index2 == 0) ? -1 : index2,
                                                        index3,
                                                        index4
                                                }, (clockEngineLayer.getDrawableArrays().size() > 11) ? new int[]{11} : null);
                                                arcImageBuilder.addToCenter(digitalImageBuilder.buildImage());
                                                arcImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_BATTERY_WITH_CIRCLE_PIC:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                drawImage(canvas, clockEngineLayer.getDrawableArrays(), clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY(), DataUnit.batteryLevel / 10);
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_STEPS_WITH_CIRCLE:
                                            if ((clockEngineLayer.getDrawableArrays() != null) &&
                                                    (clockEngineLayer.getDrawableArrays().size() > 0) && (clockEngineLayer.getColorArray() != null)) {
                                                ArcImageBuilder arcImageBuilder = new ArcImageBuilder.ArcConfigBuilder()
                                                        .setArcAngle(DataUnit.steps / (float) DataUnit.targetSteps * 360.0F)
                                                        .setArcType(ArcImageBuilder.ArcType.NORMAL)
                                                        .setArcBackground(ArcImageBuilder.ArcBackground.COLOR)
                                                        .setArcAccentColor(clockEngineLayer.getColorArray().split(",")[1])
                                                        .setArcBackgroundColor(clockEngineLayer.getColorArray().split(",")[0]).build();
                                                digitalImageBuilder.clear();
                                                int index = Math.min(Math.max(DataUnit.steps, 0), 99999);
                                                digitalImageBuilder.addSlices(clockEngineLayer.getDrawableArrays(),
                                                        new int[]{
                                                                index / 10000,
                                                                index / 1000 % 10,
                                                                index / 100 % 10,
                                                                index / 10 % 10,
                                                                index % 10
                                                        });
                                                arcImageBuilder.addToCenter(digitalImageBuilder.buildImage());
                                                arcImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_STEPS_CIRCLE_NEW: {
                                            ArcImageBuilder arcImageBuilder = new ArcImageBuilder.ArcConfigBuilder()
                                                    .setArcAngle(DataUnit.steps / (float) DataUnit.targetSteps * 360.0F)
                                                    .setArcType(ArcImageBuilder.ArcType.NORMAL)
                                                    .setArcBackground(ArcImageBuilder.ArcBackground.COLOR)
                                                    .setArcAccentColor(clockEngineLayer.getColorArray().split(",")[1])
                                                    .setArcBackgroundColor(clockEngineLayer.getColorArray().split(",")[0]).build();
                                            arcImageBuilder.addToCenter(String.valueOf(DataUnit.steps), Color.WHITE, 22);
                                            arcImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                        }
                                        break;
                                        case ClockEngineConstants.ARRAY_BATTERY_CIRCLE_NEW: {
                                            if ((clockEngineLayer.getDrawableArrays() != null) &&
                                                    (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                ArcImageBuilder arcImageBuilder = new ArcImageBuilder.ArcConfigBuilder()
                                                        .setArcAngle(DataUnit.batteryLevel / 100f * 360.0F)
                                                        .setArcType(ArcImageBuilder.ArcType.NORMAL)
                                                        .setArcBackground(ArcImageBuilder.ArcBackground.COLOR)
                                                        .setArcAccentColor(clockEngineLayer.getColorArray().split(",")[1])
                                                        .setArcBackgroundColor(clockEngineLayer.getColorArray().split(",")[0]).build();
                                                arcImageBuilder.addToCenter(String.valueOf(DataUnit.batteryLevel) + "%", Color.WHITE, 22);
                                                arcImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            }
                                        }
                                        break;
                                        case ClockEngineConstants.ARRAY_AM_PM:
                                            if ((clockEngineLayer.getDrawableArrays() != null) && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                if (!DateFormat.is24HourFormat(context)) {
                                                    int index = (TimeUnit.hour >= 12) ? 1 : 0;
                                                    drawImage(canvas, clockEngineLayer.getDrawableArrays().get(index), clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                                }
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_FRAME_ANIMATION:
                                            Calendar calendar = Calendar.getInstance();
                                            if (startAnimationTime <= 0) {
                                                startAnimationTime = calendar.getTimeInMillis();
                                            }
                                            if (clockEngineLayer.getDrawableArrays() != null && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                if (clockEngineLayer.getDurationArrays() != null && (clockEngineLayer.getDurationArrays().size() > 0)) {
                                                    if (animationTimeCount > 0) {
                                                        long diff = calendar.getTimeInMillis() - startAnimationTime;
                                                        diff = diff % animationTimeCount;
                                                        for (int i = 0; i < clockEngineLayer.getDurationArrays().size(); i++) {
                                                            if (diff < clockEngineLayer.getDurationArrays().get(i)) {
                                                                drawImage(canvas, clockEngineLayer.getDrawableArrays().get(i), clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                                                break;
                                                            }
                                                            diff -= clockEngineLayer.getDurationArrays().get(i);
                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_ANIMATED_ARRAY:
                                            if (startAnimationTime <= 0) {
                                                startAnimationTime = Calendar.getInstance().getTimeInMillis();
                                            }
                                            if (clockEngineLayer.getDrawableArrays() != null && (clockEngineLayer.getDrawableArrays().size() > 0)) {
                                                if (clockEngineLayer.getDurationArrays() != null && (clockEngineLayer.getDurationArrays().size() > 0)) {
                                                    if (clockEngineLayer.getFramerateClockSkin() > 0) {
                                                        long diff = Calendar.getInstance().getTimeInMillis() - startAnimationTime;
                                                        diff = (long) (diff % clockEngineLayer.getFramerateClockSkin());
                                                        for (int i = 0; i < clockEngineLayer.getDrawableArrays().size(); i++) {
                                                            if (diff < clockEngineLayer.getDurationArrays().get(i)) {
                                                                drawImage(canvas, clockEngineLayer.getDrawableArrays().get(i), clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                                                break;
                                                            }
                                                            diff -= clockEngineLayer.getDurationArrays().get(i);
                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_ROTATE_ANIMATION:
                                            if (startAnimationTime <= 0) {
                                                Calendar calendar2 = Calendar.getInstance();
                                                startAnimationTime = calendar2.getTimeInMillis();
                                            }
                                            if (clockEngineLayer.getDirection() == ClockEngineConstants.ROTATE_CLOCKWISE) {
                                                clockEngineLayer.setCurrentAngle(clockEngineLayer.getCurrentAngle() + clockEngineLayer.getRotateSpeed());
                                            } else {
                                                clockEngineLayer.setCurrentAngle(clockEngineLayer.getCurrentAngle() - clockEngineLayer.getRotateSpeed());
                                            }
                                            if ((clockEngineLayer.getCurrentAngle() >= clockEngineLayer.getStartAngle() + clockEngineLayer.getAngle()) ||
                                                    clockEngineLayer.getCurrentAngle() <= clockEngineLayer.getStartAngle()) {
                                                if (clockEngineLayer.getDirection() == ClockEngineConstants.ROTATE_CLOCKWISE) {
                                                    clockEngineLayer.setDirection(ClockEngineConstants.ANTI_ROTATE_CLOCKWISE);
                                                } else {
                                                    clockEngineLayer.setDirection(ClockEngineConstants.ROTATE_CLOCKWISE);
                                                }
                                            }
                                            if (clockEngineLayer.getBackground() instanceof Drawable) {
                                                drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                                        clockEngineLayer.getPositionY(), clockEngineLayer.getCurrentAngle());
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_SNOW_ANIMATION:
                                            if (clockEngineLayer.getBackground() != null && clockEngineLayer.getClockEngineInfos() != null &&
                                                    clockEngineLayer.getClockEngineInfos().size() > 0) {
                                                if (startAnimationTime <= 0) {
                                                    Calendar calendar3 = Calendar.getInstance();
                                                    startAnimationTime = calendar3.getTimeInMillis();
                                                }
                                                for (ClockEngineLayer.DrawableInfo snowInfo : clockEngineLayer.getClockEngineInfos()) {
                                                    float y = snowInfo.getDrawableY() + snowInfo.getRotateSpeed();
                                                    if (y > ClockEngineLayer.SCREEN_SIZE) {
                                                        Random random = new Random();
                                                        y = random.nextInt(ClockEngineLayer.SCREEN_SIZE / 2);
                                                    }
                                                    snowInfo.setDrawableY(y);
                                                    int diffX = Math.abs((int) snowInfo.getDrawableX() - ClockEngineLayer.SCREEN_SIZE / 2);
                                                    int diffY = Math.abs((int) snowInfo.getDrawableY() - ClockEngineLayer.SCREEN_SIZE / 2);
                                                    if (diffX * diffX + diffY * diffY <= (ClockEngineLayer.SCREEN_SIZE * ClockEngineLayer.SCREEN_SIZE / 4)) {
                                                        drawImage(canvas, snowInfo.getDrawable(),
                                                                (int) snowInfo.getDrawableX(), (int) snowInfo.getDrawableY());
                                                    }
                                                }
                                            }
                                            break;
                                        case ClockEngineConstants.ARRAY_PICTURE_HOUR: {
                                            final int index = TimeUnit.hour * 5 + TimeUnit.minute % 12;
                                            final Drawable currDrawable = clockEngineLayer.getDrawableArrays().get(index);
                                            drawRotateImage(canvas, currDrawable, clockEngineLayer.getPositionX(),
                                                    clockEngineLayer.getPositionY(), 0);
                                            break;
                                        }
                                        case ClockEngineConstants.ARRAY_PICTURE_MINUTER: {
                                            final Drawable currDrawable = clockEngineLayer.getDrawableArrays().get(TimeUnit.minute);
                                            drawRotateImage(canvas, currDrawable, clockEngineLayer.getPositionX(),
                                                    clockEngineLayer.getPositionY(), 0);
                                            break;
                                        }
                                        case ClockEngineConstants.ARRAY_PICTURE_SECOND: {
                                            final Drawable currDrawable = clockEngineLayer.getDrawableArrays().get(TimeUnit.second);
                                            drawRotateImage(canvas, currDrawable, clockEngineLayer.getPositionX(),
                                                    clockEngineLayer.getPositionY(), 0);
                                            break;
                                        }
                                        case ClockEngineConstants.ARRAY_PICTURE_HOUR_DIGITE: {
                                            final Drawable currDrawable = clockEngineLayer.getDrawableArrays().get(TimeUnit.hour);
                                            drawImage(canvas, currDrawable,
                                                    clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            break;
                                        }
                                        case ClockEngineConstants.ARRAY_VALUE_WITH_PROGRESS: {
                                            try {
                                                float angle = (clockEngineLayer.getProgressDiliverArc() > 0) ? (360f / clockEngineLayer.getProfressDiliverCount() - clockEngineLayer.getProgressDiliverArc()) : 360 * 360.0F;
                                                ArcImageBuilder arcImageBuilder = new ArcImageBuilder.ArcConfigBuilder()
                                                        .setArcAngle(DataUnit.steps / angle * 360.0F)
                                                        .setArcType(ArcImageBuilder.ArcType.NORMAL)
                                                        .setArcBackground(ArcImageBuilder.ArcBackground.COLOR)
                                                        .setArcAccentColor(clockEngineLayer.getColorArray().split(",")[1])
                                                        .setArcBackgroundColor(clockEngineLayer.getColorArray().split(",")[0]).build();
                                                arcImageBuilder.addToCenter(String.valueOf(DataUnit.steps), Color.WHITE, clockEngineLayer.getTextsize());
                                                arcImageBuilder.draw(canvas, clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            //TODO MAY BE BUG!!!!!!!!!!
                                            break;
                                        }
                                        case ClockEngineConstants.ARRAY_VALUE_STRING: {
                                            int color = Integer.parseInt(clockEngineLayer.getColorArray().split(",")[0]) + 0xFF000000;
                                            drawText(canvas, clockEngineLayer.getValusType(), color, clockEngineLayer.getTextsize(), clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                            //TODO MAY BE BUG!!!!!!!!!!
                                            break;
                                        }
                                        case ClockEngineConstants.ARRAY_VALUE_WITH_CLIP_PICTURE: {
                                            drawImageClipped(canvas, clockEngineLayer.getDrawableArrays(), clockEngineLayer.getCenterXNew(), clockEngineLayer.getCenterYNew(), clockEngineLayer.getValusType());
                                            break;
                                        }
                                    }

                                } else {
                                    if (clockEngineLayer.getLayerType() == 0 && clockEngineLayer.getDrawableArrays() != null && clockEngineLayer.getDrawableArrays().size() > 0) {
                                        drawImage(canvas, clockEngineLayer.getDrawableArrays().get(frame), clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                        long cTime = System.currentTimeMillis();
                                        if (cTime - lastTime > ((long) (1000 / clockEngineLayer.getFrameRate()))) {
                                            lastTime = cTime;
                                            frame++;
                                            if (frame >= clockEngineLayer.getDrawableArrays().size()) {
                                                frame = 0;
                                            }
                                        }
                                    } else if (clockEngineLayer.getBackground() != null) {
                                        drawImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(), clockEngineLayer.getPositionY());
                                    }
                                }
                            }
                            break;
                            case ClockEngineConstants.ROTATE_HOUR: {
                                float hourAngle = TimeUnit.hour / 12.0f * 360.0f;
                                hourAngle += (float) (TimeUnit.minute * 30 / 60);
                                if (clockEngineLayer.getDirection() == ClockEngineConstants.ANTI_ROTATE_CLOCKWISE) {
                                    hourAngle = -hourAngle;
                                }
                                drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                        clockEngineLayer.getPositionY(), hourAngle + clockEngineLayer.getAngle());
                                if (clockEngineLayer.getShadowDrawable() != null) {
                                    drawRotateImage(canvas, clockEngineLayer.getShadowDrawable(), clockEngineLayer.getPositionX(),
                                            clockEngineLayer.getPositionY(), hourAngle + clockEngineLayer.getAngle());
                                }
                            }
                            break;
                            case ClockEngineConstants.ROTATE_MINUTE: {
                                float minuteAngle = TimeUnit.minute / 60.0f * 360.0f;
                                if (clockEngineLayer.getDirection() == ClockEngineConstants.ANTI_ROTATE_CLOCKWISE) {
                                    minuteAngle = -minuteAngle;
                                }
                                drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                        clockEngineLayer.getPositionY(), minuteAngle + clockEngineLayer.getAngle());
                                if (clockEngineLayer.getShadowDrawable() != null) {
                                    drawRotateImage(canvas, clockEngineLayer.getShadowDrawable(), clockEngineLayer.getPositionX(),
                                            clockEngineLayer.getPositionY(), minuteAngle + clockEngineLayer.getAngle());
                                }
                            }
                            break;
                            case ClockEngineConstants.ROTATE_SECOND:
                                float secondAngle = (TimeUnit.second * 1000 + TimeUnit.millisecond) * 6.0f / 1000.0f;
                                if (clockEngineLayer.getMulRotate() > 0) {
                                    secondAngle *= clockEngineLayer.getMulRotate();
                                } else if (clockEngineLayer.getMulRotate() < 0) {
                                    secondAngle = secondAngle / (Math.abs(clockEngineLayer.getMulRotate()));
                                }
                                drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                        clockEngineLayer.getPositionY(), secondAngle + clockEngineLayer.getAngle());
                                if (clockEngineLayer.getShadowDrawable() != null) {
                                    drawRotateImage(canvas, clockEngineLayer.getShadowDrawable(), clockEngineLayer.getPositionX(),
                                            clockEngineLayer.getPositionY(), secondAngle + clockEngineLayer.getAngle());
                                }
                                break;
                            case ClockEngineConstants.ROTATE_MONTH:
                                if (clockEngineLayer.getBackground() != null) {
                                    int maxDay = TimeUnit.getActualMaximumDay(TimeUnit.year, TimeUnit.month);
                                    float angle = (TimeUnit.month + 1) * 30.0F + TimeUnit.day * 30.0F / maxDay;
                                    drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                            clockEngineLayer.getPositionY(), angle + clockEngineLayer.getAngle());
                                }
                                break;
                            case ClockEngineConstants.ROTATE_WEEK:
                                if (clockEngineLayer.getBackground() != null) {
                                    float angle = (TimeUnit.dayOfWeek + TimeUnit.hour * 1.0F / 24) * 360.0F / 7;
                                    drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                            clockEngineLayer.getPositionY(), angle + clockEngineLayer.getAngle());
                                }
                                break;
                            case ClockEngineConstants.ROTATE_BATTERY:
                                if (clockEngineLayer.getBackground() != null) {
                                    int direction = clockEngineLayer.getDirection();
                                    final float startoffset = clockEngineLayer.getProgressDiliverArc();
                                    if (startoffset == 0) {
                                        float angle = DataUnit.batteryLevel * 180.0F / 100;
                                        drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                                clockEngineLayer.getPositionY(), angle * ((direction == 1) ? 1 : -1) + clockEngineLayer.getAngle());
                                    } else {
                                        float angle1 = DataUnit.batteryLevel * (180.0F - startoffset * 2) / 100;
                                        drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                                clockEngineLayer.getPositionY(), angle1 * ((direction == 1) ? 1 : -1) + clockEngineLayer.getAngle() + startoffset * ((direction == 1) ? 1 : -1));
                                    }
                                }
                                break;
                            case ClockEngineConstants.ROTATE_DAY_NIGHT:
                                if (clockEngineLayer.getBackground() != null) {
                                    float angle = TimeUnit.hour * 15.0F + TimeUnit.minute / 60.0F * 15.0F;
                                    drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                            clockEngineLayer.getPositionY(), angle + clockEngineLayer.getAngle());
                                }
                                break;
                            case ClockEngineConstants.ROTATE_HOUR_BG:
                                float hourAngle = TimeUnit.hour / 24.0f * 360.0f;
                                hourAngle += (float) (TimeUnit.minute * 30 / 60);
                                drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                        clockEngineLayer.getPositionY(), hourAngle + clockEngineLayer.getAngle());

                                break;
                            case ClockEngineConstants.ROTATE_BATTERY_CIRCLE:
                                if (clockEngineLayer.getBackground() != null) {
                                    int batteryLevel = DataUnit.batteryLevel;
                                    int direction = clockEngineLayer.getDirection();
                                    final float startoffset = clockEngineLayer.getProgressDiliverArc();
                                    if (startoffset == 0) {
                                        float angle = batteryLevel * 360.0F / 100;
                                        drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                                clockEngineLayer.getPositionY(), angle * ((direction == 1) ? 1 : -1) + clockEngineLayer.getAngle());
                                    } else {
                                        float angle1 = batteryLevel * (360.0F - startoffset * 2) / 100;
                                        drawRotateImage(canvas, clockEngineLayer.getBackground(), clockEngineLayer.getPositionX(),
                                                clockEngineLayer.getPositionY(), angle1 * ((direction == 1) ? 1 : -1) + clockEngineLayer.getAngle() + startoffset * ((direction == 1) ? 1 : -1));
                                    }
                                }
                                break;
                        }
                    }
                } catch (ConcurrentModificationException e) {
                    e.printStackTrace();
                    clockSkinLayers = null;
                    selectLastUsedSkin();
                }
            }
        }
}

    @Override
    public void run() {
        while (isRunning) {
            if (isAttachedToWindow()) {
                try {
                    TimeUnit.tick();
                    DataUnit.tick(context);
                    postInvalidateOnAnimation();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawBackgroundFrame(Canvas canvas, float renderScale, float renderLeft, float renderTop) {
        long now = SystemClock.uptimeMillis();
        if (renderStart == 0L) {
            renderStart = now;
        }
        long duration = backgroundRender.duration();
        if (duration == 0) {
            duration = 1000;
        }
        renderCurrentTime = (int) ((now - renderStart) % duration);
        backgroundRender.setTime(renderCurrentTime);
        canvas.save();
        canvas.scale(renderScale, renderScale);
        backgroundRender.draw(canvas, renderLeft, renderTop);
        canvas.restore();
    }


    public void startDraw() {
        isRunning = true;
        if (thread != null && thread.isAlive()) {
            return;
        }
        thread = new Thread(this);
        thread.start();
        renderStart = SystemClock.uptimeMillis() - renderCurrentTime;
    }

    public void stopDraw() {
        isRunning = false;
    }

    private void showSkin() {
        animationShow.setDuration(300);
        animationHide.setInterpolator(new OvershootInterpolator());
        animationShow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setScaleX((float) valueAnimator.getAnimatedValue("Alpha"));
                setScaleY((float) valueAnimator.getAnimatedValue("Alpha"));
                invalidate();
            }
        });
        animationShow.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (isHWAccelerated) {
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                } else {
                    setLayerType(View.LAYER_TYPE_HARDWARE, null);
                }

                startDraw();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                startDraw();
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        if (animationHide.isRunning()) {
            animationHide.cancel();
        }
        if (animationShow.isRunning()) {
            try {
                animationShow.cancel();
            } catch (AndroidRuntimeException e) {
                e.printStackTrace();
            }
        }
        animationShow.start();
    }

    public void hideSkin(Animator.AnimatorListener listener) {
        animationHide.setDuration(300);
        animationHide.setInterpolator(new OvershootInterpolator());
        animationHide.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setScaleX((float) valueAnimator.getAnimatedValue("Alpha"));
                setScaleY((float) valueAnimator.getAnimatedValue("Alpha"));
                invalidate();
            }
        });
        if (animationShow.isRunning()) {
            animationShow.cancel();
        }
        animationHide.start();
        animationHide.addListener(listener);
    }


    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }


    public void drawImage(Canvas canvas, List<?> images, int centerX, int centerY, int imageIndex) {
        Object image = images.get(imageIndex);
        if (image instanceof Drawable) {
            drawImage(canvas, images, centerX, centerY);
        } else if (image instanceof InputStream) {
            //TODO Draw gif
        }
    }

    public void drawImage(Canvas canvas, Object image, int centerX, int centerY) {
        if (image instanceof Drawable) {
            Drawable drawable = (Drawable) image;
            int drawableWidth = getItemWidth(image);
            int drawableHeight = getItemHeight(image);
            drawable.setBounds(centerX - drawableWidth / 2, centerY - drawableHeight / 2, centerX + drawableWidth / 2, centerY + drawableHeight / 2);
            drawable.draw(canvas);
        } else if (image instanceof InputStream) {
            if (renderStart == 0L) {
                renderStart = SystemClock.uptimeMillis();
            }
            long duration = ((backgroundRender.duration()) == 0) ? 1000 : backgroundRender.duration();
            renderCurrentTime = (int) ((SystemClock.uptimeMillis() - renderStart) % duration);
            backgroundRender.setTime(renderCurrentTime);
            canvas.save();
            canvas.scale(SCALE, SCALE);
            backgroundRender.draw(canvas, centerX, centerY);
            canvas.restore();
        }
    }

    private void drawText(Canvas canvas, int valueType, int textColor, int textSize, int positionX, int positionY) {
        switch (valueType) {
            case ClockEngineConstants.VALUE_TYPE_KCAL:
                drawText(canvas, new DecimalFormat("0.0").format(DataUnit.calories), textColor, textSize, positionX, positionY);
            case ClockEngineConstants.VALUE_TYPE_STEP:
                drawText(canvas, String.valueOf(DataUnit.steps), textColor, textSize, positionX, positionY);
            case ClockEngineConstants.VALUE_TYPE_BATTERY:
                drawText(canvas, String.valueOf(DataUnit.batteryLevel), textColor, textSize, positionX, positionY);
                break;
        }
    }

    private void drawText(Canvas canvas, String text, int textColor, int textSize, int positionX, int positionY) {
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        canvas.drawText(text, positionX - rect.width() / 2f, positionY + rect.height() / 2f, textPaint);
    }

    public void drawImageClipped(Canvas canvas, List<?> images, int centerX, int centerY, int valueType) {
        if (images.size() < 1)
            return;
        Object image = images.get(0);
        Object clipImage = images.get(1);
        drawImage(canvas, image, centerX, centerY);
        float clipRadius = Math.max(getItemHeight(image), getItemHeight(image)) / 2f;
        float clipAngle = 0;
        switch (valueType) {
            case ClockEngineConstants.VALUE_TYPE_KCAL:
                clipAngle = 360 * (float) DataUnit.calories / (float) DataUnit.targetCalories;
                break;
            case ClockEngineConstants.VALUE_TYPE_STEP:
                clipAngle = (float) 360 * (float) DataUnit.steps / (float) DataUnit.targetSteps;
                break;
            case ClockEngineConstants.VALUE_TYPE_BATTERY:
                clipAngle = (float) 360 * DataUnit.batteryLevel / 100;
                break;
            default:
                clipAngle = 0;
        }
        Path path = new Path();
        path.moveTo(centerX, centerY);
        path.lineTo(centerX, centerY - clipRadius);
        path.lineTo((float) (centerX + clipRadius * Math.sin(clipAngle * Math.PI / 180)), (float) (centerY - clipRadius * Math.cos(clipAngle * Math.PI / 180)));
        path.close();
        RectF rectF = new RectF(centerX - clipRadius, centerY - clipRadius, centerX + clipRadius, centerY + clipRadius);
        path.addArc(rectF, 270f, clipAngle);
        canvas.save();
        canvas.clipPath(path);
        drawImage(canvas, clipImage, centerX, centerY);
        canvas.restore();
        if (images.size() < 3)
            return;
        Object snapImage = images.get(2);
        canvas.save();
        canvas.rotate(clipAngle, centerX, centerY);
        drawImage(canvas, snapImage, centerX, centerY);
        canvas.restore();
    }

    public void drawRotateImage(Canvas canvas, Object background, int centerX, int centerY, float angle) {
        canvas.save();
        canvas.translate(0, 0);
        canvas.rotate(angle, centerX, centerY);
        drawImage(canvas, background, centerX, centerY);
        canvas.restore();
    }

    private int getItemWidth(Object item) {
        if (item instanceof Drawable) {
            Drawable drawable = (Drawable) item;
            return Math.round(drawable.getIntrinsicWidth() * SCALE);
        } else if (item instanceof Integer) {
            return Math.round(((Integer) item) * SCALE);
        } else if (item instanceof InputStream) {
            backgroundRender = Movie.decodeStream((InputStream) item);
            return (backgroundRender == null) ? 0 : Math.round(backgroundRender.width() * SCALE);
        } else {
            return 0;
        }
    }

    private int getItemHeight(Object item) {
        if (item instanceof Drawable) {
            Drawable drawable = (Drawable) item;
            return Math.round(drawable.getIntrinsicHeight() * SCALE);
        } else if (item instanceof Integer) {
            return Math.round(((Integer) item) * SCALE);
        } else if (item instanceof InputStream) {
            backgroundRender = Movie.decodeStream((InputStream) item);
            return (backgroundRender == null) ? 0 : Math.round(backgroundRender.height() * SCALE);
        } else {
            return 0;
        }
    }

    //CHINA DRAW
    public void drawSpecialSecond(Canvas canvas, String colorsInfo, int minute, int second) {
        int centerX = ClockEngineLayer.SCREEN_SIZE / 2;
        int highColor;
        int normalColor;
        Paint paint;
        if (colorsInfo.contains(",")) {
            highColor = 0xFF000000 | Integer.valueOf(colorsInfo.split(",")[0], 16);
            normalColor = 0xFF000000 | Integer.valueOf(colorsInfo.split(",")[1], 16);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(getItemWidth(10));
            paint.setStyle(Paint.Style.STROKE);
            paint.setAlpha(255);
            canvas.save();
            canvas.translate(centerX, centerX);
            float f = -centerX + 5;
            for (int i = 0; i < 60; i++) {
                if (minute % 2 == 0) {
                    paint.setColor((i < second) ? highColor : normalColor);
                } else {
                    paint.setColor((i < second) ? normalColor : highColor);
                }
                canvas.drawLine(getItemWidth(5), f, getItemWidth(5), f + getItemWidth(15), paint);
                canvas.rotate(6.0F, 0.0F, 0.0F);
            }
            canvas.restore();
        }
    }
}