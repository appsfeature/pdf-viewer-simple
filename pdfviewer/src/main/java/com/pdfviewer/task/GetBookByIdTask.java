package com.pdfviewer.task;

import android.content.Context;

import com.helper.task.TaskRunner;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.model.PDFModel;

import java.util.concurrent.Callable;


public class GetBookByIdTask {
    private final PDFModel pdfModel;
    private final TaskRunner.Callback<PDFModel> callback;

    public GetBookByIdTask(PDFModel pdfModel, TaskRunner.Callback<PDFModel> callback) {
        this.pdfModel = pdfModel;
        this.callback = callback;
    }

    public void execute(Context context) {
        TaskRunner.getInstance().executeAsync(new Callable<PDFModel>() {
            @Override
            public PDFModel call() throws Exception {
                PDFModel response = fetchFromDB(context, pdfModel.getId(), pdfModel.getTitle());
                if(response == null){
                    PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().insertOnlySingleRecord(pdfModel);
                }else {
                    PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().updateStatistics(pdfModel.getId(), pdfModel.getTitle(), pdfModel.getStatsJson());
                }
                return response;
            }
        },callback);
    }

    public static PDFModel fetchFromDB(Context context, int id, String title) {
        return PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().getPdfById(id, title);
    }
}
