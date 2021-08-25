package com.pdfviewer.task;

import android.content.Context;

import com.helper.model.HistoryModelResponse;
import com.helper.task.TaskRunner;
import com.pdfviewer.PDFHandler;
import com.pdfviewer.util.PDFCallback;

import java.util.concurrent.Callable;

public class TaskDeleteHistory {

    private final HistoryModelResponse mItem;
    private final PDFHandler testHandler;
    private final PDFCallback.Status<Boolean> callback;

    public TaskDeleteHistory(Context context, HistoryModelResponse mItem, PDFCallback.Status<Boolean> callback) {
        this.testHandler = new PDFHandler(context);
        this.mItem = mItem;
        this.callback = callback;
    }

    public void execute() {
        TaskRunner.getInstance().executeAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                testHandler.deleteHistory(mItem.getAutoId(), mItem.getId());
                return null;
            }
        }, new TaskRunner.Callback<Void>() {
            @Override
            public void onComplete(Void result) {
                callback.onSuccess(true);
            }
        });
    }
}
