package com.cpigeon.cpigeonhelper.modular.geyuntong.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.ImgTag;
import com.cpigeon.cpigeonhelper.ui.SaActionSheetDialog;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.shaohui.bottomdialog.BottomDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/6/15.
 */

public class UploadImgActivity extends ToolbarBaseActivity {
    @BindView(R.id.tv_chose_Tag)
    TextView tvChoseTag;
    @BindView(R.id.ll_chose_tag)
    LinearLayout llChoseTag;
    @BindView(R.id.iv_compressuploadimg)
    ImageView ivCompressuploadimg;
    private List<LocalMedia> list = new ArrayList<>();
    private int chooseMode = PictureMimeType.ofImage();//设置选择的模式
    private int compressMode = PictureConfig.LUBAN_COMPRESS_MODE;//选择压缩模式
    private int themeId;
    private File compressimg;
    private SaActionSheetDialog mSaActionSheetDialog = null;
    private int tagid = 0;
    private long timestamp;

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
        setTitle("上传图片");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("上传", this::upload);
        themeId = R.style.picture_default_style;
        PictureSelector.create(UploadImgActivity.this)
                .openCamera(chooseMode)
                .theme(themeId)
                .previewImage(true)
                .compressGrade(Luban.THIRD_GEAR)
                .compress(true)
                .compressMode(compressMode)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    public void upload() {
        if (tagid == 0 || "请选择".equals(tvChoseTag.getText().toString().trim()))
        {
            CommonUitls.showToast(this,"请选择上传类型");
        }else if (compressimg == null){
            CommonUitls.showToast(this,"请上传图片哦");
        } else {
            BottomDialog.create(getSupportFragmentManager())
                    .setViewListener(v -> {
                        TextView tv = (TextView) v.findViewById(R.id.tv_geyuntong_weather);
                        tv.setText("天气好热哦哈哈哈哈哈");
                    })
                    .setLayoutRes(R.layout.layout_watermarkinfo)
                    .setDimAmount(0.1f)            // Dialog window dim amount(can change window background color）, range：0 to 1，default is : 0.2f
                    .setCancelOutside(true)     // click the external area whether is closed, default is : true
                    .setTag("BottomDialog")     // setting the DialogFragment tag
                    .show();


            // 创建 RequestBody，用于封装构建RequestBody
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/jpeg"), compressimg);

            // MultipartBody.Part  和后端约定好Key，这里的partName是用image
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", compressimg.getName(), requestFile);


            RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                    .addFormDataPart("rid", String.valueOf(8))
                    .addFormDataPart("ft", "image")
                    .addFormDataPart("faid", String.valueOf(5))
                    .addFormDataPart("tagid", String.valueOf(tagid))
                    .addFormDataPart("lo", String.valueOf(tagid))
                    .addFormDataPart("la", String.valueOf(tagid))
                    .addFormDataPart("we", "天气真的很棒")
                    .addFormDataPart("t", "天气真的很棒")
                    .addFormDataPart("wp", "天气真的很棒")
                    .addFormDataPart("wd", "天气真的很棒")
                    .addPart(body)
                    .build();
            Map<String, Object> postParams = new HashMap<>();
            postParams.put("uid", String.valueOf(AssociationData.getUserId()));
            postParams.put("rid", String.valueOf(8));
            postParams.put("ft", "image");
            postParams.put("faid", String.valueOf(5));
            postParams.put("tagid", String.valueOf(tagid));
            postParams.put("lo", String.valueOf(tagid));
            postParams.put("la", String.valueOf(tagid));
            postParams.put("file", compressimg);
            postParams.put("we", "天气真的很棒");
            postParams.put("t", "天气真的很棒");
            postParams.put("wp", "天气真的很棒");
            postParams.put("wd", "天气真的很棒");


            timestamp = System.currentTimeMillis() / 1000;
            RetrofitHelper.getApi().raceImageOrVideo(AssociationData.getUserToken(),
                    mRequestBody, timestamp, CommonUitls.getApiSign(timestamp, postParams))
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(objectApiResponse -> {
                        if (objectApiResponse.getErrorCode() == 0) {
                            new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("成功")
                                    .setContentText("上传成功")
                                    .setConfirmText("好的")
                                    .show();
                        }
                    }, throwable -> {
                        Logger.e("失败" + throwable.getMessage());
                    });

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
                    compressimg = new File(list.get(0).getCompressPath());
                    Drawable drawable = ContextCompat.getDrawable(this, R.drawable.watermark);
                    BitmapDrawable bd = (BitmapDrawable) drawable;
                    Bitmap compressbitmap = BitmapFactory.decodeFile(compressimg.getPath());
                    Bitmap bmp = bd.getBitmap();


                    Bitmap watermark = createWaterMaskImage(this, compressbitmap, bmp);


                    ivCompressuploadimg.setImageBitmap(watermark);

                    Logger.e("压缩后路径是:" + compressimg.getPath());
                    Logger.e("压缩后大小是:" + compressimg.length() / 1024 + "k");
                    Logger.e("压缩分辨率是:" + computeSize(compressimg)[0] + "*" + computeSize(compressimg)[1]);
                    break;
            }
        }
    }

    private int[] computeSize(File srcImg) {
        int[] size = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeFile(srcImg.getAbsolutePath(), options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;

        return size;
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
                        mSaActionSheetDialog.setCanceledOnTouchOutside(false);
                    }
                    mSaActionSheetDialog.show();

                }, throwable -> {
                    Logger.e("错误消息:" + throwable.getMessage());
                });
    }

    @OnClick({R.id.ll_chose_tag, R.id.iv_compressuploadimg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_chose_tag:
                getTag();
                break;
            case R.id.iv_compressuploadimg:
                getTag();
                break;
        }
    }
}
