package com.pdfviewer.sample;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.pdfviewer.PDFViewer;


public class AppApplication extends Application {

    private static AppApplication _instance;

    public static AppApplication getInstance() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        PDFViewer.getInstance().init(this);
        PDFViewer.setDownloadDirectory(this,"PDFViewerApp");
        initTheme();
    }

    public static final String NIGHT_MODE = "NIGHT_MODE";

    private void initTheme() {
        if (isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public boolean isNightModeEnabled() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        return mPrefs.getBoolean(NIGHT_MODE, false);
    }

    public void setNightModeEnabled(boolean isNightModeEnabled) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(NIGHT_MODE, isNightModeEnabled);
        editor.apply();
    }

    public void setIsNightModeEnabled(MainActivity activity, boolean isNightModeEnabled) {
        setNightModeEnabled(isNightModeEnabled);
//        if (isNightModeEnabled) {
//            activity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            activity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }

        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
