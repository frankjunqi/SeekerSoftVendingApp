package com.seekersoftvendingapp;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.Method;

/**
 * Created by kjh08490 on 2017/2/27.
 */

public class WebviewActivity extends BaseActivity {

    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        webView = (WebView) findViewById(R.id.web);
        WebSettings settings = webView.getSettings();
        // H5自动播放声音。setMediaPlaybackRequiresUserGesture方法API 17添加
        if (Build.VERSION.SDK_INT >= 17) {
            Class<?> clazz = settings.getClass();
            try {
                Method method = clazz.getDeclaredMethod("setMediaPlaybackRequiresUserGesture", boolean.class);
                method.invoke(settings, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= 16) {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }

        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);
        //WebView加载web资源
        webView.loadUrl("http://ad5.ctrmi.com/iadexcv/CVHandler.ashx?&aid=90857&wid=10037&RedirectUrl=http://m.dongfeng-honda-xr-v.com/&ns=[M_ADIP]&wv_viewport");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

}
