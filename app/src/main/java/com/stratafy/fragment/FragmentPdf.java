package com.stratafy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.stratafy.R;



public class FragmentPdf extends DialogFragment {

    private String TAG = FragmentPdf.class.getSimpleName();
    String file;

    public static FragmentPdf newInstance() {
        FragmentPdf fragmentFullScreen = new FragmentPdf();
        return fragmentFullScreen;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.MyMaterialThemeFull);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_pdf, container, false);
        v.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
        file = getArguments().getString("file");

        Log.d("FIle:", file);
        WebView webView = (WebView) v.findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+file);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
            Log.d("OnPause called", "YES");
            dismiss();
    }
}
