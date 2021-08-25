package com.pdfviewer.task;

import android.content.Context;
import android.text.TextUtils;

import com.helper.task.TaskRunner;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.util.ArraysUtil;
import com.pdfviewer.util.PdfUtil;

import java.util.concurrent.Callable;

public class UpdatePagePosition {
    private PDFModel book;
    private TaskRunner.Callback<Boolean> callback;

    private boolean status = false;

    public UpdatePagePosition(PDFModel book, TaskRunner.Callback<Boolean> callback) {
        this.book = book;
        this.callback = callback;
    }


    public void execute(Context context) {
        TaskRunner.getInstance().executeAsync(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    if (PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().getPdfById(book.getId(), book.getTitle()) != null) {
                        PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().updateCurrentPosition(
                                book.getId(), book.getTitle(),
                                book.getOpenPagePosition(),book.getViewCount(), book.getViewCountFormatted(), PdfUtil.getDatabaseDateTime()
                        );
                    } else {
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
