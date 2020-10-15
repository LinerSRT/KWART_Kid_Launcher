package com.sgtc.launcher.ClockSkin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ClockEnginePager extends ClockEngineRecyclerView{

	public final static int FLING_DIEABLE = -1;
	public static final int INVALUED_POSITION = -1;
	public static final float INVALUED_DOWN = -1.0F;
	private int flingVelocity = 1;
	private int initPosition = INVALUED_POSITION;
	
	private float mDownY = INVALUED_DOWN;
	
	private PageLayoutManager mPageLayoutManager;
	
	public ClockEnginePager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void initLayoutManager(int oration, boolean isResver){
		mPageLayoutManager = new PageLayoutManager(getContext(), oration, isResver);
		setLayoutManager(mPageLayoutManager);
	}
	
	public void setFlingVelocity(int scale){
		this.flingVelocity = scale;
	}
	
	public void setInitPosition(int position){
		this.initPosition = position;
	}

	public void scroollTo(int pos){
		scrollToPosition(pos);
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		super.onMeasure(widthSpec, heightSpec);
		if(INVALUED_POSITION != initPosition){
			scrollToPosition(initPosition);
			initPosition = INVALUED_POSITION;
		}
	}
	
	@Override
	public void setLayoutManager(RecyclerView.LayoutManager layout) {
		super.setLayoutManager(layout);
	}
	
	@Override
	public boolean fling(int velocityX, int velocityY) {
		if(FLING_DIEABLE == flingVelocity){
			return false;
		}
		if(flingVelocity <= 1){
			flingVelocity = 1;
		}
		velocityX /= flingVelocity;
		velocityY /= flingVelocity;
		return super.fling(velocityX, velocityY);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		final boolean result =  super.onTouchEvent(arg0);
		switch (arg0.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownY = arg0.getY();
				break;
			case MotionEvent.ACTION_UP:
				if(FLING_DIEABLE == flingVelocity){
					if(INVALUED_DOWN != mDownY){
						scrollerToNext(arg0.getY(), mDownY);
						mDownY = INVALUED_DOWN;
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if(INVALUED_DOWN == mDownY){
					mDownY = arg0.getY();
				}
			default:
				break;
		}
		return result;
	}
	
	private void scrollerToNext(float upY,float downY){
		if(mPageLayoutManager == null){
			return;
		}
		if(getChildCount() < 2){
			return;
		}
		final int centerY = getHeight()/2;
		if(upY > downY){
			final View currView = getChildAt(0);
			final int currOffset = getViewCenterY(currView) - centerY;
			smoothScrollBy(0, currOffset);
		}else{
			final View nextView = getChildAt(1);
			final int nextOffset = getViewCenterY(nextView) - centerY;
			smoothScrollBy(0, nextOffset);
		}
	}
	
	public void scrollerByPager() {
		if(getLayoutManager() == null){
			return;
		}
		if(getChildCount() < 2){
			return;
		}
		final View currView = getChildAt(0);
		final View nextView = getChildAt(1);
		if(getLayoutManager().canScrollHorizontally()){
			final int centerX = getWidth()/2;
			final int currOffset = getViewCenterX(currView) - centerX;
			final int nextOffset = getViewCenterX(nextView) - centerX;
			if(Math.abs(currOffset) < Math.abs(nextOffset)){
				smoothScrollBy(currOffset, 0);
			}else{
				smoothScrollBy(nextOffset, 0);
			}
		}else{
			final int centerY = getHeight()/2;
			final int currOffset = getViewCenterY(currView) - centerY;
			final int nextOffset = getViewCenterY(nextView) - centerY;
			if(Math.abs(currOffset) < Math.abs(nextOffset)){
				smoothScrollBy(0, currOffset);
			}else{
				smoothScrollBy(0, nextOffset);
			}
		}
	}

	
	private int getViewCenterX(View view){
		return (view.getRight() - view.getLeft())/2 + view.getLeft();
	}
	
	private int getViewCenterY(View view){
		return (view.getBottom() - view.getTop())/2 + view.getTop();
	}

	public class PageLayoutManager extends LinearLayoutManager {

		PageLayoutManager(Context context, int orientation,
						  boolean reverseLayout) {
			super(context, orientation, reverseLayout);
		}

		@Override
		public void onScrollStateChanged(int state) {
			super.onScrollStateChanged(state);
			if(SCROLL_STATE_IDLE == state){
				scrollerByPager();
			}
		}
		
		@Override
		public void scrollToPosition(int position) {
			if(getAdapter() == null || !(getAdapter() instanceof ClockSkinChooseAdapter)){
				super.scrollToPosition(position);
				return;
			}
			super.scrollToPosition(position);
		}
	}
}
