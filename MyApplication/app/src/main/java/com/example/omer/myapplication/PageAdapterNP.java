package com.example.omer.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by omer on 5/17/2018.
 */

public class PageAdapterNP extends FragmentStatePagerAdapter {
    int mNoOfTabs;

    public PageAdapterNP(FragmentManager fm, int numberOfTabs){
        super(fm);
        this.mNoOfTabs = numberOfTabs;

    }
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                tabInfo tInfo = new tabInfo();
                return tInfo;

            case 1:
                FragmentHome tPosts = new FragmentHome();
                return tPosts;
            default:
                FragmentHome tInfo2 = new FragmentHome();
                return tInfo2;

        }

    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
