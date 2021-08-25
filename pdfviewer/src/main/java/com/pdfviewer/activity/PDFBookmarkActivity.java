package com.pdfviewer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.helper.task.TaskRunner;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.R;
import com.pdfviewer.model.PDFBookmarkAdapter;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.util.PDFCallback;
import com.pdfviewer.util.PDFConstant;
import com.pdfviewer.util.PDFFileUtil;
import com.pdfviewer.util.PdfUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class PDFBookmarkActivity extends AppCompatActivity {

    private PDFBookmarkAdapter adapter;
    private List<PDFModel> mList = new ArrayList<>();
    private boolean isShowDownloadedList = false;
    private View llNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_bookmark_activity);

        initLayouts();

        getDataFromIntent();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(isShowDownloadedList ? "Downloaded Books" : "Pdf Bookmark");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        PdfUtil.initBannerAd(this, findViewById(R.id.rl_ads));

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            isShowDownloadedList = intent.getBooleanExtra(PDFConstant.IS_SHOW_DOWNLOADED_LIST, false);
        }
    }

    private Handler handler;

    @Override
    protected void onResume() {
        super.onResume();
        if (handler == null) {
            handler = new Handler(Looper.myLooper());
        }
        fetchFromDatabase();
    }

    private PDFCallback.LastUpdateListener recentUpdateListener = new PDFCallback.LastUpdateListener() {
        @Override
        public void onRecentPdfViewedUpdated() {
            fetchFromDatabase();
        }
    };

    private void initLayouts() {
        llNoData = findViewById(R.id.ll_no_data);
        RecyclerView rvList = findViewById(R.id.rvList);

        adapter = new PDFBookmarkAdapter(mList, new PDFCallback.OnListClickListener<PDFModel>() {
            @Override
            public void onItemClicked(View view, PDFModel item) {
                PDFViewer.openPdfViewerActivity(PDFBookmarkActivity.this, item, true);
            }

            @Override
            public void onDeleteClicked(View view, int position, PDFModel item) {
                handleDeleteClick(view, position, item);
            }
        });
        rvList.setAdapter(adapter);

        PDFViewer.getInstance().addRecentUpdateListener(recentUpdateListener);
    }

    private void fetchFromDatabase() {
        TaskRunner.getInstance().executeAsync(new Callable<List<PDFModel>>() {
            @Override
            public List<PDFModel> call() throws Exception {
                List<PDFModel> finalList = new ArrayList<>();
                List<PDFModel> mLocalList;
                if (isShowDownloadedList) {
                    mLocalList = PDFViewer.getInstance().getDatabase(PDFBookmarkActivity.this).pdfViewerDAO().fetchAllDownloadedData();
                } else {
                    mLocalList = PDFViewer.getInstance().getDatabase(PDFBookmarkActivity.this).pdfViewerDAO().fetchAllData();
                }
                List<String> mStorageFileList = PDFFileUtil.getStorageFileList(PDFBookmarkActivity.this);
                if(mStorageFileList != null && mLocalList != null && mLocalList.size() > 0) {
                    for (PDFModel item : mLocalList) {
                        String fileName = getFileName(item.getFilePath());
                        if (mStorageFileList.contains(fileName)) {
                            finalList.add(item);
                        }
                    }
                }
                return finalList;
            }
        }, new TaskRunner.Callback<List<PDFModel>>() {
            @Override
            public void onComplete(List<PDFModel> result) {
                if (result != null) {
                    loadList(result);
                } else {
                    loadList(null);
                }
            }
        });
    }

    private String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        if (!filePath.contains("/")) {
            return filePath;
        }
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    private void loadList(List<PDFModel> list) {
        PdfUtil.showNoData(llNoData, View.GONE);
        mList.clear();
        if (list != null && list.size() > 0) {
            mList.addAll(list);
        } else {
            PdfUtil.showNoData(llNoData, View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleDeleteClick(View v, int position, PDFModel model) {
        if (model != null) {
            TaskRunner.getInstance().executeAsync(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    if (isShowDownloadedList) {
                        PDFViewer.getInstance().getDatabase(v.getContext()).pdfViewerDAO().delete(model.getId(), model.getTitle());
                        PDFFileUtil.deleteFile(PDFBookmarkActivity.this, model.getPdf());
                    } else {
                        PDFViewer.getInstance().getDatabase(v.getContext()).pdfViewerDAO().updateBookmarkPages(model.getId(), model.getTitle(), "", PdfUtil.getDatabaseDateTime());
                    }
                    return true;
                }
            }, new TaskRunner.Callback<Boolean>() {
                @Override
                public void onComplete(Boolean result) {
                    try {
                        if(mList != null) {
                            if (mList.size() > 0 && mList.size() > position) {
                                mList.remove(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                            }
                            if (mList.size() == 0) {
                                PdfUtil.showNoData(llNoData, View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}