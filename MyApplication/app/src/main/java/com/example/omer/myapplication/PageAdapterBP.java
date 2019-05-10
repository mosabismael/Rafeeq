package com.example.omer.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by omer on 5/17/2018.
 */

public class PageAdapterBP extends FragmentStatePagerAdapter {
    int mNoOfTabs;

    public PageAdapterBP(FragmentManager fm, int numberOfTabs){
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
                FragmentTabGallery tGallery = new FragmentTabGallery();
                return tGallery;

            case 2:
                FragmentHome tPosts = new FragmentHome();
                return tPosts;


            default:
                tabInfo tInfo2 = new tabInfo();
                return tInfo2;

        }

    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
