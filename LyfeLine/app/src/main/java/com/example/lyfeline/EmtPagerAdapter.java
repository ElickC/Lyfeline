package com.example.lyfeline;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class EmtPagerAdapter extends FragmentPagerAdapter {
    int numOfTabs;
    public EmtPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new EmtHomeFragment();
            case 1:
                return new EmtMapFragment();
            case 2:
                return new EmtHelpFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
