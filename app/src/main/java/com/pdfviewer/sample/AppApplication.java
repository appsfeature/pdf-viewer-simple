package com.pdfviewer.sample;

import com.helper.application.BaseApplication;
import com.pdfviewer.PDFViewer;


public class AppApplication extends BaseApplication {

    public static final String BASE_URL_PDF_DOWNLOAD = "https://katyayanacademy.com/erp/application/libraries/uploads/studymaterial/";
    private static AppApplication _instance;

    public static AppApplication getInstance() {
        return _instance;
    }

    @Override
    public boolean isDebugMode() {
        return BuildConfig.DEBUG;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        PDFViewer.getInstance()
                .setDebugModeEnabled(isDebugMode())
                .setBaseUrl(BASE_URL_PDF_DOWNLOAD)
                .setDisablePrint(true)
                .setDisableShare(true)
                .init(this);
        PDFViewer.setDownloadDirectory(this,"PDFViewerApp");
    }

    @Override
    public void initLibs() {

    }

}
