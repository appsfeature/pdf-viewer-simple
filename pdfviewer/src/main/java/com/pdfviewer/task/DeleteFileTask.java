package com.pdfviewer.task;

import android.content.Context;

import com.helper.task.TaskRunner;
import com.pdfviewer.util.PDFFileUtil;

import java.util.concurrent.Callable;

public class DeleteFileTask {
    private final String fileName;
    private final TaskRunner.Callback<Boolean> callback;

    private final boolean status = false;

    public DeleteFileTask(String fileName, TaskRunner.Callback<Boolean> callback) {
        this.fileName = fileName;
        this.callback = callback;
    }


    public void execute(Context context) {
        TaskRunner.getInstance().executeAsync(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                PDFFileUtil.deleteFile(context, fileName);
                return status;
            }
        }, callback);

    }
}
