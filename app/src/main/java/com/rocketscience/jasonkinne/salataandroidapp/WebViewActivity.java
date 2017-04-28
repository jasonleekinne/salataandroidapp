package com.kcc.huggiesrewards.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.kcc.huggiesrewards.R;

/**
 * A generic fragment that shows given URL on WebView.
 */
public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = WebViewActivity.class.getSimpleName();

    private static final String ARG_URL = "argUrl";
    private static final String ARG_ENABLE_JAVASCRIPT = "argEnableJavaScript";

    private WebView mWebView;
    private String mUrl;
    private UrlChangeListener mUrlChangeListener;
    private ProgressBar mSpinner;
    private boolean mEnableJavaScript;

    public static BaseWebViewFragment newInstance(String url, boolean enableJavaScript) {
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        args.putBoolean(ARG_ENABLE_JAVASCRIPT, enableJavaScript);
        BaseWebViewFragment fragment = new BaseWebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString(ARG_URL);
        mEnableJavaScript = getArguments().getBoolean(ARG_ENABLE_JAVASCRIPT);
        Log.d(TAG, "url: " + mUrl);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UrlChangeListener) {
            mUrlChangeListener = (UrlChangeListener) context;
        } else {
            throw new ClassCastException("Calling Activity does not implement UrlChangeListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        mWebView = (WebView) view.findViewById(R.id.webView);
        mSpinner = (ProgressBar) view.findViewById(R.id.progressBar);
        initializeWebView();

        return view;
    }

    private void initializeWebView() {
        if (mWebView != null) {
            WebSettings settings = mWebView.getSettings();
            settings.setJavaScriptEnabled(mEnableJavaScript);

            settings.setSupportZoom(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);

            mWebView.setWebViewClient(mWebViewClient);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mWebView != null) {
            Log.d(TAG, "[onStart] load url: " + mUrl);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl(mUrl);
                }
            });
        }
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            showSpinner(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mUrlChangeListener != null) {
                mUrlChangeListener.onPageLoaded(url);
            }
            showSpinner(false);
        }
    };

    private void showSpinner(boolean show) {
        if (mSpinner != null) {
            mSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public interface UrlChangeListener {
        void onPageLoaded(String url);
    }
}
