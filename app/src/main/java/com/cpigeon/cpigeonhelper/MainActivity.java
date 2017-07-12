package com.cpigeon.cpigeonhelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.cpigeon.cpigeonhelper.base.BaseActivity;
import com.cpigeon.cpigeonhelper.base.MyApp;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.db.RealmUtils;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.GeYunTongListActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.activity.MyGYTActivity;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.GeYunTongListAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.GYTService;
import com.cpigeon.cpigeonhelper.modular.home.bean.Ad;
import com.cpigeon.cpigeonhelper.modular.home.bean.HomeAd;
import com.cpigeon.cpigeonhelper.modular.order.activity.MyBalanceActivity;
import com.cpigeon.cpigeonhelper.modular.order.activity.OpeningGeyuntongActivity;
import com.cpigeon.cpigeonhelper.modular.order.activity.OrderListActivity;
import com.cpigeon.cpigeonhelper.modular.order.activity.ReChargeActivity;
import com.cpigeon.cpigeonhelper.modular.setting.activity.OperatorActivity;
import com.cpigeon.cpigeonhelper.modular.setting.activity.SettingActivity;
import com.cpigeon.cpigeonhelper.modular.xiehui.activity.XieHuiInfoActivity;
import com.cpigeon.cpigeonhelper.ui.MyDecoration;
import com.cpigeon.cpigeonhelper.ui.textview.MarqueeTextView;
import com.cpigeon.cpigeonhelper.utils.AppManager;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.PicassoImageLoader;
import com.cpigeon.cpigeonhelper.utils.ScreenTool;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, AMap.OnMyLocationChangeListener {

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
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mapView)
    MapView mMapView;
    @BindView(R.id.btn_enter_gyt)
    Button mButton;

    private GeYunTongListAdapter mAdapter;
    private int count = 1;
    private AMap aMap;

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
                .resizeDimen(R.dimen.image_width_headicon, R.dimen.image_height_headicon)
                .config(Bitmap.Config.RGB_565)
                .onlyScaleDown()
                .into(mUserIcon);
        mUserName.setText(AssociationData.getUserName());
        mUserSign.setText(AssociationData.getUserSign());
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navView.setNavigationItemSelectedListener(this);
        loadAd();
        mMapView.onCreate(savedInstanceState);
        loadRace();
        loadGTYServer();
        loadTopNews();
    }

    private void loadGTYServer() {
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("uid", AssociationData.getUserId());
        urlParams.put("type", AssociationData.getUserType());
        urlParams.put("atype", AssociationData.getUserAType());
        RetrofitHelper.getApi()
                .getGYTInfo(AssociationData.getUserToken(), urlParams)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gytServiceApiResponse -> {
                    if (gytServiceApiResponse.getErrorCode() == 0 && gytServiceApiResponse.getData()!= null) {
                        RealmUtils.getInstance().insertGYTService(gytServiceApiResponse.getData());
                    } else {
                        CommonUitls.showToast(this, "您未开通鸽运通");
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        CommonUitls.showToast(this, "连接超时，网络不太好");
                    } else if (throwable instanceof ConnectException) {
                        CommonUitls.showToast(this, "连接异常，网络不通畅");
                    } else if (throwable instanceof RuntimeException) {
                        CommonUitls.showToast(this, "发生了不可预期的错误" + throwable.getMessage());
                    }
                });
    }

    private void loadRace() {

        mMapView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("uid", AssociationData.getUserId());
        urlParams.put("type", AssociationData.getUserType());
        urlParams.put("ps", 3);
        urlParams.put("pi", 1);
        RetrofitHelper.getApi()
                .getGeYunTongRaceList(AssociationData.getUserToken(), urlParams)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listApiResponse -> {
                    if (listApiResponse.getErrorCode() == 0 && listApiResponse.getData() != null && listApiResponse.getData().size() > 0) {
                        mAdapter = new GeYunTongListAdapter(listApiResponse.getData());
                        mAdapter.setOnItemClickListener((adapter, view, position) ->
                                startActivity(new Intent(this, GeYunTongListActivity.class))
                        );
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        mRecyclerView.addItemDecoration(new MyDecoration(this, MyDecoration.VERTICAL_LIST));
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        loadMap();
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        CommonUitls.showToast(this, "连接超时");
                    } else if (throwable instanceof ConnectException) {
                        CommonUitls.showToast(this, "无法连接到服务器，请检查连接");
                    } else if (throwable instanceof RuntimeException) {
                        CommonUitls.showToast(this, "发生了不可预期的错误，错误信息:" + throwable.getMessage());
                    }
                });
    }


    private void loadMap() {
        mMapView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        aMap.setOnMyLocationChangeListener(this);
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
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        CommonUitls.showToast(this, "连接超时");
                    } else if (throwable instanceof ConnectException) {
                        CommonUitls.showToast(this, "无法连接到服务器，请检查连接");
                    } else if (throwable instanceof RuntimeException) {
                        CommonUitls.showToast(this, "发生了不可预期的错误，错误信息:" + throwable.getMessage());
                    }
                });
    }

    private void loadTopNews() {
        RetrofitHelper.getApi()
                .getAnnouncementTop()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(announcementListApiResponse -> {
                    if (announcementListApiResponse.isStatus()) {
                        listHeaderRaceDetialGg.setText(TextUtils.isEmpty(announcementListApiResponse.getData().getTitle()) ? "暂无任何消息" : announcementListApiResponse.getData().getTitle());
                    } else {
                        Logger.e("返回数据为空");
                    }
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        CommonUitls.showToast(this, "连接超时，请检查检查网络");
                    } else if (throwable instanceof ConnectException) {
                        CommonUitls.showToast(this, "连接失败，请检查连接");
                    } else if (throwable instanceof RuntimeException) {
                        CommonUitls.showToast(this, "连接失败");
                    }
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
            case R.id.xiehui_zone://协会空间
                startActivity(new Intent(MainActivity.this, XieHuiInfoActivity.class));
                break;
            case R.id.money://钱包
                startActivity(new Intent(MainActivity.this, MyBalanceActivity.class));
                break;
            case R.id.my_geyuntong://我的鸽运通
                if (RealmUtils.getInstance().existGYTInfo())
                {
                    GYTService gytService = RealmUtils.getInstance().queryGTYInfo().get(0);
                    if (!gytService.isIsExpired())
                    {
                        startActivity(new Intent(MainActivity.this, MyGYTActivity.class));
                    }else {
                        startActivity(new Intent(MainActivity.this, ReChargeActivity.class));
                    }

                }else {
                    startActivity(new Intent(MainActivity.this, OpeningGeyuntongActivity.class));
                }

                break;
            case R.id.my_order://我的订单
                startActivity(new Intent(MainActivity.this, OrderListActivity.class));
                break;
            case R.id.setting://设置
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.operator_log://日志查看
                startActivity(new Intent(MainActivity.this, OperatorActivity.class));
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMyLocationChange(Location location) {

    }

    @OnClick({R.id.list_header_race_detial_gg, R.id.btn_enter_gyt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.list_header_race_detial_gg:
                break;
            case R.id.btn_enter_gyt:
                startActivity(new Intent(this,GeYunTongListActivity.class));
                break;
        }
    }
}
