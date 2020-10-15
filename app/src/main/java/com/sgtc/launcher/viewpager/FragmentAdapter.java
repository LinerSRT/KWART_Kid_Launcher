package com.sgtc.launcher.viewpager;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager fragmentManager, Context context, ArrayList<Class<? extends Fragment>> pages) {
        super(fragmentManager);
        mPagesClasses = pages;
        mContext = context;
    }

    private List<Class<? extends Fragment>> mPagesClasses;
    private Context mContext;

    @Override
    public Fragment getItem(int position) {
        return Fragment.instantiate(mContext, mPagesClasses.get(position).getName());
    }

    @Override
    public int getCount() {
        return mPagesClasses.size();
    }

}
