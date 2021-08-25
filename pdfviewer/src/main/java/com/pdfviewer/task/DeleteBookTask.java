package com.pdfviewer.task;

import android.content.Context;

import com.helper.task.TaskRunner;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.util.PDFFileUtil;

import java.util.concurrent.Callable;

public class DeleteBookTask {
    private PDFModel book;
    private TaskRunner.Callback<Boolean> callback;

    private boolean status = false;

    public DeleteBookTask(PDFModel book, TaskRunner.Callback<Boolean> callback) {
        this.book = book;
        this.callback = callback;
    }


    public void execute(Context context) {
        TaskRunner.getInstance().executeAsync(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    if (book.getPdf() == null) {
                        PDFBackgroundTask.deleteBookmarkByTitle(context, book.getId(), book.getTitle());
                    } else {
                        PDFBackgroundTask.deleteBookmarkByFileName(context, book.getId(), book.getPdf());
                        PDFFileUtil.deleteFile(context, book.getPdf());
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
