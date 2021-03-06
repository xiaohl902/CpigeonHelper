package com.cpigeon.cpigeonhelper.modular.geyuntong.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.CarPhotoFragment;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.CarPlaybackFragment;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.CarServiceFragment;
import com.cpigeon.cpigeonhelper.modular.geyuntong.fragment.CarVideoFragment;

/**
 * Created by hcc on 16/8/4 14:12
 * 100332338@qq.com
 * <p/>
 * 主界面Fragment模块Adapter
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

  private final String[] TITLES;

  private Fragment[] fragments;

  private int loadtype;

  public HomePagerAdapter(FragmentManager fm, Context context,int loadtype) {
    super(fm);
    this.loadtype = loadtype;
    TITLES = context.getResources().getStringArray(R.array.sections);
    fragments = new Fragment[TITLES.length];
  }


  @Override
  public Fragment getItem(int position) {

    if (fragments[position] == null) {
      switch (position) {
        case 0:
          if (loadtype == 0||loadtype == 1)
          {
            fragments[position] = CarServiceFragment.newInstance();
          }else {
            fragments[position] = CarPlaybackFragment.newInstance();
          }

          break;
        case 1:
          fragments[position] = CarPhotoFragment.newInstance();
          break;
        case 2:
          fragments[position] = CarVideoFragment.newInstance();
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
