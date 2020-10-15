package com.sgtc.launcher.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sgtc.launcher.R;


public class Scrollbar extends View {
    private Paint backgroundPaint;
    private Paint highlightPaint;
    private int totalScrolled = 0;
    private RectF backgroundRect;
    private RectF scrollerRect;
    private int strokeWidth = 8;
    private int strokeSize = 32;
    private float scrollPercent = 0;
    private float scrollerRange;
    private float scrollerPosition;

    private float totalScrollHeight = 0;


    public Scrollbar(Context context) {
        super(context);
    }

    public Scrollbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(RecyclerView recyclerView) {
        backgroundRect = new RectF();
        scrollerRect = new RectF();
        backgroundPaint = new Paint();
        backgroundPaint.setColor(ContextCompat.getColor(recyclerView.getContext(), R.color.recycler_item_color));
        backgroundPaint.setStrokeWidth(8);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setDither(true);
        backgroundPaint.setAntiAlias(true);
        highlightPaint = new Paint();
        highlightPaint.setColor(ContextCompat.getColor(recyclerView.getContext(), R.color.accent));
        highlightPaint.setStrokeWidth(6);
        highlightPaint.setStyle(Paint.Style.FILL);
        highlightPaint.setAntiAlias(true);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalScrolled += dy;
                if(totalScrollHeight == 0){
                    View view = recyclerView.getChildAt(0);
                    totalScrollHeight = (getItemHeight(view) * recyclerView.getAdapter().getItemCount());
                }
                scrollScroller(totalScrolled, totalScrollHeight);
            }
        });
    }

    private float getItemHeight(View view) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        return view.getHeight() - layoutParams.topMargin;
    }

    private void scrollScroller(float totalScrolled, float scrollRange) {
        if (totalScrolled != 0) {
            scrollPercent = (totalScrolled / scrollRange) * 100;
            scrollerPosition = scrollerRange * (scrollPercent / 100);
            if (scrollerPosition + (strokeSize / 2f) <= scrollerRange - (strokeSize / 2f)) {
                invalidate();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(backgroundRect, 4, 4, backgroundPaint);
        scrollerRect.set(getWidth() - strokeWidth * 2, scrollerPosition + (strokeSize / 2f), getWidth() - strokeWidth, scrollerPosition + (strokeSize / 2f) + strokeSize);
        canvas.drawRoundRect(scrollerRect, 4, 4, highlightPaint);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        scrollerRange = ((getHeight() - strokeWidth * 2) - (strokeWidth * 2));
        backgroundRect.set(getWidth() - strokeWidth * 2, strokeWidth * 2, getWidth() - strokeWidth, getHeight() - strokeWidth * 2);
        scrollerRect.set(getWidth() - strokeWidth * 2, strokeWidth * 2, getWidth() - strokeWidth, getHeight() - strokeWidth * 2);
    }
}
