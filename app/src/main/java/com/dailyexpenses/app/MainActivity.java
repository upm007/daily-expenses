package com.dailyexpenses.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** Hosts the Daily Expenses web app (assets/index.html) in a full-screen WebView. */
public class MainActivity extends Activity {

    private static final int FILE_REQUEST = 1001;
    private WebView web;
    private ValueCallback<Uri[]> fileCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);   // localStorage keeps the entries
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri u = request.getUrl();
                if ("file".equals(u.getScheme())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) { }
                return true;
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
                pick.addCategory(Intent.CATEGORY_OPENABLE);
                pick.setType("*/*");
                try {
                    startActivityForResult(Intent.createChooser(pick, "Choose a backup CSV"), FILE_REQUEST);
                } catch (Exception e) {
                    fileCallback = null;
                    return false;
                }
                return true;
            }
        });

        web.addJavascriptInterface(new Bridge(), "AndroidBridge");

        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl("file:///android_asset/index.html");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST && fileCallback != null) {
            Uri[] result = null;
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                result = new Uri[]{ data.getData() };
            }
            fileCallback.onReceiveValue(result);
            fileCallback = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        web.evaluateJavascript("(window.handleBack && window.handleBack()) ? 'yes' : 'no'", value -> {
            if (value == null || !value.contains("yes")) finish();
        });
    }

    /** Called from JavaScript to save a CSV backup to Downloads and open the share sheet. */
    public class Bridge {
        @JavascriptInterface
        public void shareFile(final String name, final String content) {
            runOnUiThread(() -> shareCsv(name, content));
        }
    }

    private void shareCsv(String name, String content) {
        try {
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues cv = new ContentValues();
                cv.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                cv.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
                uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);
                if (uri != null) {
                    try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                        if (os != null) os.write(content.getBytes(StandardCharsets.UTF_8));
                    }
                    Toast.makeText(this, "Saved " + name + " to Downloads", Toast.LENGTH_SHORT).show();
                }
            }
            Intent send = new Intent(Intent.ACTION_SEND);
            send.putExtra(Intent.EXTRA_SUBJECT, name);
            if (uri != null) {
                send.setType("text/csv");
                send.putExtra(Intent.EXTRA_STREAM, uri);
                send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                send.setType("text/plain");
                send.putExtra(Intent.EXTRA_TEXT, content);
            }
            startActivity(Intent.createChooser(send, "Save or send your backup"));
        } catch (Exception e) {
            Toast.makeText(this, "Could not export: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
