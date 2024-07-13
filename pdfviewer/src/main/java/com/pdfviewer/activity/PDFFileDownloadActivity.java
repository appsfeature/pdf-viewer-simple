package com.pdfviewer.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.configlite.network.download.DownloadManager;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.R;
import com.pdfviewer.analytics.BaseAnalyticsActivity;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.util.PDFConstant;
import com.pdfviewer.util.PDFFileUtil;
import com.pdfviewer.util.PdfUtil;

import java.io.File;
import java.util.Locale;


public class PDFFileDownloadActivity extends BaseAnalyticsActivity implements DownloadManager.Progress {

    private String mFileUrl;
    private String mPdfFileName;
    private View btnDownload;
    private View downloadProgress;
    private TextView tv_download_percentage;
    private DownloadManager downloadManager;
    private View downloadButton;
    private String mPdfTitle;
    private boolean isAutoDownload = false;
    private PDFModel pdfModel;
    private boolean isOpenExternal = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_main_downlaod);
        downloadManager = new DownloadManager(this);
        downloadManager.setBaseDirectory(PDFFileUtil.getFileStoreDirectory(this));
        PdfUtil.initBannerAd(this, findViewById(R.id.adViewtop));
        init();
        getDataFromIntent();
    }

    @Override
    public void onShowAdsInUi() {
        RelativeLayout rlNativeAd = findViewById(R.id.full_ad);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null && intent.getExtras().getParcelable(PDFConstant.EXTRA_PROPERTY) instanceof PDFModel) {
            pdfModel = intent.getExtras().getParcelable(PDFConstant.EXTRA_PROPERTY);
            isAutoDownload = intent.getBooleanExtra(PDFConstant.IS_AUTO_DOWNLOAD, false);
            isOpenExternal = intent.getBooleanExtra(PDFConstant.IS_OPEN_EXTERNAL, false);
            if (pdfModel != null && !TextUtils.isEmpty(pdfModel.getTitle())
                    && !TextUtils.isEmpty(pdfModel.getPdfUrl())) {
                mPdfFileName = pdfModel.getPdf();//contains pdf file name.
                mPdfTitle = pdfModel.getTitle();//contains pdf title visible to user.
                mFileUrl = pdfModel.getPdfUrl();//contains .pdf extension

                if (mPdfFileName == null) {
                    mPdfFileName = PDFFileUtil.getFileNameFromUrl(pdfModel.getPdfUrl());
                }
                if ( !TextUtils.isEmpty(pdfModel.getPdfBaseUrlPrefix()) ) {
                    downloadManager.setBaseUrl(pdfModel.getPdfBaseUrlPrefix() );
                }
                downloadManager.loadFileIfExists(this, mPdfFileName);
            } else {
                PdfUtil.showToast(this, PDFConstant.INVALID_PROPERTY);
                finish();
            }
        } else {
            PdfUtil.showToast(this, PDFConstant.INVALID_PROPERTY);
            finish();
        }
    }


    void init() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Download PDF");
        }

        downloadButton = findViewById(R.id.layout_download_button);
        btnDownload = findViewById(R.id.btnProgressBar);
        downloadProgress = findViewById(R.id.layout_download_progress);
        tv_download_percentage = findViewById(R.id.tv_download_percentage);


        btnDownload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                downloadPdfFile(mFileUrl);
            }
        });
    }

    private String mStatistics;

    @Override
    protected void onStart() {
        super.onStart();
//        mStatistics = TextUtils.isEmpty(pdfModel.getStatsJson()) ? getStatistics() : getUpdatedStatistics(pdfModel.getStatsJson());
    }

    private String getUpdatedStatistics(String statsJson) {
        return null;
    }

    private void downloadPdfFile(String pdfUrl) {
        if(mStatistics == null){
            mStatistics = TextUtils.isEmpty(pdfModel.getStatsJson()) ? "" : getUpdatedStatistics(pdfModel.getStatsJson());
        }
        downloadButton.setVisibility(View.VISIBLE);
        downloadManager.downloadFile(this, pdfUrl);
    }

    private void openPdfFromUri(Uri uri, Boolean isFileAlreadyDownloaded) {
        if (!isOpenExternal) {
            if(mStatistics == null){
                mStatistics = TextUtils.isEmpty(pdfModel.getStatsJson()) ? "" : getUpdatedStatistics(pdfModel.getStatsJson());
            }

            PDFViewer.openPdfViewerActivity(this, pdfModel.getClone(), pdfModel.getId(), mPdfTitle, mPdfFileName, pdfModel.getSubTitle(), mStatistics, uri, isFileAlreadyDownloaded, pdfModel.getViewCount());
        } else {
            openExternalViewer(uri);
            finish();
        }
    }

    private void openExternalViewer(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// don't use this in API30 and above
            DownloadManager.grantAllUriPermissions(this, intent, uri);
        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// don't use this in API30 and above
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Application Available to View PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFirstHit = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDFViewer.INTENT_PDF_LOAD_SUCCESS) {
            if (resultCode == RESULT_OK) {
                finish();
            } else {
                if (isFirstHit) {
                    isFirstHit = false;
                    if (!TextUtils.isEmpty(mFileUrl)) {
                        downloadPdfFile(mFileUrl);
                    }
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressManager(boolean isVisible) {
        if (isVisible) {
            btnDownload.setVisibility(View.INVISIBLE);
            downloadProgress.setVisibility(View.VISIBLE);
        } else {
            btnDownload.setVisibility(View.VISIBLE);
            downloadProgress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDownloadedFileStatus(boolean isFileExists) {
        if (!isFileExists && isAutoDownload) {
            downloadPdfFile(mFileUrl);
        }
    }

    private final Handler handler = new Handler(Looper.myLooper());

    @Override
    public void onProgressUpdate(int progress) {
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (tv_download_percentage != null) {
                        tv_download_percentage.setText(String.format(Locale.US, "%d %% ", progress));
                    }
                }
            });
        }
    }

    @Override
    public void onFileDownloaded(File file, Uri fileUri, String ext, String type, Boolean isFileAlreadyDownloaded) {
//        downloadButton.setVisibility(View.GONE);
        onProgressManager(true);
        openPdfFromUri(fileUri, isFileAlreadyDownloaded);
    }

    @Override
    public void onDownloadingError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadingCanceled() {

    }

    @Override
    public void onBackPressed() {
        if (downloadManager != null) {
            downloadManager.cancelDownload();
        }
        super.onBackPressed();
    }
}
