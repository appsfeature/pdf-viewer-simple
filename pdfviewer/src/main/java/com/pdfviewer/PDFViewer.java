package com.pdfviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.helper.task.TaskRunner;
import com.helper.util.BaseUtil;
import com.pdfviewer.activity.PDFBookmarkActivity;
import com.pdfviewer.activity.PDFFileDownloadActivity;
import com.pdfviewer.activity.PDFViewerActivity;
import com.pdfviewer.database.PDFDatabase;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.task.DeleteBookTask;
import com.pdfviewer.util.ArraysUtil;
import com.pdfviewer.util.PDFCallback;
import com.pdfviewer.util.PDFConstant;
import com.pdfviewer.util.PDFFileUtil;
import com.pdfviewer.util.PDFSupportPref;

import java.util.ArrayList;

/**
 * Stable Release
 */
public class PDFViewer {

    private static volatile PDFViewer sSoleInstance;
    public static final int INTENT_PDF_LOAD_SUCCESS = 1000;
    private PDFCallback.LastUpdateListener recentUpdateListener;
    private PDFCallback.BookmarkUpdateListener bookmarkUpdateListener;
    private boolean isDebugModeEnabled = false;
    private boolean isEnableViewCount = false;
    private String baseUrl;

    private final ArrayList<PDFCallback.StatsListener> mStatsCallbacks = new ArrayList<>();

    private PDFViewer() { }

    public void init(Context context) {
        PDFFileUtil.initStorageFileMigrationOnApiLevel29(context);
    }

    public static PDFViewer getInstance() {
        if (sSoleInstance == null) {
            synchronized (PDFViewer.class) {
                if (sSoleInstance == null) sSoleInstance = new PDFViewer();
            }
        }
        return sSoleInstance;
    }

    private PDFDatabase pdfDatabase;

    public PDFDatabase getDatabase(Context context) {
        if (pdfDatabase == null) {
            pdfDatabase = PDFDatabase.create(context);
        }
        return pdfDatabase;
    }

    /**
     * @param downloadDirectory : your app directory name, if you want to save your all pdf file in custom folder.
     */
    public static void setDownloadDirectory(Context context, String downloadDirectory) {
        PDFSupportPref.setDownloadDirectory(context, downloadDirectory);
    }

    public static void setEnableFileStreamPath(Context context, boolean isEnableFileStreamPath) {
        PDFSupportPref.setEnableFileStreamPath(context, isEnableFileStreamPath);
    }

    public static Boolean isEnableFileStreamPath(Context context) {
        return PDFSupportPref.isEnableFileStreamPath(context);
    }

    public static void setHeaderAuth(Context context, String value) {
        PDFSupportPref.setHeaderAuth(context, value);
    }

    public static void setHeaderAuthEnc(Context context, String value) {
        PDFSupportPref.setHeaderAuthEnc(context, value);
    }


    public static void openPdfDownloadActivity(Activity activity, int id, String pdfTitle, String pdfFileName, String pdfFileUrl, String subTitle) {
        openPdfDownloadActivity(activity, id, pdfTitle, pdfFileName, null, pdfFileUrl, subTitle, false, false);
    }

    public static void openPdfDownloadActivity(Activity activity, int id, String pdfTitle, String pdfFileName, String pdfBaseUrlPrefix, String pdfFileUrl, String subTitle) {
        openPdfDownloadActivity(activity, id, pdfTitle, pdfFileName, pdfBaseUrlPrefix, pdfFileUrl, subTitle, false, false);
    }

    public static void openPdfDownloadActivity(Activity activity, int id, String pdfTitle, String pdfFileName, String pdfFileUrl, String subTitle, boolean isAutoDownload) {
        openPdfDownloadActivity(activity, id, pdfTitle, pdfFileName, null, pdfFileUrl, subTitle, isAutoDownload, false);
    }

    public static void openPdfDownloadActivity(Activity activity, int id, String pdfTitle, String pdfFileName, String pdfFileUrl, String subTitle, boolean isAutoDownload, boolean isOpenExternal) {
        openPdfDownloadActivity(activity, id, pdfTitle, pdfFileName, null, pdfFileUrl, subTitle, isAutoDownload, isOpenExternal);
    }
    public static void openPdfDownloadActivity(Activity activity, int id, String pdfTitle, String pdfFileName, String pdfFileUrl, String subTitle, boolean isAutoDownload, boolean isOpenExternal, int viewCount) {
        openPdfDownloadActivity(activity, id, pdfTitle, pdfFileName, null, pdfFileUrl, subTitle, isAutoDownload, isOpenExternal, viewCount);
    }
    public static void openPdfDownloadActivity(Activity activity, int id, String pdfTitle, String pdfFileName, String pdfBaseUrlPrefix, String pdfFileUrl, String subTitle, boolean isAutoDownload, boolean isOpenExternal) {
        openPdfDownloadActivity(activity, id, pdfTitle, pdfFileName, pdfBaseUrlPrefix, pdfFileUrl, subTitle, isAutoDownload, isOpenExternal, 0);
    }
        /**
         * @param activity    reference
         * @param pdfTitle    visible to user eg. 'XYZ Pdf'
         * @param pdfFileName file name eg. 'xyz.pdf' where xyz is the file name
         * @param pdfBaseUrlPrefix  contains base url prefix without .pdf extension
         * @param pdfFileUrl  contains url with .pdf extension
         * @param subTitle  category name
         * @param isAutoDownload  category name
         * @param isOpenExternal  open pdf in external viewer
         */
    public static void openPdfDownloadActivity(Activity activity, int id, String pdfTitle, String pdfFileName, String pdfBaseUrlPrefix, String pdfFileUrl, String subTitle, boolean isAutoDownload, boolean isOpenExternal, int viewCount) {
        if(validateLibrary(activity)) {
            PDFModel pdfModel = new PDFModel();
            pdfModel.setId(id);
            pdfModel.setTitle(pdfTitle);
            pdfModel.setPdf(pdfFileName);
            pdfModel.setPdfBaseUrlPrefix(pdfBaseUrlPrefix);
            pdfModel.setPdfUrl(pdfFileUrl);
            pdfModel.setSubTitle(subTitle);
            pdfModel.setViewCount(viewCount);
            pdfModel.setViewCountFormatted(viewCount == 0 ? "" : BaseUtil.convertNumberUSFormat(viewCount));
            Intent intent = new Intent(activity, PDFFileDownloadActivity.class);
            intent.putExtra(PDFConstant.EXTRA_PROPERTY, pdfModel);
            intent.putExtra(PDFConstant.IS_AUTO_DOWNLOAD, isAutoDownload);
            intent.putExtra(PDFConstant.IS_OPEN_EXTERNAL, isOpenExternal);
            activity.startActivity(intent);
        }
    }

    public static void openPdfDownloadActivity(Activity activity, PDFModel pdfModel, boolean isAutoDownload, boolean isOpenExternal) {
        if(validateLibrary(activity) && pdfModel != null) {
            Intent intent = new Intent(activity, PDFFileDownloadActivity.class);
            intent.putExtra(PDFConstant.EXTRA_PROPERTY, pdfModel);
            intent.putExtra(PDFConstant.IS_AUTO_DOWNLOAD, isAutoDownload);
            intent.putExtra(PDFConstant.IS_OPEN_EXTERNAL, isOpenExternal);
            activity.startActivity(intent);
        }
    }

    public static void openPdfViewerActivity(Activity context, int id, String pdfTitle, String pdfFileName, Uri fileUri) {
        openPdfViewerActivity(context, id, pdfTitle, pdfFileName, null, fileUri);
    }

    public static void openPdfViewerActivity(Activity context, int id, String pdfTitle, String pdfFileName, String subTitle, Uri fileUri) {
        openPdfViewerActivity(context, id, pdfTitle, pdfFileName, subTitle, "", fileUri);
    }
    /**
     * @param context     reference
     * @param pdfTitle    visible to user eg.'XYZ Pdf'
     * @param pdfFileName file name eg.'xyz.pdf' where xyz is the file name
     * @param subTitle contains category name
     * @param fileUri     contains file path stored in your local storage.
     */
    public static void openPdfViewerActivity(Activity context, int id, String pdfTitle, String pdfFileName, String subTitle, String statsJson, Uri fileUri) {
        openPdfViewerActivity(context, id, pdfTitle, pdfFileName, subTitle, statsJson, fileUri, true);
    }

    public static void openPdfViewerActivity(Activity context, int id, String pdfTitle, String pdfFileName, String subTitle, String statsJson, Uri fileUri, boolean isFileAlreadyDownloaded) {
        openPdfViewerActivity(context, id, pdfTitle, pdfFileName, subTitle, statsJson, fileUri, isFileAlreadyDownloaded, 0);
    }

    public static void openPdfViewerActivity(Activity context, int id, String pdfTitle, String pdfFileName, String subTitle, String statsJson, Uri fileUri, boolean isFileAlreadyDownloaded, int viewCount) {
        openPdfViewerActivity(context, new PDFModel(), id, pdfTitle, pdfFileName, subTitle, statsJson, fileUri, isFileAlreadyDownloaded, viewCount);
    }

    public static void openPdfViewerActivity(Activity context, PDFModel pdfModel, int id, String pdfTitle, String pdfFileName, String subTitle, String statsJson, Uri fileUri, boolean isFileAlreadyDownloaded, int viewCount) {
        pdfModel.setId(id);
        pdfModel.setTitle(pdfTitle);
        pdfModel.setPdf(pdfFileName);
        pdfModel.setStatsJson(statsJson);
        pdfModel.setSubTitle(subTitle);
        pdfModel.setFilePath(fileUri.toString());
        pdfModel.setFileAlreadyDownloaded(isFileAlreadyDownloaded);
        pdfModel.setViewCount(viewCount);
        pdfModel.setViewCountFormatted(viewCount == 0 ? "" : BaseUtil.convertNumberUSFormat(viewCount));
        openPdfViewerActivity(context, pdfModel);
    }

    public static void openPdfViewerActivity(Activity context, PDFModel pdfModel) {
        openPdfViewerActivity(context, pdfModel, false);
    }

    public static void openPdfViewerActivity(Activity context, PDFModel pdfModel, boolean showBookmarkDialog) {
        if(validateLibrary(context)) {
            Intent intent = new Intent(context, PDFViewerActivity.class);
            intent.putExtra(PDFConstant.EXTRA_PROPERTY, pdfModel);
            intent.putExtra(PDFConstant.SHOW_BOOKMARK_DIALOG, showBookmarkDialog);
            context.startActivityForResult(intent, INTENT_PDF_LOAD_SUCCESS);
        }
    }

    public static void openPdfViewerFromHistory(Context context, PDFModel pdfModel) {
        if(validateLibrary(context)) {
            Intent intent = new Intent(context, PDFViewerActivity.class);
            intent.putExtra(PDFConstant.EXTRA_PROPERTY, pdfModel);
            context.startActivity(intent);
        }
    }

    private static boolean validateLibrary(Context context) {
        if(!PDFSupportPref.isStorageMigrationCompleted(context)){
            BaseUtil.showToast(context, PDFConstant.ERROR_PDF_VIEWER_INITIALIZATION);
            return false;
        }
        return true;
    }

    public static void openPdfBookmarkActivity(Context context) {
        context.startActivity(new Intent(context, PDFBookmarkActivity.class));
    }

    public static void openPdfDownloadedListActivity(Context context) {
        Intent intent = new Intent(context, PDFBookmarkActivity.class);
        intent.putExtra(PDFConstant.IS_SHOW_DOWNLOADED_LIST, true);
        context.startActivity(intent);
    }

    /**
     * @param activity reference
     * @param pdfId    id'
     * @param pdfTitle visible to user eg. 'XYZ Pdf'
     * Delete item from database only.
     */
    public static void deletePdfByTitle(Activity activity, int pdfId, String pdfTitle, TaskRunner.Callback<Boolean> callback) {
        PDFModel pdfModel = new PDFModel();
        pdfModel.setId(pdfId);
        pdfModel.setTitle(pdfTitle);
        new DeleteBookTask(pdfModel, callback).execute(activity);
    }

    /**
     * @param activity reference
     * @param pdfId    id'
     * @param pdfFileName file name eg.'xyz.pdf' where xyz is the file name
     * Delete item from both database and storage.
     */
    public static void deletePdfByFileName(Activity activity, int pdfId, String pdfFileName, TaskRunner.Callback<Boolean> callback) {
        PDFModel pdfModel = new PDFModel();
        pdfModel.setId(pdfId);
        pdfModel.setPdf(pdfFileName);
        new DeleteBookTask(pdfModel, callback).execute(activity);
    }

    public void addRecentUpdateListener(PDFCallback.LastUpdateListener recentUpdateListener) {
        if(this.recentUpdateListener != null){
            this.recentUpdateListener = null;
        }
        this.recentUpdateListener = recentUpdateListener;
    }

    public PDFCallback.LastUpdateListener getRecentUpdateListener() {
        return recentUpdateListener;
    }

    public PDFViewer addBookmarkUpdateListener(PDFCallback.BookmarkUpdateListener recentUpdateListener) {
        if(this.bookmarkUpdateListener != null){
            this.bookmarkUpdateListener = null;
        }
        this.bookmarkUpdateListener = recentUpdateListener;
        return this;
    }

    public PDFCallback.BookmarkUpdateListener getBookmarkUpdateListener() {
        return bookmarkUpdateListener;
    }

    public void updateBookmarkUpdateListener(int id, String bookmarkPages) {
        if (getBookmarkUpdateListener() != null) {
            getBookmarkUpdateListener().onBookmarkUpdate(id, ArraysUtil.shortStringArrayList(bookmarkPages));
        }
    }

    public boolean isDebugModeEnabled() {
        return isDebugModeEnabled;
    }

    public PDFViewer setDebugModeEnabled(boolean debugModeEnabled) {
        isDebugModeEnabled = debugModeEnabled;
        return this;
    }

    public PDFViewer addStatisticsCallbacks(PDFCallback.StatsListener callback) {
        synchronized (mStatsCallbacks) {
            mStatsCallbacks.add(callback);
        }
        return this;
    }

    public void removeStatisticsCallbacks(PDFCallback.StatsListener callback) {
        synchronized (mStatsCallbacks) {
            mStatsCallbacks.remove(callback);
        }
    }

    public void dispatchStatsUpdated() {
        for (PDFCallback.StatsListener callback : mStatsCallbacks) {
            callback.onStatsUpdated();
        }
    }

    public boolean isEnableViewCount() {
        return isEnableViewCount;
    }

    public PDFViewer setEnableViewCount(boolean enableViewCount) {
        isEnableViewCount = enableViewCount;
        return this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public PDFViewer setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }
}