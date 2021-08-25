package com.pdfviewer.task;

import android.content.Context;

import com.pdfviewer.PDFViewer;

public class PDFBackgroundTask {
    public static void deleteBookmarkByTitle(Context context, int id, String title) {
        try {
            PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().delete(
                    id, title
            );
        } catch (Exception e) {
        }
    }

    public static void deleteBookmarkByFileName(Context context, int id, String fileName) {
        try {
            PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().deleteByFileName(
                    id, fileName
            );
        } catch (Exception e) {
        }
    }
}
