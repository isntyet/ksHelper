package com.ks.dblab.kshelper.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ks.dblab.kshelper.R;

/**
 * Created by jojo on 2016-06-04.
 */
public class WebviewActivity extends BaseActivity{
    private ProgressBar prgsBar = null;
    private WebView webView = null;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_webview);
        getSupportActionBar().setTitle(getIntent().getExtras().getString("name"));

        this.prgsBar = (ProgressBar) view.findViewById(R.id.prgs_bar);

        this.webView = (WebView) view.findViewById(R.id.web_view);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setBuiltInZoomControls(true);
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                WebviewActivity.this.prgsBar.setVisibility(View.VISIBLE);
            };

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                WebviewActivity.this.prgsBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        this.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                WebviewActivity.this.prgsBar.setProgress(newProgress);
            }
        });

        this.webView.loadUrl(getIntent().getExtras().getString("url"));
    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        }
    }
}

