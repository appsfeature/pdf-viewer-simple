package com.pdfviewer.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.helper.task.TaskRunner;
import com.helper.util.Logger;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.R;
import com.pdfviewer.network.DownloadManager;
import com.pdfviewer.task.TaskMigrateScopedStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class PDFFileUtil {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void doPrint(Activity activity, final String fileName, final File fileToPrint) {
        PdfUtil.doPrint(activity, fileName, fileToPrint);
    }

    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        if (!filePath.contains("/")) {
            return filePath;
        }
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    public static String getFileNameFromUrl(String fileUrl) {
        String fileName = null;
        try {
            if (!TextUtils.isEmpty(fileUrl) && PdfUtil.isValidUrl(fileUrl)) {
                if (fileUrl.contains(".")) {
                    fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.lastIndexOf("."));
                } else {
                    fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                }
            }
            if (TextUtils.isEmpty(fileName)) {
                fileName = "Error-" + System.currentTimeMillis();
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error-" + System.currentTimeMillis();
        }
    }

    public static void deleteFile(Context context, String fileName) {
        try {
            if (!TextUtils.isEmpty(fileName)) {
                File apkStorage = null;
                if (DownloadManager.isSDCardPresent()) {
//                    apkStorage = new File(PDFFileUtil.getFileStoreDirectory(context) + "/" + PDFSupportPref.getDownloadDirectory(context));
                    apkStorage = PDFFileUtil.getFileStoreDirectory(context);
                }
                if (apkStorage != null && apkStorage.exists()) {
                    File outputFile = new File(apkStorage, fileName);
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isFileExists(Context context, String fileName) {
        boolean isFileExixts = false;
        try {
            if (!TextUtils.isEmpty(fileName)) {
                File apkStorage = null;
                if (DownloadManager.isSDCardPresent()) {
//                    apkStorage = new File(PDFFileUtil.getFileStoreDirectory(context) + "/" + PDFSupportPref.getDownloadDirectory(context));
                    apkStorage = PDFFileUtil.getFileStoreDirectory(context);
                }
                if (apkStorage != null && apkStorage.exists()) {
                    File outputFile = new File(apkStorage, fileName);
                    isFileExixts = outputFile.exists();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isFileExixts;
    }

    public static List<String> getStorageFileList(Context context) {
        List<String> result = new ArrayList<>();
        try {
            File path = getFileStoreDirectory(context);
            String[] storageFileList = path.list();
            if (storageFileList != null) {
                result.addAll(Arrays.asList(storageFileList));
            }
            if (PDFViewer.getInstance().isDebugModeEnabled()) {
                PdfUtil.log("getFileStoreDirectory : " + path.getAbsolutePath());
            }
            if (TextUtils.isEmpty(PDFSupportPref.getDownloadDirectory(context))) {
                File pdfViewerPath = getFile(context, "PDFViewer");
                if (pdfViewerPath.exists()) {
                    String[] pdfViewerFileList = pdfViewerPath.list();
                    if (pdfViewerFileList != null) {
                        result.addAll(Arrays.asList(pdfViewerFileList));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    public static void initStorageFileMigrationOnApiLevel29(Context context) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            if(!PDFSupportPref.isStorageMigrationCompleted(context)) {
                TaskRunner.getInstance().executeAsync(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        copyFiles(context);
                        PDFSupportPref.setStorageMigrationCompleted(context, true);
                        new TaskMigrateScopedStorage(context).execute();
                        return null;
                    }
                });
            }
        }else {
            PDFSupportPref.setStorageMigrationCompleted(context, true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void copyFiles(Context context) {
        File destination = PDFFileUtil.getFileStoreDirectory(context);
        boolean isDeleteFile = !TextUtils.isEmpty(PDFSupportPref.getDownloadDirectory(context));
        final String filePath = Environment.getExternalStorageDirectory() + "/" + PDFSupportPref.getDownloadDirectory(context);
        File sourceDirectory = new File(filePath);
        File[] files = sourceDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if(file.isFile()) {
                    File mDestination = new File(destination, file.getName());
                    try {
                        FileUtils.copy(new FileInputStream(file), new FileOutputStream(mDestination));
                        if (isDeleteFile && file.exists()) {
                            file.delete();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (isDeleteFile && sourceDirectory.exists()) {
                sourceDirectory.delete();
            }
        }
    }

    public static File getFileStoreDirectory(Context context) {
        if (isSupportLegacyExternalStorage()) {
            final String filePath = Environment.getExternalStorageDirectory() + "/" + PDFSupportPref.getDownloadDirectory(context);
            return new File(filePath);
        } else {
            if (PDFViewer.isEnableFileStreamPath(context)) {
                return context.getFilesDir();
            } else {
                return context.getExternalFilesDir("");
            }
        }
    }

    public static boolean isSupportLegacyExternalStorage() {
//        return Build.VERSION.SDK_INT < Build.VERSION_CODES.R;
        return Build.VERSION.SDK_INT < 29;
    }

    public static File getFile(Context context, String fileName) {
        if (isSupportLegacyExternalStorage()) {
            return new File(getFileStoreDirectory(context), fileName);
        } else {
            if (PDFViewer.isEnableFileStreamPath(context)) {
                return context.getFileStreamPath(fileName);//Check openFileOutput outputStream in DownloadManager
            } else {
                return new File(getFileStoreDirectory(context), fileName);
            }
        }
    }

    public static boolean shouldAskPermissions(Context context) {
        if (context != null && isSupportLegacyExternalStorage()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                return context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
            }
        } else {
            return false;
        }
        return false;
    }

    public static Uri getUriFromFile(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + context.getString(R.string.file_provider), file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }
}
