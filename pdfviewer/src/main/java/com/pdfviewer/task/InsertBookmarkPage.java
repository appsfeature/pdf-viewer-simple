package com.pdfviewer.task;

import android.content.Context;
import android.text.TextUtils;

import com.helper.task.TaskRunner;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.util.ArraysUtil;
import com.pdfviewer.util.PdfUtil;

import java.util.concurrent.Callable;

public class InsertBookmarkPage {
    private String bookmarkPages;
    private PDFModel book;
    private TaskRunner.Callback<Boolean> callback;

    private boolean status = false;

    public InsertBookmarkPage(String bookmarkPages, PDFModel book, TaskRunner.Callback<Boolean> callback) {
        this.bookmarkPages = bookmarkPages;
        this.book = book;
        this.callback = callback;
    }

    public void execute(Context context) {
        TaskRunner.getInstance().executeAsync(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    if (PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().getPdfById(book.getId(), book.getTitle()) == null) {
                        book.setBookmarkPages(ArraysUtil.shortStringArrayList(bookmarkPages));
                        book.setUpdated_at(PdfUtil.getDatabaseDateTime());
                        book.setLastUpdate(PdfUtil.getDatabaseDateTime());
                        PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().insertOnlySingleRecord(
                                book
                        );
                    }
                    status = true;
                } catch (Exception e) {
                    status = false;
                }
                return status;
            }
        }, callback);

    }
}
