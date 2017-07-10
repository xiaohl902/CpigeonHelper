package com.cpigeon.cpigeonhelper.modular.xiehui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.common.db.AssociationData;
import com.cpigeon.cpigeonhelper.common.network.ApiResponse;
import com.cpigeon.cpigeonhelper.common.network.RetrofitHelper;
import com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.GridImageAdapter;
import com.cpigeon.cpigeonhelper.modular.root.bean.OrgNameApplyStatus;
import com.cpigeon.cpigeonhelper.ui.FullyGridLayoutManager;
import com.cpigeon.cpigeonhelper.utils.CommonUitls;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.cpigeon.cpigeonhelper.modular.geyuntong.adapter.GridImageAdapter.isAllowUpLoad;

/**
 * Created by Administrator on 2017/6/26.
 */

public class ChangeNameActivity extends ToolbarBaseActivity {

    @BindView(R.id.tv_xiehui_name)
    EditText tvXiehuiName;
    @BindView(R.id.tv_xiehui_reason)
    EditText tvXiehuiReason;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.btn_send)
    Button mButton;
    private long timestamp;
    private List<LocalMedia> list = new ArrayList<>();
    private int chooseMode = PictureMimeType.ofImage();//设置选择的模式
    private int compressMode = PictureConfig.LUBAN_COMPRESS_MODE;//选择压缩模式
    private File compressimg;
    private GridImageAdapter mAdapter;
    private int maxSelectNum = 1;

    @Override
    protected void swipeBack() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_changename;
    }

    @Override
    protected void setStatusBar() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("修改名称");
        setTopLeftButton(R.drawable.ic_back, this::finish);
        setTopRightButton("状态", this::showStatus);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(ChangeNameActivity.this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new GridImageAdapter(this, onAddPicClickListener);
        mAdapter.setList(list);
        mAdapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((position, v) -> {
            if (list.size() > 0) {
                LocalMedia media = list.get(position);
                String pictureType = media.getPictureType();
                // 预览图片 可自定长按保存路径
                //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                PictureSelector.create(ChangeNameActivity.this).externalPicturePreview(position, list);
            }
        });
        mButton.setOnClickListener(v -> save());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    list = PictureSelector.obtainMultipleResult(data);
                    mAdapter.setList(list);
                    mAdapter.notifyDataSetChanged();
                    compressimg = new File(list.get(0).getCompressPath());
                    break;
            }
        }
    }

    private void save() {
        timestamp = System.currentTimeMillis() / 1000;
        if (isAllowUpLoad && compressimg != null && !TextUtils.isEmpty(compressimg.getPath()) &&
                !TextUtils.isEmpty(tvXiehuiName.getText().toString().trim()) &&
                !TextUtils.isEmpty(tvXiehuiReason.getText().toString().trim())) {
            // 创建 RequestBody，用于封装构建RequestBody
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/jpeg"), compressimg);

            // MultipartBody.Part  和后端约定好Key，这里的partName是用image
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", compressimg.getName(), requestFile);

            Map<String, Object> postParams = new HashMap<>();
            postParams.put("uid", String.valueOf(AssociationData.getUserId()));
            postParams.put("type", AssociationData.getUserType());
            postParams.put("name", tvXiehuiName.getText().toString().trim());
            postParams.put("reason", tvXiehuiReason.getText().toString().trim());

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("uid", String.valueOf(AssociationData.getUserId()))
                    .addFormDataPart("type", AssociationData.getUserType())
                    .addFormDataPart("name", tvXiehuiName.getText().toString().trim())
                    .addFormDataPart("reason", tvXiehuiReason.getText().toString().trim())
                    .addPart(body)
                    .build();


            RetrofitHelper.getApi()
                    .submitOrgNameApply(AssociationData.getUserToken(),
                            requestBody,
                            timestamp,
                            CommonUitls.getApiSign(timestamp, postParams))
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(objectApiResponse -> {
                        if (objectApiResponse.getErrorCode() == 0) {
                            CommonUitls.showToast(this, "申请已提交");
                            this.finish();
                        } else {
                            CommonUitls.showToast(this, objectApiResponse.getMsg());
                        }
                    }, throwable -> {
                        if (throwable instanceof SocketTimeoutException) {
                            CommonUitls.showToast(this, "申请提交失败,连接超时");
                        } else if (throwable instanceof ConnectException) {
                            CommonUitls.showToast(this, "申请提交失败，请检查连接");
                        } else if (throwable instanceof RuntimeException) {
                            CommonUitls.showToast(this, "申请提交失败");
                        }
                    });
        } else {
            CommonUitls.showToast(this, "上传的东西为空，不能提交");
        }
    }


    @OnClick({R.id.tv_xiehui_name, R.id.tv_xiehui_reason})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_xiehui_name:
                tvXiehuiName.setCursorVisible(true);
                tvXiehuiName.setFocusable(true);
                tvXiehuiName.setFocusableInTouchMode(true);
                tvXiehuiName.requestFocus();
                break;
            case R.id.tv_xiehui_reason:
                tvXiehuiReason.setCursorVisible(true);
                tvXiehuiReason.setFocusable(true);
                tvXiehuiReason.setFocusableInTouchMode(true);
                tvXiehuiReason.requestFocus();
                break;
        }
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = () -> {
        // 进入相册 以下是例子：不需要的api可以不写
        PictureSelector.create(ChangeNameActivity.this)
                .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮
                .compress(true)// 是否压缩
                .compressMode(compressMode)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

    };

    private void showStatus(){
        Map<String,Object> urlParams= new HashMap<>();
        urlParams.put("uid",AssociationData.getUserId());
        urlParams.put("type","xiehui");

        RetrofitHelper.getApi()
                .getOrgNameApplyStatus(AssociationData.getUserToken(),urlParams)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orgNameApplyStatusApiResponse -> {
                    CommonUitls.showToast(this,orgNameApplyStatusApiResponse.getData().getStatus());
                }, throwable -> {
                    if (throwable instanceof SocketTimeoutException)
                    {
                        CommonUitls.showToast(this,"连接超时，请稍后再试");
                    }else if (throwable instanceof ConnectException)
                    {
                        CommonUitls.showToast(this,"连接失败，请检查连接");
                    }
                    else if (throwable instanceof RuntimeException){
                        CommonUitls.showToast(this,"发生不可预期的错误："+throwable.getMessage());
                    }
                });
    }
}
