package com.cpigeon.cpigeonhelper.modular.geyuntong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.GridImageAdapter;
import com.cpigeon.cpigeonhelper.modular.geyuntong.bean.ImgTag;
import com.cpigeon.cpigeonhelper.ui.FullyGridLayoutManager;
import com.cpigeon.cpigeonhelper.ui.SaActionSheetDialog;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
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
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.shaohui.bottomdialog.BottomDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/6/15.
 */

public class UploadVideoActivity extends ToolbarBaseActivity {
    @BindView(R.id.tv_chose_Tag)
    TextView tvChoseTag;
    @BindView(R.id.ll_chose_tag)
    LinearLayout llChoseTag;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    private int chooseMode = PictureMimeType.ofVideo();
    private List<LocalMedia> selectList = new ArrayList<>();
    private SaActionSheetDialog mSaActionSheetDialog = null;
    private int tagid = 0;
    private long timestamp;
    private File video;
    private int rid;
    private GridImageAdapter mAdapter;
    private SweetAlertDialog mSweetAlertDialogLoading,mSweetAlertDialogSuccess;
    @Override
    protected void swipeBack() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_uploadvideo;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this,R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        rid = intent.getIntExtra("rid", 0);
        setTitle("上传视频");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("上传", this::upload);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(UploadVideoActivity.this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new GridImageAdapter(UploadVideoActivity.this, onAddPicClickListener);
        mAdapter.setList(selectList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((position, v) -> {
            if (selectList.size() > 0) {
                LocalMedia media = selectList.get(position);
                PictureSelector.create(UploadVideoActivity.this).externalPictureVideo(media.getPath());
            }
        });
    }

    @OnClick(R.id.ll_chose_tag)
    public void onViewClicked() {
        getTag();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    video = new File(selectList.get(0).getPath());
                    mAdapter.setList(selectList);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private void takeVideo(){
        PictureSelector.create(UploadVideoActivity.this)
                .openCamera(chooseMode)
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .previewVideo(true)
                .videoQuality(0)
                .videoSecond(7)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    private void upload() {
        if (tagid == 0 || "请选择".equals(tvChoseTag.getText().toString().trim())||video == null) {
            CommonUitls.showToast(this, "上传不能为空");
        } else {
            mSweetAlertDialogLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mSweetAlertDialogLoading.setTitleText("正在上传...");
            mSweetAlertDialogLoading.setCancelable(false);
            mSweetAlertDialogLoading.show();
//            BottomDialog.create(getSupportFragmentManager())
//                    .setViewListener(v -> {
//                        TextView tv = (TextView) v.findViewById(R.id.tv_geyuntong_weather);
//                        tv.setText("天气好热哦哈哈哈哈哈");
//                    })
//                    .setLayoutRes(R.layout.layout_watermarkinfo)
//                    .setDimAmount(0.1f)            // Dialog window dim amount(can change window background color）, range：0 to 1，default is : 0.2f
//                    .setCancelOutside(true)     // click the external area whether is closed, default is : true
//                    .setTag("BottomDialog")     // setting the DialogFragment tag
//                    .show();


            // 创建 RequestBody，用于封装构建RequestBody
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("video/mp4"), video);

            // MultipartBody.Part  和后端约定好Key，这里的partName是用image
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", video.getName(), requestFile);


            RequestBody mRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                    .addFormDataPart("rid", String.valueOf(rid))
                    .addFormDataPart("ft", "video")
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
            postParams.put("rid", String.valueOf(rid));
            postParams.put("ft", "video");
            postParams.put("tagid", String.valueOf(tagid));
            postParams.put("lo", String.valueOf(tagid));
            postParams.put("la", String.valueOf(tagid));
            postParams.put("file", video);
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
                            mSweetAlertDialogLoading.dismissWithAnimation();
                            mSweetAlertDialogSuccess = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                            mSweetAlertDialogSuccess.setTitleText("上传成功");
                            mSweetAlertDialogSuccess.setConfirmText("好的");
                            mSweetAlertDialogSuccess.setConfirmClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                finish();
                            });
                            mSweetAlertDialogSuccess.setCancelable(false);
                            mSweetAlertDialogSuccess.show();
                        }
                    }, throwable -> {
                        Logger.e("失败" + throwable.getMessage());
                    });

        }
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

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = this::takeVideo;
}
