package trainedge.pod_droid.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import trainedge.pod_droid.Model.Songs;
import trainedge.pod_droid.tools.Constants;



public class FragmentDetail extends BaseFragment {


    String playingError;
    Songs song;
    TextView tvChapterTitle;
    ImageView ivChapterLogo;
    WebView wvChapterVideo;

    void init() {
        initLogicalComponents();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wvChapterVideo.loadUrl("about:blank");
        wvChapterVideo.destroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        wvChapterVideo.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        wvChapterVideo.onPause();
    }


    private void initLogicalComponents() {
        Bundle args = getArguments();
        if (args != null)
            song = new Gson().fromJson(args.getString(Constants.KEY_SONG), Songs.class);
        Picasso.with(getActivity()).load(song.getImage().getLink()).fit().into(ivChapterLogo);
        tvChapterTitle.setText(song.getTitle());
        configWebView();
        try {
            if (song.getUrl().getUrl() != null) {
                wvChapterVideo.loadUrl(song.getUrl().getUrl());
            }
        } catch (RuntimeException e) {
            toast(playingError);
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configWebView() {
        wvChapterVideo.getSettings().setJavaScriptEnabled(true);
        wvChapterVideo.clearHistory();
        wvChapterVideo.clearCache(true);
        wvChapterVideo.getSettings().setSupportZoom(true);
        wvChapterVideo.getSettings().setBuiltInZoomControls(true);
        wvChapterVideo.getSettings().setDisplayZoomControls(false);
        wvChapterVideo.setWebChromeClient(new WebChromeClient());
        wvChapterVideo.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
                try {
                    showLoading(true);
                    wvChapterVideo.setEnabled(false);
                } catch (Exception e) {
                    leh("Problem loading new web page");
                }

                //TRy catching exception
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                try {
                    showLoading(false);
                    wvChapterVideo.setEnabled(true);
                } catch (Exception e) {
                    leh("Problem when finishing loading web page");
                }

            }
        });
    }
}
