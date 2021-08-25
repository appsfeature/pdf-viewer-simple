package com.pdfviewer.task;

import android.content.Context;

import com.helper.callback.ItemType;
import com.helper.task.TaskRunner;
import com.helper.util.BaseUtil;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.model.PDFHistoryModel;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.util.ArraysUtil;
import com.pdfviewer.util.PdfUtil;

import java.util.concurrent.Callable;

public class InsertViewHistory {
    private final PDFModel book;

    public InsertViewHistory(PDFModel book) {
        this.book = book;
    }

    public void execute(Context context) {
        TaskRunner.getInstance().executeAsync(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    PDFHistoryModel history = new PDFHistoryModel();
                    history.setId(book.getId());
                    history.setTitle(book.getTitle());
                    history.setSubTitle(book.getSubTitle());
                    history.setJsonData(book.toJson());
                    history.setCreatedAt(PdfUtil.getDatabaseDateTime());
                    history.setItemType(ItemType.History.TYPE_PDF);
                    history.setViewCount(book.getViewCount());
                    history.setViewCountFormatted(BaseUtil.convertNumberUSFormat(book.getViewCount()));
                    PDFViewer.getInstance().getDatabase(context).pdfHistoryDAO().insertHistory(history);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

    }
}
