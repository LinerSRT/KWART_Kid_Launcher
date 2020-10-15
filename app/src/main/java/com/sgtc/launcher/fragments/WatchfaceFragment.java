package com.sgtc.launcher.fragments;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sgtc.launcher.ClockSkin.ClockEngineChooseSkin;
import com.sgtc.launcher.ClockSkin.View.Clock;
import com.sgtc.launcher.LauncherApplication;
import com.sgtc.launcher.R;
import com.sgtc.launcher.util.Broadcast;

import java.util.Objects;

public class WatchfaceFragment extends Fragment {
    private Clock clock;
    private Broadcast broadcast;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.watchface_fragment, container, false);
        clock = view.findViewById(R.id.clock);
        clock.selectLastUsedSkin();
        broadcast = new Broadcast(getContext(), new String[]{
                LauncherApplication.getContext().getPackageName() + ".SELECT_LAST_USED_WATCHFACE",
                LauncherApplication.getContext().getPackageName() + ".WATCHFACE_SELECTED"

        }) {
            @Override
            public void handleChanged(Intent intent) {
                clock.selectLastUsedSkin();
            }
        };
        broadcast.setListening(true);
        clock.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    clock.onTouch((int) e.getX(), (int) e.getY());
                    return super.onSingleTapUp(e);
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    long[] pattern = {0, 50};
                    Vibrator vibrator = (Vibrator) Objects.requireNonNull(getContext()).getSystemService(Context.VIBRATOR_SERVICE);
                    assert vibrator != null;
                    vibrator.vibrate(pattern, -1);
                    clock.hideSkin(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Intent intent = new Intent(getContext(), ClockEngineChooseSkin.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Objects.requireNonNull(getContext()).startActivity(intent);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    super.onLongPress(e);
                }
            });

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcast.setListening(false);
    }
}
