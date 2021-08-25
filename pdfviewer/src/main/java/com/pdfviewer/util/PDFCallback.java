package com.pdfviewer.util;

import android.view.View;


public interface PDFCallback {

    interface Callback<T> {
        void onSuccess(T response);
        void onFailure(Exception e);
    }

    interface StatsListener {
        void onStatsUpdated();
    }

    interface Status<T> {
        void onSuccess(T response);
    }

    interface LastUpdateListener {
        void onRecentPdfViewedUpdated();
    }

    interface BookmarkUpdateListener {
        void onBookmarkUpdate(int id, String bookmarkPages);
    }

    interface OnClickListener<T> {
        void onItemClicked(View view, T item);
    }

    interface OnListClickListener<T> {
        void onItemClicked(View view, T item);
        void onDeleteClicked(View view, int position, T item);
    }

    interface OnClickListenerWithDelete<T> {
        void onItemClicked(View view, T item);
        void onDeleteClicked(View view, T item);
        void onUpdateUI();
    }

    interface Progress {
        void onStartProgressBar();
        void onStopProgressBar();
    }
}