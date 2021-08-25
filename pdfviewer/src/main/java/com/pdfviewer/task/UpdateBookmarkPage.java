package com.pdfviewer.task;

import android.content.Context;
import android.text.TextUtils;

import com.helper.task.TaskRunner;
import com.helper.util.BaseUtil;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.util.ArraysUtil;
import com.pdfviewer.util.PdfUtil;

import java.util.concurrent.Callable;

public class UpdateBookmarkPage {
    private String bookmarkPages;
    private PDFModel book;
    private TaskRunner.Callback<Boolean> callback;

    private boolean status = false;

    public UpdateBookmarkPage(String bookmarkPages, PDFModel book, TaskRunner.Callback<Boolean> callback) {
        this.bookmarkPages = bookmarkPages;
        this.book = book;
        this.callback = callback;
    }


    public void execute(Context context) {
        TaskRunner.getInstance().executeAsync(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    if (PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().getPdfById(book.getId(), book.getTitle()) != null) {
                        PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().updateBookmarkPages(
                                book.getId(), book.getTitle(),
                                ArraysUtil.shortStringArrayList(bookmarkPages), book.getOpenPagePosition(),
                                PdfUtil.getDatabaseDateTime()
                        );
                    } else {
                        book.setBookmarkPages(ArraysUtil.shortStringArrayList(bookmarkPages));
                        book.setUpdated_at(PdfUtil.getDatabaseDateTime());
                        book.setLastUpdate(PdfUtil.getDatabaseDateTime());
                        PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().insertOnlySingleRecord(
                                book
                        );
                    }
                    status = true;
                } catch (Exception e) {
                    status = false;
                }
                return status;
            }
        }, new TaskRunner.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                callback.onComplete(result);
                PDFViewer.getInstance().updateBookmarkUpdateListener(book.getId(), bookmarkPages);
            }
        });

    }

    public static String getValidBookmark(int bookmarkPages, PDFModel mBook) {

        if (TextUtils.isEmpty(mBook.getBookmarkPages())) {
            return String.valueOf(bookmarkPages);
        } else {
            try {
                if (mBook.getBookmarkPages().contains(",")) {
                    String[] tempArray = mBook.getBookmarkPages().split(",");
                    for (String it : tempArray) {
                        if (it.trim().equalsIgnoreCase(String.valueOf(bookmarkPages))) {
                            return mBook.getBookmarkPages();
                        }
                    }
                } else if (mBook.getBookmarkPages().contains(String.valueOf(bookmarkPages))) {
                    return mBook.getBookmarkPages();
                }
                return mBook.getBookmarkPages() + ", " + bookmarkPages;
            } catch (Exception e) {
                return mBook.getBookmarkPages();
            }
        }
    }
}
