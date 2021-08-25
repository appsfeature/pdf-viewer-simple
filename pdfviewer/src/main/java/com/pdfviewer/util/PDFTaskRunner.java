package com.pdfviewer.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PDFTaskRunner {
    private final Executor executor = Executors.newCachedThreadPool(); // change according to your requirements
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static PDFTaskRunner taskRunner ;

    public Executor getExecutor() {
        return executor;
    }

    public Handler getHandler() {
        return handler;
    }

    private PDFTaskRunner() {
    }

    public static PDFTaskRunner getInstance(){
        if ( taskRunner == null ){
            taskRunner = new PDFTaskRunner();
        }
        return taskRunner ;
    }

    public interface Callback<R> {
        void onCancelled();
        void onProgressUpdate(int progress);
        void onComplete(R result);
    }

    public interface CallbackWithError<R> {
        void onComplete(R result);
        void onError(Exception e);
    }

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback) {
        executor.execute(() -> {
            try {
                final R result = callable.call();
                handler.post(() -> {
                    if (callback != null) {
                        callback.onComplete(result);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    if (callback != null) {
                        callback.onComplete(null);
                    }
                });
            }
        });
    }

    public <R> void executeAsync(Callable<R> callable, CallbackWithError<R> callback) {
        executor.execute(() -> {
            try {
                final R result = callable.call();
                handler.post(() -> {
                    if (callback != null) {
                        callback.onComplete(result);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    if (callback != null) {
                        callback.onComplete(null);
                    }
                });
            }
        });
    }

    public <R> void executeAsync(Callable<R> callable) {
        executor.execute(() -> {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
