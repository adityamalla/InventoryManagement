package com.safetystratus.inventorymanagement;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAdapterLocateTags extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public TabAdapterLocateTags(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                SingleLocateTag singleLocateTagFragment = new SingleLocateTag();
                return singleLocateTagFragment;
            case 1:
                MultiTagLocateTag multiTagLocateTag = new MultiTagLocateTag();
                return multiTagLocateTag;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }

}