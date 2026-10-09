package com.taskmaster.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true); // localStorage = your saved tasks and ticks
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new Bridge(), "TM");
        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");

        Reminders.createChannel(this);
        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Reminders.scheduleAll(this); // also picks up a newly granted exact-alarm permission
    }

    class Bridge {
        @JavascriptInterface
        public void testNotify() { Reminders.show(MainActivity.this, 9); }

        @JavascriptInterface
        public boolean exactOk() { return Reminders.exactOk(MainActivity.this); }

        @JavascriptInterface
        public void openExact() {
            if (Build.VERSION.SDK_INT >= 31) {
                runOnUiThread(() -> startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:" + getPackageName()))));
            }
        }
    }
}
