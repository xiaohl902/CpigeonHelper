package com.cpigeon.cpigeonhelper.modular.setting.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.cpigeon.cpigeonhelper.R;
import com.cpigeon.cpigeonhelper.base.ToolbarBaseActivity;
import com.cpigeon.cpigeonhelper.ui.ObservableWebView;
import com.cpigeon.cpigeonhelper.utils.StatusBarUtil;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.r0adkll.slidr.Slidr;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 *
 * Created by Administrator on 2017/7/19.
 *
 */

public class WebViewActivity extends ToolbarBaseActivity implements ObservableWebView.OnScrollChangedListener {

    String url, title;
    private static final Map<String, Integer> URL_POSITION_CACHES = new HashMap<>();
    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_TITLE = "extra_title";
    boolean overrideTitleEnabled = true;
    int positionHolder;

    @BindView(R.id.web_view)
    ObservableWebView webview;
    @BindView(R.id.progressbar)
    NumberProgressBar progressbar;

    @Override
    protected void swipeBack() {
        Slidr.attach(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.layout_webview;
    }

    @Override
    protected void setStatusBar() {
        mColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForSwipeBack(this, mColor, 0);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("帮助");
        setTopLeftButton(R.drawable.ic_back,this::finish);
        url = getIntent().getStringExtra(EXTRA_URL);
        title = getIntent().getStringExtra(EXTRA_TITLE);
        //声明WebSettings子类
        WebSettings settings = webview.getSettings();
        //与Javascript交互
        settings.setJavaScriptEnabled(true);
        ////设置自适应屏幕，两者合用
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setAppCacheEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);//支持缩放
        settings.setDomStorageEnabled(true);
        webview.setWebChromeClient(new ChromeClient());
        webview.setOnScrollChangedListener(this);
        webview.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        final String url = webview.getUrl();
        int bottom = (int) Math.floor(webview.getContentHeight() * webview.getScale() * 0.8f);
        if (positionHolder >= bottom) {
            URL_POSITION_CACHES.remove(url);
        } else {
            URL_POSITION_CACHES.put(url, positionHolder);
        }
        super.onDestroy();
        if (webview != null) webview.destroy();
    }

    @Override
    public void onScrollChanged(WebView v, int x, int y, int oldX, int oldY) {
        positionHolder = y;
    }

    private class ChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressbar.setProgress(newProgress);
            if (newProgress == 100) {
                progressbar.setVisibility(View.INVISIBLE);
            } else {
                progressbar.setVisibility(View.VISIBLE);
            }
        }


        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (overrideTitleEnabled) {
                setTitle(title);
            }
        }
    }


}
