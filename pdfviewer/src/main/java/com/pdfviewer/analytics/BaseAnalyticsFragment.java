package com.pdfviewer.analytics;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseAnalyticsFragment extends Fragment {

    private String currentTag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppAnalytics.getInstance(getActivity()).addScreenTags(getContext());
        getAppAnalytics();
        addTag(getClass().getSimpleName());
    }


    public AppAnalytics getAppAnalytics() {
        return AppAnalytics.getInstance(getActivity());
    }

    public void addTag(String title) {
        this.currentTag = title;
        AppAnalytics.getInstance(getActivity()).addTag(title);
    }

    @Override
    public void onStart() {
        super.onStart();
        AppAnalytics.getInstance(getActivity()).setCurrentScreen(getActivity());
    }

    @Override
    public void onDestroy() {
        removeTag(getActivity(), currentTag);
        super.onDestroy();
    }

    private void removeTag(Context context, String title) {
        this.currentTag = title;
        AppAnalytics.getInstance(getActivity()).removeTag(title);
        AppAnalytics.getInstance(getActivity()).removeScreenTags(context);
    }

}
