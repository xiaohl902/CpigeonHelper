package com.cpigeon.cpigeonhelper.modular.geyuntong.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.CarPhotoFragment;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.CarServiceFragment;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.MyFlyingAreaFragment;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.SystemFlyingAreaFragment;

/**
 * 司放地的适配器，用于显示两个fragment
 * Created by Administrator on 2017/6/14.
 */

public class FlyingAreaPagerAdapter extends FragmentPagerAdapter {
    private final String[] TITLES;

    private Fragment[] fragments;


    public FlyingAreaPagerAdapter(FragmentManager fm, Context context) {

        super(fm);
        TITLES = context.getResources().getStringArray(R.array.flyingarea_sections);
        fragments = new Fragment[TITLES.length];
    }


    @Override
    public Fragment getItem(int position) {

        if (fragments[position] == null) {
            switch (position) {
                case 0:
                    fragments[position] = MyFlyingAreaFragment.newInstance();
                    break;
                case 1:
                    fragments[position] = SystemFlyingAreaFragment.newInstance();
                    break;
                default:
                    break;
            }
        }
        return fragments[position];
    }


    @Override
    public int getCount() {

        return TITLES.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        return TITLES[position];
    }
}
