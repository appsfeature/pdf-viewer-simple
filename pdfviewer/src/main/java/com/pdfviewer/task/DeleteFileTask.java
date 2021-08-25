package com.pdfviewer.task;

import android.content.Context;
import android.os.Environment;

import com.helper.task.TaskRunner;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.network.DownloadManager;
import com.pdfviewer.util.PDFFileUtil;

import java.io.File;
import java.util.concurrent.Callable;

public class DeleteFileTask {
    private String fileName;
    private TaskRunner.Callback<Boolean> callback;

    private boolean status = false;

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
