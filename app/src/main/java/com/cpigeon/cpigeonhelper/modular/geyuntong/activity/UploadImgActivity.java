package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.GridImageAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.ImgTag;
import com.cpigeon.cpigeonhelper.ui.FullyGridLayoutManager;
import com.cpigeon.cpigeonhelper.ui.SaActionSheetDialog;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 *
 * Created by Administrator on 2017/6/15.
 *
 */

public class UploadImgActivity extends ToolbarBaseActivity implements AMapLocationListener, WeatherSearch.OnWeatherSearchListener {


    @BindView(R.id.tv_chose_Tag)
    TextView tvChoseTag;
    @BindView(R.id.ll_chose_tag)
    LinearLayout llChoseTag;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private List<LocalMedia> list = new ArrayList<>();
    private int chooseMode = PictureMimeType.ofImage();//设置选择的模式
    private File compressimg;
    private SaActionSheetDialog mSaActionSheetDialog = null;
    private SweetAlertDialog mSweetAlertDialogLoading, mSweetAlertDialogSuccess;
    private int tagid = 0;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private double latitude, longitude;
    private AMapLocation aMapLocation;
    private int rid;
    private GridImageAdapter mAdapter;
    private static final String SD_PATH = "/sdcard/dskqxt/pic/";
    private static final String IN_PATH = "/dskqxt/pic/";

    @Override
    protected void swipeBack() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_uploadimg;
    }

    @Override
    protected void setStatusBar() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        rid = intent.getIntExtra("rid", 0);
        setTitle("上传图片");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("上传", this::upload);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(UploadImgActivity.this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new GridImageAdapter(this, onAddPicClickListener);
        mAdapter.setList(list);
        mAdapter.setSelectMax(1);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((position, v) -> {
            if (list.size() > 0) {
                PictureSelector.create(UploadImgActivity.this).externalPicturePreview(position, list);
            }
        });

    }

    public void takePhoto() {
        int compressMode = PictureConfig.LUBAN_COMPRESS_MODE;
        PictureSelector.create(UploadImgActivity.this)
                .openCamera(chooseMode)
                .maxSelectNum(1)// 最大图片选择数量
                .previewImage(true)
                .compressGrade(Luban.THIRD_GEAR)
                .compress(true)
                .compressMode(compressMode)
                .minSelectNum(1)// 最小选择数量
                .isCamera(true)// 是否显示拍照按钮
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    public void upload() {
        if (tagid == 0 || "请选择".equals(tvChoseTag.getText().toString().trim())) {
            CommonUitls.showToast(this, "请选择上传类型");
        } else if (compressimg == null) {
            CommonUitls.showToast(this, "请上传图片哦");
        } else {
            if (mLocationClient == null) {
                mLocationClient = new AMapLocationClient(this);
                mLocationOption = new AMapLocationClientOption();
                mLocationOption.setOnceLocation(true);
                // 设置定位监听
                mLocationClient.setLocationListener(this);
                // 设置为高精度定位模式
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                //设置是否返回地址信息（默认返回地址信息）
                mLocationOption.setNeedAddress(true);
                //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
                mLocationOption.setHttpTimeOut(20000);

                mLocationClient.setLocationOption(mLocationOption);

                mLocationClient.startLocation();

            }
            mSweetAlertDialogLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mSweetAlertDialogLoading.setTitleText("正在上传...");
            mSweetAlertDialogLoading.setCancelable(false);
            mSweetAlertDialogLoading.show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    list = PictureSelector.obtainMultipleResult(data);
                    Drawable drawable = ContextCompat.getDrawable(this, R.drawable.watermark);
                    BitmapDrawable bd = (BitmapDrawable) drawable;
                    Bitmap compressbitmap = BitmapFactory.decodeFile(new File(list.get(0).getCompressPath()).getPath());
                    Bitmap bmp = bd.getBitmap();
                    Bitmap watermark = createWaterMaskImage(this, compressbitmap, bmp);
                    if (saveBitmap(this, watermark) != null) {
                        compressimg = new File(saveBitmap(this, watermark));
                    } else {
                        compressimg = new File(list.get(0).getCompressPath());
                    }

                    mAdapter.setList(list);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    private Bitmap createWaterMaskImage(Context context, Bitmap src, Bitmap watermark) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        // create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        // draw src into
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        // draw watermark into
        cv.drawBitmap(watermark, w / 2, h / 2, null);// 在src的右下角画入水印
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return newb;
    }

    private void getTag() {
        RetrofitHelper.getApi().getTag("gyt")
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imgTagApiResponse -> {
                    if (mSaActionSheetDialog == null) {
                        mSaActionSheetDialog = new SaActionSheetDialog(this).builder();
                        for (ImgTag imgTag : imgTagApiResponse.getData()) {
                            mSaActionSheetDialog.addSheetItem(imgTag.getName(), which -> {
                                tagid = imgTag.getTid();
                                tvChoseTag.setText(imgTag.getName());
                            });
                        }
                        mSaActionSheetDialog.setCanceledOnTouchOutside(true);
                    }
                    mSaActionSheetDialog.show();

                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        CommonUitls.showToast(this, "获取数据超时");
                    } else if (throwable instanceof ConnectException) {
                        CommonUitls.showToast(this, "无法连接到服务器，请检查网络连接");
                    }
                });
    }

    @OnClick({R.id.ll_chose_tag})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_chose_tag:
                getTag();
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                this.aMapLocation = aMapLocation;
                latitude = aMapLocation.getLatitude();
                longitude = aMapLocation.getLongitude();
                WeatherSearchQuery mquery = new WeatherSearchQuery(aMapLocation.getCity(), WeatherSearchQuery.WEATHER_TYPE_LIVE);
                WeatherSearch mweathersearch = new WeatherSearch(this);
                mweathersearch.setOnWeatherSearchListener(this);
                mweathersearch.setQuery(mquery);
                mweathersearch.searchWeatherAsyn();//异步搜索
            } else {
                mSweetAlertDialogLoading.dismissWithAnimation();
                mLocationClient = null;
                CommonUitls.showToast(this, "定位失败" + aMapLocation.getErrorCode() + ":" + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == 1000) {

            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                Logger.e("这句话执行了");
                LocalWeatherLive mLocalWeatherLive = weatherLiveResult.getLiveResult();

                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("image/jpeg"), compressimg);

                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", compressimg.getName(), requestFile);

                RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                        .addFormDataPart("rid", String.valueOf(rid))
                        .addFormDataPart("ft", "image")
                        .addFormDataPart("tagid", String.valueOf(tagid))
                        .addFormDataPart("lo", CommonUitls.GPS2AjLocation(longitude))
                        .addFormDataPart("la", CommonUitls.GPS2AjLocation(latitude))
                        .addFormDataPart("we", mLocalWeatherLive.getWeather())
                        .addFormDataPart("t", mLocalWeatherLive.getTemperature())
                        .addFormDataPart("wp", mLocalWeatherLive.getWindPower())
                        .addFormDataPart("wd", mLocalWeatherLive.getWindDirection())
                        .addPart(body)
                        .build();

                Map<String, Object> postParams = new HashMap<>();
                postParams.put("uid", String.valueOf(AssociationData.getUserId()));
                postParams.put("rid", String.valueOf(rid));
                postParams.put("ft", "image");
                postParams.put("tagid", String.valueOf(tagid));
                postParams.put("lo", CommonUitls.GPS2AjLocation(longitude));
                postParams.put("la", CommonUitls.GPS2AjLocation(latitude));
                postParams.put("file", compressimg);
                postParams.put("we", mLocalWeatherLive.getWeather());
                postParams.put("t", mLocalWeatherLive.getTemperature());
                postParams.put("wp", mLocalWeatherLive.getWindPower());
                postParams.put("wd", mLocalWeatherLive.getWindDirection());

                long timestamp = System.currentTimeMillis() / 1000;
                RetrofitHelper.getApi().raceImageOrVideo(AssociationData.getUserToken(),
                        mRequestBody, timestamp, CommonUitls.getApiSign(timestamp, postParams))
                        .compose(bindToLifecycle())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(objectApiResponse -> {
                            mSweetAlertDialogLoading.dismissWithAnimation();
                            if (objectApiResponse.getErrorCode() == 0) {
                                mLocationClient = null;
                                if ("司放瞬间".equals(tvChoseTag.getText().toString())) {

                                    mSweetAlertDialogSuccess = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                                    mSweetAlertDialogSuccess.setTitleText("上传成功");
                                    mSweetAlertDialogSuccess.setContentText("是否将立即结束监控？");
                                    mSweetAlertDialogSuccess.setConfirmText("好的");
                                    mSweetAlertDialogSuccess.setConfirmClickListener(sweetAlertDialog -> {
                                        sweetAlertDialog.dismiss();
                                        finish();
                                    });
                                    mSweetAlertDialogSuccess.setCancelText("不用了");
                                    mSweetAlertDialogSuccess.setCancelClickListener(sweetAlertDialog -> {
                                        sweetAlertDialog.dismiss();
                                        finish();
                                    });
                                    mSweetAlertDialogSuccess.setCancelable(false);
                                    mSweetAlertDialogSuccess.show();
                                } else {
                                    mSweetAlertDialogSuccess = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                                    mSweetAlertDialogSuccess.setTitleText("上传成功");
                                    mSweetAlertDialogSuccess.setConfirmText("好的");
                                    mSweetAlertDialogSuccess.setConfirmClickListener(sweetAlertDialog -> {
                                        sweetAlertDialog.dismiss();
                                        finish();
                                    });
                                    mSweetAlertDialogSuccess.setCancelable(false);
                                    mSweetAlertDialogSuccess.show();
                                }
                            } else {
                                mLocationClient = null;
                                CommonUitls.showToast(this, objectApiResponse.getMsg());
                            }
                        }, throwable -> {
                            mSweetAlertDialogSuccess.dismissWithAnimation();
                            mLocationClient = null;
                        });

            } else {
                mLocationClient = null;
                mSweetAlertDialogLoading.dismissWithAnimation();
                CommonUitls.showToast(this, "无结果");
            }
        } else {
            mLocationClient = null;
            mSweetAlertDialogLoading.dismissWithAnimation();
            CommonUitls.showToast(this, "发生错误，错误代码" + rCode);
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = this::takePhoto;

}
