package com.cpigeon.cpigeonhelper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpigeon.cpigeonhelper.base.BaseActivity;
import com.cpigeon.cpigeonhelper.base.MyApp;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.ApiResponse;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.ACarServiceActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.OpeningGeyuntongActivity;
import com.cpigeon.cpigeonhelper.modular.home.bean.Ad;
import com.cpigeon.cpigeonhelper.modular.home.bean.HomeAd;
import com.cpigeon.cpigeonhelper.modular.root.activity.RootListActivity;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.AnnouncementList;
import com.cpigeon.cpigeonhelper.modular.usercenter.bean.UserBean;
import com.cpigeon.cpigeonhelper.modular.xiehui.activity.XieHuiInfoActivity;
import com.cpigeon.cpigeonhelper.ui.textview.MarqueeTextView;
import com.cpigeon.cpigeonhelper.utils.AppManager;
import com.cpigeon.cpigeonhelper.utils.PicassoImageLoader;
import com.cpigeon.cpigeonhelper.utils.ScreenTool;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.home_banner)
    Banner mBanner;
    @BindView(R.id.list_header_race_detial_gg)
    MarqueeTextView listHeaderRaceDetialGg;
    @BindView(R.id.layout_gg)
    LinearLayout layoutGg;
    @BindView(R.id.ll_xiehui_introduce)
    LinearLayout llXiehuiIntroduce;
    @BindView(R.id.ll_xiehui_geyuntong)
    LinearLayout llXiehuiGeyuntong;

    private int count = 1;


    @Override
    protected void swipeBack() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        View headerView = navView.getHeaderView(0);
        CircleImageView mUserIcon = (CircleImageView) headerView.findViewById(R.id.iv_userheadimage);
        TextView mUserName = (TextView) headerView.findViewById(R.id.tv_userName);
        TextView mUserSign = (TextView) headerView.findViewById(R.id.tv_userSign);
        Picasso.with(MyApp.getInstance())
                .load(AssociationData.getUserImgUrl())
                .placeholder(R.mipmap.logos)
                .error(R.mipmap.logos)
                .into(mUserIcon);
        mUserName.setText(AssociationData.getUserName());
        mUserSign.setText(AssociationData.getUserSign());
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navView.setNavigationItemSelectedListener(this);
        Logger.e("当前用户类型" + AssociationData.getUsetType());
        loadAd();
        loadTopNews();
    }

    private void loadAd() {
        RetrofitHelper.getApi()
                .getAllAd()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listApiResponse -> {
                    if (listApiResponse.isStatus()) {
                        List<HomeAd> homeAds = new ArrayList<>();
                        for (Ad ad : listApiResponse.getData()) {
                            homeAds.add(new HomeAd(ad.getAdImageUrl(), ad.getAdUrl()));
                        }
                        showAd(homeAds);
                    }
                },throwable -> {
                    Logger.e("错误消息:"+throwable.getMessage());
                });
    }

    private void loadTopNews() {
        RetrofitHelper.getApi()
                .getAnnouncementTop()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(announcementListApiResponse -> {
                    if (announcementListApiResponse.isStatus())
                    {
                        listHeaderRaceDetialGg.setText(TextUtils.isEmpty(announcementListApiResponse.getData().getTitle())?"暂无任何消息":announcementListApiResponse.getData().getTitle());
                    }else {
                        Logger.e("返回数据为空");
                    }
                },throwable -> {
                    Logger.e("错误消息"+throwable.getMessage());
                });
    }

    private void showAd(List<HomeAd> homeAds) {

        ViewGroup.LayoutParams lp = mBanner.getLayoutParams();
        lp.height = ScreenTool.getScreenWidth(this) / 2;
        mBanner.setLayoutParams(lp);
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        mBanner.setImageLoader(new PicassoImageLoader());
        //设置图片集合
        mBanner.setImages(homeAds);
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(5000);
        //banner设置方法全部调用完毕时最后调用
        mBanner.start();

    }

    @Override
    public void initToolBar() {
        setSupportActionBar(toolbar);
    }


    /**
     * 如果按下返回键的时候Drawerlayout未关闭，那么就先关闭
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (count != 2) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();

            } else {
                AppManager.getAppManager().AppExit();
            }
            count = 2;
            Observable.timer(2, TimeUnit.SECONDS)
                    .compose(bindToLifecycle())
                    .subscribe(aLong -> count = 1);
        }

    }

    /**
     * 设置statusbar的颜色
     */
    @Override
    protected void setStatusBar() {

        StatusBarUtil.setColorForDrawerLayout(this, drawerLayout, mColor, 0);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.levelup_vip://升级会员
                startActivity(new Intent(MainActivity.this, OpeningGeyuntongActivity.class));
                break;
            case R.id.xiehui_zone://协会空间
                startActivity(new Intent(MainActivity.this, XieHuiInfoActivity.class));
                break;
            case R.id.money://钱包

                break;
            case R.id.my_geyuntong://我的鸽运通
                startActivity(new Intent(MainActivity.this, ACarServiceActivity.class));
                break;
            case R.id.sifangdi://常用司放地

                break;
            case R.id.my_authorization://账户授权
                startActivity(new Intent(MainActivity.this, RootListActivity.class));
                break;
            case R.id.my_zone://我的空间

                break;
            case R.id.my_order://我的订单

                break;
            case R.id.setting://设置

                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick({R.id.layout_gg, R.id.ll_xiehui_introduce,R.id.ll_xiehui_geyuntong})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_gg:
                break;
            case R.id.ll_xiehui_introduce:
                break;
            case R.id.ll_xiehui_geyuntong:
                startActivity(new Intent(MainActivity.this, OpeningGeyuntongActivity.class));
                break;
        }
    }


}