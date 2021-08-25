package com.pdfviewer.analytics;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseAnalyticsAdsActivity extends AppCompatActivity {

    private String currentTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppAnalytics.getInstance(this).addScreenTags(this);

        getAppAnalytics();
    }

    public AppAnalytics getAppAnalytics() {
        return AppAnalytics.getInstance(this);
    }

    public void addTag(String title) {
        this.currentTag = title;
        AppAnalytics.getInstance(this).addTag(title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppAnalytics.getInstance(this).setCurrentScreen(this);
    }

    @Override
    protected void onDestroy() {
        removeTag(this, currentTag);
        super.onDestroy();
    }
    private void removeTag(Context context, String title) {
        this.currentTag = title;
        AppAnalytics.getInstance(this).removeTag(title);
        AppAnalytics.getInstance(this).removeScreenTags(context);
    }

}
