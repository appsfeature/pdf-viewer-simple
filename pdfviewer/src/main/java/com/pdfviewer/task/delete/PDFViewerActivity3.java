//package com.pdfviewer.task;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//
//import com.adssdk.PageAdsAppCompactActivity;
//import com.github.barteksc.pdfviewer.PDFView;
//import com.github.barteksc.pdfviewer.listener.OnErrorListener;
//import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
//import com.github.barteksc.pdfviewer.listener.OnLongPressListener;
//import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
//import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
//import com.github.barteksc.pdfviewer.listener.OnTapListener;
//import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
//import com.github.barteksc.pdfviewer.scroll.ScrollHandle;
//import com.github.barteksc.pdfviewer.util.FitPolicy;
//import com.helper.callback.Response;
//import com.helper.task.TaskRunner;
//import com.pdfviewer.R;
//import com.pdfviewer.fragment.PDFIndexFragment;
//import com.pdfviewer.model.PDFModel;
//import com.pdfviewer.util.AlertUtil;
//import com.pdfviewer.util.ArraysUtil;
//import com.pdfviewer.util.PDFCallback;
//import com.pdfviewer.util.PDFConstant;
//import com.pdfviewer.util.PDFSupportPref;
//import com.pdfviewer.util.PdfUtil;
//import com.pdfviewer.util.Screenshot;
//import com.pdfviewer.util.SmoothSeekBar;
//
//import java.util.List;
//
//
//public class PDFViewerActivity3 extends PageAdsAppCompactActivity implements OnPageChangeListener, OnErrorListener
//        , OnPageErrorListener, OnLoadCompleteListener {
//
//    public static final String TAG = "pdfIndexFragment";
//    private PDFView pdfView;
//    private PDFModel pdfModel;
//    private boolean isMenuShow = true;
//    private MenuItem menuCount;
//    private TextView tvProgressCount;
//    private SmoothSeekBar seekBar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pdf_viewer_activity);
//        initView();
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle("PDF");
//        }
//        initDataFromIntent();
//        Log.e("PDFViewerActivity", "called");
//        PdfUtil.initBannerAd(this, findViewById(R.id.rl_ads));
//    }
//
//    private void initView() {
//        pdfView = findViewById(R.id.pdfView);
//        tvProgressCount = findViewById(R.id.tv_progress_count);
//        seekBar = findViewById(R.id.seekBar);
//        pdfView.setBackgroundColor(ContextCompat.getColor(this, R.color.themeWindowBackground));
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                updateProgressCount();
////                makeSmooth(seekBar, progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                onStopSeekBar();
//            }
//        });
//    }
//
//    private void initDataFromIntent() {
//        Intent intent = getIntent();
//        if (intent != null) {
//            if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
//                Uri uri = intent.getData();
//                loadPdf(uri, 0);
//                isMenuShow = false;
//            } else if (intent.getExtras() != null) {
//                Bundle bundle = intent.getExtras();
//                pdfModel = (PDFModel) bundle.getParcelable(PDFConstant.EXTRA_PROPERTY);
//                if (pdfModel != null) {
//                    if(pdfModel.getFileUri()!=null) {
//                        pdfModel.setFilePath(pdfModel.getFileUri().toString());
//                        loadPdf(pdfModel.getFileUri(), pdfModel.getOpenPagePosition());
//                    }else if(!TextUtils.isEmpty(pdfModel.getFilePath())) {
//                        loadPdf(pdfModel.getFilePath(), pdfModel.getOpenPagePosition());
//                    }
//                    updateBookModel(bundle.getBoolean(PDFConstant.SHOW_BOOKMARK_DIALOG, false));
//                } else {
//                    PdfUtil.showToast(this, PDFConstant.INVALID_PROPERTY);
//                    finish();
//                }
//            }
//        } else {
//            PdfUtil.showToast(this, PDFConstant.INVALID_PROPERTY);
//            finish();
//        }
//
//    }
//
//    private void loadPdfHandler(String mFileUri, int initialPage) {
//        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadPdf(mFileUri, initialPage);
//            }
//        }, 10);
//    }
//
//    private void loadPdf(String mFileUri, int initialPage) {
//        if (mFileUri != null && pdfView != null) {
//            Uri fileUri = Uri.parse(mFileUri);
//            loadPdf(fileUri, initialPage);
//        }
//    }
//
//    private void loadPdf(Uri mFileUri, int initialPage) {
//        if (mFileUri != null && pdfView != null) {
//            pdfView.fromUri(mFileUri)
//                    .defaultPage(setInitialPage(initialPage))
//                    .onPageChange(this)
//                    .onError(this)
//                    .pageFitPolicy(FitPolicy.WIDTH)
//                    .onPageError(this)
//                    .pageSnap(false)
//                    .nightMode(PdfUtil.isNightMode())
//                    .onLongPress(new OnLongPressListener() {
//                        @Override
//                        public void onLongPress(MotionEvent e) {
//                            bookmarkCurrentPage();
//                        }
//                    })
//                    .onTap(new OnTapListener() {
//                        @Override
//                        public boolean onTap(MotionEvent e) {
//                            showSeekBar();
//                            return false;
//                        }
//                    })
//                    .scrollHandle(getScrollHandler())
//                    .spacing(20)
//                    .onLoad(this)
//                    .load();
//        }
//    }
//
//    private void showSeekBar() {
//        if (seekBar != null) {
//            seekBar.setVisibility(seekBar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//            tvProgressCount.setVisibility(tvProgressCount.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//        }
//    }
//
//    private void setSeekBarSize(int maxValue) {
//        if (seekBar != null) {
//            seekBar.setMax(maxValue - 1);
//            updateProgressCount();
//        }
//    }
//
//    private void updateProgressCount() {
//        if (tvProgressCount != null && seekBar != null) {
//            int progress = seekBar.getProgress() + 1;
//            int max = seekBar.getMax() + 1;
//            tvProgressCount.setText(progress + " / " + max);
//        }
//    }
//
//    private void onStopSeekBar() {
//        loadPage(seekBar.getProgress());
//    }
//
//    private ScrollHandle getScrollHandler() {
//        DefaultScrollHandle scrollHandler = new DefaultScrollHandle(this);
//        scrollHandler.setTextColor(Color.BLACK);
//        return scrollHandler;
//    }
//
//    private int setInitialPage(int currentPage) {
//        return currentPage - 1;
//    }
//
//    private int getCurrentPage(int currentPage) {
//        return currentPage + 1;
//    }
//
//    private void showError() {
//        setResult(RESULT_CANCELED);
//        PdfUtil.showToast(this, "Error : Invalid PDF file.");
//        finish();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.pdf_viewer_menu, menu);
//        menuCount = menu.findItem(R.id.menu_count);
//        menuCount.setVisible(false);
//        if (!isMenuShow) {
//            MenuItem menu_bookmark = menu.findItem(R.id.menu_bookmark);
//            menu_bookmark.setVisible(false);
//            MenuItem menu_bookmark_add = menu.findItem(R.id.menu_bookmark_add);
//            menu_bookmark_add.setVisible(false);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            onBackPressed();
//            return true;
//        } else if (id == R.id.menu_fullscreen) {
//            showFullScreenMode(true);
//            return true;
//        } else if (id == R.id.menu_bookmark_add) {
//            bookmarkCurrentPage();
//            return true;
//        } else if (id == R.id.menu_bookmark) {
//            showBookMarkDialog();
//            return true;
//        } else if (id == R.id.menu_screenshot) {
//            String title = (pdfModel != null) ? pdfModel.getTitle() : "screenshot_" + pdfView.getCurrentPage();
//            Screenshot.takeScreenShot(title, pdfView, new TaskRunner.CallbackWithError<Uri>() {
//                @Override
//                public void onComplete(Uri result) {
//                    PdfUtil.shareImageWithImagePath(PDFViewerActivity3.this, title, result);
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    PdfUtil.showToast(PDFViewerActivity3.this, e.getMessage());
//                }
//            });
//            return true;
//        } else if (id == R.id.menu_index) {
//            openTableOfContent();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void showBookMarkDialog() {
//        if (pdfModel != null && !TextUtils.isEmpty(pdfModel.getBookmarkPages())) {
//            final List<String> pages = ArraysUtil.split(pdfModel.getBookmarkPages());
//            AlertUtil.openPageSelection(this, pdfModel, pages, new PDFCallback.OnClickListenerWithDelete<String>() {
//                @Override
//                public void onItemClicked(@Nullable View view, String item) {
//                    pdfModel.setOpenPagePosition(PdfUtil.paresInt(item));
//                    loadPage(setInitialPage(pdfModel.getOpenPagePosition()));
//                }
//
//                @Override
//                public void onDeleteClicked(@Nullable View view, String bookmarkPages) {
//                    new UpdateBookmarkPage(bookmarkPages, pdfModel, new TaskRunner.Callback<Boolean>() {
//                        @Override
//                        public void onComplete(Boolean result) {
//                            updateBookModel(false);
//                        }
//
//                    }).execute(PDFViewerActivity3.this);
//                }
//
//                @Override
//                public void onUpdateUI() {
//                    updateBookModel(false);
//                }
//            });
//        } else {
//            PdfUtil.showToast(this, "No bookmark available");
//        }
//    }
//
//    private void updateBookModel(boolean showBookMarkDialog) {
//        new GetBookByIdTask(pdfModel, new TaskRunner.Callback<PDFModel>() {
//            @Override
//            public void onComplete(PDFModel response) {
//                if (response != null) {
//                    pdfModel.setBookmarkPages(response.getBookmarkPages());
//                    if (showBookMarkDialog)
//                        showBookMarkDialog();
//                } else {
//                    pdfModel.setBookmarkPages(null);
//                }
//            }
//        }).execute(this);
//    }
//
//    private void showFullScreenMode(boolean isFullScreen) {
//        if (getSupportActionBar() != null) {
//            if (isFullScreen) {
//                getSupportActionBar().hide();
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            } else {
//                getSupportActionBar().show();
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            }
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (getSupportActionBar() != null && !getSupportActionBar().isShowing()) {
//            showFullScreenMode(false);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//
//    private void bookmarkCurrentPage() {
//        if (PDFSupportPref.isShowBookmarkPageAlert(this)) {
//            AlertUtil.openAlertDialog(this, PDFConstant.DIALOG_INITIAL_BOOKMARK_PAGE_TITLE, PDFConstant.DIALOG_INITIAL_BOOKMARK_PAGE_MESSAGE, "OK", new PDFCallback.OnClickListener<Void>() {
//                @Override
//                public void onItemClicked(@Nullable View view, Void item) {
//                    bookmarkCurrentPage(getCurrentPage(pdfView.getCurrentPage()));
//                    PDFSupportPref.setShowBookmarkPageAlert(PDFViewerActivity3.this, false);
//                }
//            });
//        } else {
//            bookmarkCurrentPage(getCurrentPage(pdfView.getCurrentPage()));
//        }
//    }
//
//    private void bookmarkCurrentPage(final int currentPage) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
//        //Setting message manually and performing action on button click
//        builder.setMessage("Do you want to bookmark page " + currentPage + " ?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        if (pdfModel != null) {
//                            new UpdateBookmarkPage(UpdateBookmarkPage.getValidBookmark(currentPage, pdfModel), pdfModel, new TaskRunner.Callback<Boolean>() {
//                                @Override
//                                public void onComplete(Boolean result) {
//                                    updateBookModel(false);
//                                }
//
//                            }).execute(PDFViewerActivity3.this);
//                        }
//                        dialog.dismiss();
//                        dialog.cancel();
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        //  Action for 'NO' Button
//                        dialog.dismiss();
//                        dialog.cancel();
//                    }
//                });
//        //Creating dialog box
//        AlertDialog alert = builder.create();
//        alert.setTitle("Alert!");
//        alert.show();
//    }
//
//
//    @Override
//    public void onError(Throwable t) {
//        showError();
//    }
//
//    @Override
//    public void onPageChanged(int page, int pageCount) {
//        seekBar.setProgress(page);
//        updateProgressCount();
//        updatePageCount(page);
//    }
//
//    @Override
//    public void onPageError(int page, Throwable t) {
//
//    }
//
//    @Override
//    public void loadComplete(int nbPages) {
//        setResult(RESULT_OK);
//        updatePageCount(nbPages);
//        setSeekBarSize(pdfView.getPageCount());
//    }
//
//
//    private void openTableOfContent() {
//        PDFIndexFragment.newInstance(pdfView.getTableOfContents(), pdfView.getPageCount(), new Response.OnClickListener<Integer>() {
//            @Override
//            public void onItemClicked(View view, Integer index) {
//                loadPage(index);
//            }
//        }).show(this);
//    }
//
//    private void loadPage(int pageIdx) {
//        try {
//            pdfView.jumpTo(pageIdx);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void updatePageCount(int currentPage) {
//        if (menuCount != null && pdfView != null) {
//            int totalPageCount = pdfView.getPageCount();
//            menuCount.setTitle(getCurrentPage(currentPage) + "/" + totalPageCount);
//            menuCount.setVisible(true);
//        }
//    }
//}
