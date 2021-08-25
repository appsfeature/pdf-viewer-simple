package com.pdfviewer;

import android.content.Context;

import androidx.annotation.WorkerThread;

import com.helper.model.HistoryModelResponse;

import java.util.List;

public class PDFHandler {
    private final Context context;

    public PDFHandler(Context context) {
        this.context = context;
    }

    @WorkerThread
    public List<HistoryModelResponse> getPDFHistory(boolean isUniqueTestIdsInList) {
        if(isUniqueTestIdsInList){
            return PDFViewer.getInstance().getDatabase(context).pdfHistoryDAO().getPDFHistoryUnique();
        }else {
            return PDFViewer.getInstance().getDatabase(context).pdfHistoryDAO().getPDFHistory();
        }
    }

    @WorkerThread
    public void deleteHistory(int autoId, int id) {
        PDFViewer.getInstance().getDatabase(context).pdfHistoryDAO().delete(autoId, id);
    }

    @WorkerThread
    public void clearHistory() {
        PDFViewer.getInstance().getDatabase(context).pdfHistoryDAO().clearAllRecords();
    }
}
