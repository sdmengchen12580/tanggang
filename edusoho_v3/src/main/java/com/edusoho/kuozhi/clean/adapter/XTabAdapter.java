package com.edusoho.kuozhi.clean.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class XTabAdapter extends FragmentPagerAdapter {

    //    private String[] title = {"one", "two", "three", "four"};
    private List<Fragment> fragmentList;

    public XTabAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    //重写
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return title[position];
//    }
}
