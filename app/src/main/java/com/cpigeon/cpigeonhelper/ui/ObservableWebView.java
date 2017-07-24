package com.cpigeon.cpigeonhelper.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by Administrator on 2017/7/19.
 */

public class ObservableWebView extends WebView {

    private OnScrollChangedListener onScrollChangedListener;


    public ObservableWebView(Context context) {
        super(context);
    }


    public ObservableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public ObservableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }


    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }


    public OnScrollChangedListener getOnScrollChangedListener() {
        return onScrollChangedListener;
    }


    public interface OnScrollChangedListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param v The view whose scroll position has changed.
         * @param x Current horizontal scroll origin.
         * @param y Current vertical scroll origin.
         * @param oldX Previous horizontal scroll origin.
         * @param oldY Previous vertical scroll origin.
         */
        void onScrollChanged(WebView v, int x, int y, int oldX, int oldY);
    }
}