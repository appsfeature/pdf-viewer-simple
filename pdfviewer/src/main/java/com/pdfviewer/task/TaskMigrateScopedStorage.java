package com.pdfviewer.task;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.google.gson.reflect.TypeToken;
import com.helper.callback.ItemType;
import com.helper.model.HistoryModelResponse;
import com.helper.task.TaskRunner;
import com.helper.util.BaseUtil;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.R;
import com.pdfviewer.activity.PDFBookmarkActivity;
import com.pdfviewer.model.PDFHistoryModel;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.util.PDFFileUtil;
import com.pdfviewer.util.PdfUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public class TaskMigrateScopedStorage {

    private final Context context;

    public TaskMigrateScopedStorage(Context context) {
        this.context = context;
    }

    public void execute() {
        File folderDirectory = PDFFileUtil.getFileStoreDirectory(context);

        List<PDFModel> mLocalList = PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().fetchAllDownloadedData();
        if (mLocalList != null && mLocalList.size() > 0) {
            for (PDFModel item : mLocalList) {
                Uri filePath = getUriFromFile(context, new File(folderDirectory, item.getPdf()));
                item.setFilePath(filePath.toString());
                PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().updateMigrationFilePath(item.getAutoId(), item.getFilePath());
            }
        }

        List<HistoryModelResponse> mHistoryList = PDFViewer.getInstance().getDatabase(context).pdfHistoryDAO().getPDFHistory();
        if (mHistoryList != null && mHistoryList.size() > 0) {
            for (HistoryModelResponse item : mHistoryList) {
                PDFModel pdfProperty = item.getJsonModel(new TypeToken<PDFModel>() {
                });
                Uri filePath = getUriFromFile(context, new File(folderDirectory, pdfProperty.getPdf()));
                pdfProperty.setFilePath(filePath.toString());
                item.setJsonData(pdfProperty.toJson());
                PDFViewer.getInstance().getDatabase(context).pdfHistoryDAO().updateMigrationJsonData(item.getAutoId(), item.getJsonData());
            }
        }
    }

    private Uri getUriFromFile(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + context.getString(R.string.file_provider), file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }
}
