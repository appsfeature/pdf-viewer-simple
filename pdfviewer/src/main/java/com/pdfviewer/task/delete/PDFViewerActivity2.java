package com.pdfviewer.task.delete;//package com.pdfviewer.activity;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.WindowManager;
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
//import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
//import com.github.barteksc.pdfviewer.scroll.ScrollHandle;
//import com.github.barteksc.pdfviewer.util.FitPolicy;
//import com.helper.callback.Response;
//import com.helper.task.TaskRunner;
//import com.pdfviewer.R;
//import com.pdfviewer.fragment.PDFIndexFragment;
//import com.pdfviewer.model.PDFModel;
//import com.pdfviewer.task.GetBookByIdTask;
//import com.pdfviewer.task.UpdateBookmarkPage;
//import com.pdfviewer.util.AlertUtil;
//import com.pdfviewer.util.ArraysUtil;
//import com.pdfviewer.util.PDFCallback;
//import com.pdfviewer.util.PDFConstant;
//import com.pdfviewer.util.PDFSupportPref;
//import com.pdfviewer.util.PdfUtil;
//import com.pdfviewer.util.Screenshot;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//
//public class PDFViewerActivity2 extends PageAdsAppCompactActivity implements OnPageChangeListener, OnErrorListener
//        , OnPageErrorListener, OnLoadCompleteListener {
//
//    public static final String TAG = "pdfIndexFragment";
//    private PDFView pdfView;
//    private PDFModel pdfModel;
//    private TextView tvPageCount;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pdf_viewer_activity);
//        initView();
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//        initDataFromIntent();
//
//        PdfUtil.initBannerAd(this, findViewById(R.id.rl_ads));
//    }
//
//    private void initView() {
//        pdfView = findViewById(R.id.pdfView);
//        pdfView.setBackgroundColor(ContextCompat.getColor(this, R.color.themeWindowBackground));
//        tvPageCount = findViewById(R.id.tv_page_count);
//        tvPageCount.setVisibility(View.GONE);
//    }
//
//    private void initDataFromIntent() {
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            pdfModel = (PDFModel) bundle.getSerializable(PDFConstant.EXTRA_PROPERTY);
//            if (pdfModel != null) {
//                loadPdf(pdfModel.getFileUri(), pdfModel.getOpenPagePosition());
//                updateBookModel();
//            } else {
//                PdfUtil.showToast(this, PDFConstant.INVALID_PROPERTY);
//                finish();
////                openSample();
//            }
//        } else {
//            PdfUtil.showToast(this, PDFConstant.INVALID_PROPERTY);
//            finish();
//        }
//
//    }
//
//    private void openSample() {
//        pdfModel = new PDFModel();
//        pdfModel.setBookmarkPages("1, 2");
//        pdfModel.setTitle("Sample");
//        try {
//            InputStream stream = getAssets().open("sample2.pdf");
//            loadPdfFromAssets(stream, 0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadPdf(String mFileUri, int initialPage) {
//        if (mFileUri != null && pdfView != null) {
//            Uri fileUri = Uri.parse(mFileUri);
//            pdfView.fromUri(fileUri)
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
//                    .scrollHandle(getScrollHandler())
//                    .spacing(20)
//                    .onLoad(this)
//                    .load();
//        } else {
//            showError();
//        }
//    }
//
//    private void loadPdfFromAssets(InputStream fileName, int initialPage) {
//        if (fileName != null && pdfView != null) {
//            pdfView.fromStream(fileName)
//                    .defaultPage(setInitialPage(initialPage))
//                    .onPageChange(this)
//                    .onError(this)
//                    .pageFitPolicy(FitPolicy.WIDTH)
//                    .onPageError(this)
//                    .pageSnap(false)
//                    .nightMode(PdfUtil.isNightMode())
//                    .scrollHandle(getScrollHandler())
//                    .spacing(20)
//                    .onLongPress(new OnLongPressListener() {
//                        @Override
//                        public void onLongPress(MotionEvent e) {
//                            bookmarkCurrentPage();
//                        }
//                    })
//                    .onLoad(this)
//                    .load();
//        } else {
//            showError();
//        }
//    }
//
//    private ScrollHandle getScrollHandler() {
//        DefaultScrollHandle scrollHandler = new DefaultScrollHandle(this);
//        scrollHandler.setTextColor(Color.BLACK);
//        return scrollHandler;
//    }
//
//    private void drawOnView(Canvas canvas) {
//        canvas.drawPath(new Path(), getPaint());
//    }
//
//    private Paint getPaint() {
//        // Setup paint with color and stroke styles
//        Paint drawPaint = new Paint();
//        drawPaint.setColor(Color.BLACK);
//        drawPaint.setAntiAlias(true);
//        drawPaint.setStrokeWidth(5);
//        drawPaint.setStyle(Paint.Style.STROKE);
//        drawPaint.setStrokeJoin(Paint.Join.ROUND);
//        drawPaint.setStrokeCap(Paint.Cap.ROUND);
//        return drawPaint;
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
//        PdfUtil.showToast(this, "Error : Invalid PDF file.");
//        finish();
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.pdf_viewer_menu, menu);
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
//            closeTableOfContent();
//            bookmarkCurrentPage();
//            return true;
//        } else if (id == R.id.menu_bookmark) {
//            closeTableOfContent();
//            final List<String> pages = ArraysUtil.split(pdfModel.getBookmarkPages());
//            AlertUtil.openPageSelection(this, pdfModel, pages, new PDFCallback.OnClickListenerWithDelete<String>() {
//                @Override
//                public void onItemClicked(@Nullable View view, String item) {
//                    pdfModel.setOpenPagePosition(PdfUtil.paresInt(item));
////                    loadPdf(pdfModel.getFileUri(), pdfModel.getOpenPagePosition());
//                    loadPage(setInitialPage(pdfModel.getOpenPagePosition()));
//                }
//
//                @Override
//                public void onDeleteClicked(@Nullable View view, String bookmarkPages) {
//                    new UpdateBookmarkPage(bookmarkPages, pdfModel, new TaskRunner.Callback<Boolean>() {
//                        @Override
//                        public void onComplete(Boolean result) {
//                            updateBookModel();
//                        }
//
//                    }).execute(PDFViewerActivity2.this);
//                }
//
//                @Override
//                public void onUpdateUI() {
//                    updateBookModel();
//                }
//            });
//            return true;
//        } else if (id == R.id.menu_screenshot) {
//            closeTableOfContent();
//            Screenshot.takeScreenShot(pdfModel.getTitle(), pdfView, new TaskRunner.CallbackWithError<Uri>() {
//                @Override
//                public void onComplete(Uri result) {
//                    PdfUtil.shareImageWithImagePath(PDFViewerActivity2.this, pdfModel.getTitle(), result);
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    PdfUtil.showToast(PDFViewerActivity2.this, e.getMessage());
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
//    private void updateBookModel() {
//        new GetBookByIdTask(pdfModel, new TaskRunner.Callback<PDFModel>() {
//            @Override
//            public void onComplete(PDFModel response) {
//                if (response != null) {
//                    pdfModel.setBookmarkPages(response.getBookmarkPages());
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
//                    PDFSupportPref.setShowBookmarkPageAlert(PDFViewerActivity2.this, false);
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
//                        new UpdateBookmarkPage(UpdateBookmarkPage.getValidBookmark(currentPage, pdfModel), pdfModel, new TaskRunner.Callback<Boolean>() {
//                            @Override
//                            public void onComplete(Boolean result) {
//                                updateBookModel();
//                            }
//
//                        }).execute(PDFViewerActivity2.this);
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
////        updatePageCount(page);
//    }
//
//    @Override
//    public void onPageError(int page, Throwable t) {
//
//    }
//
//    @Override
//    public void loadComplete(int nbPages) {
////        updatePageCount(nbPages);
////        openTableOfContent();
//    }
//
//    private void closeTableOfContent() {
//        if (pdfIndexFragment != null) {
//            pdfIndexFragment.finish();
//            pdfIndexFragment = null;
//        }
//    }
//
//    private PDFIndexFragment pdfIndexFragment;
//
//    private void openTableOfContent() {
//        pdfIndexFragment = PDFIndexFragment.newInstance(pdfView.getTableOfContents(), pdfView.getPageCount(), new Response.OnClickListener<Integer>() {
//            @Override
//            public void onItemClicked(View view, Integer index) {
//                loadPage(index);
//            }
//        });
//        pdfIndexFragment.show(this);
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
//        if (tvPageCount != null && pdfView != null) {
//            int totalPageCount = pdfView.getPageCount();
//            tvPageCount.setText(getCurrentPage(currentPage) + "/" + totalPageCount);
////            tvPageCount.setVisibility(View.VISIBLE);
//        }
//    }
//}
