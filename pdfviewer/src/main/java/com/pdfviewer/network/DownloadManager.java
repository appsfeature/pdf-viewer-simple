package com.pdfviewer.network;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.config.config.ApiInterface;
import com.config.config.ConfigConstant;
import com.config.config.ConfigManager;
import com.config.config.ConfigPreferences;
import com.config.network.download.ConfigDownloadListener;
import com.config.util.ConfigUtil;
import com.config.util.Logger;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.R;
import com.pdfviewer.util.PDFFileUtil;
import com.pdfviewer.util.PDFSupportPref;
import com.pdfviewer.util.PDFTaskRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DownloadManager {

    //    private static final String DOWNLOAD_DIRECTORY = PDFViewer.DownloadDirectory;
    private final Progress progress;
    private Context context;
    private String endPoint = "";

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public DownloadManager(Context context) {
        this.context = context;
        this.progress = (Progress) context;
        this.setEndPoint(PDFViewer.getInstance().getBaseUrl());
        if (ConfigManager.getInstance() != null) {
            ConfigManager.getInstance().setDownloadListener(new ConfigDownloadListener() {
                @Override
                public void onProgressUpdate(int mProgress) {
                    if(progress != null){
                        progress.onProgressUpdate(mProgress);
                    }
                }
            });
        }
    }

    public void loadFileIfExists(String fileName) {
        if (fileName == null) {
            progress.onDownloadingError(new Exception("Invalid file name."));
            return;
        }
        this.fileName = fileName;
        if (PDFFileUtil.shouldAskPermissions(context)) {
            askPermissions();
        } else {
            afterPermissionCode();
        }
    }

//    private DownloadFileFromURL downloadFileAsyncTask;

    public void downloadFile(String fileUrl, String statistics) {
        if (fileUrl == null) {
            progress.onDownloadingError(new Exception("Invalid file name."));
            return;
        } else if (TextUtils.isEmpty(fileUrl)) {
            progress.onDownloadingError(new Exception("Invalid file url."));
            return;
        }
        if (!isNotConnected(context)) {
            downloadFileRetrofit(fileUrl, statistics);
//            downloadFileAsyncTask = new DownloadFileFromURL();
//            downloadFileAsyncTask.execute(fileUrl);
        } else
            progress.onDownloadingError(new Exception("No internet connection."));
    }

    private boolean isCancelDownload = false;

    public void cancelDownload() {
        isCancelDownload = true;
        if ( executor != null ) {
            executor.shutdownNow();
        }

//        if (downloadFileAsyncTask != null) {
//            downloadFileAsyncTask.cancel(true);
//        }
    }

    public interface Progress {
        void onProgressManager(boolean isVisible);

        void onShowAdsInUi();

        void onDownloadedFileStatus(boolean isFileExists);

        void onProgressUpdate(int progress);

        void onFileDownloaded(File file, Uri fileUri, String ext, String type, Boolean isFileAlreadyDownloaded);

        void onDownloadingError(Exception e);

        void onDownloadingCanceled();
    }

    public void setEndPoint(String endPoint) {
        if(!TextUtils.isEmpty(endPoint)) {
            this.endPoint = endPoint;
        }
    }

    private void downloadFileRetrofit(String fileUrl, String statisticsJson) {
        progress.onProgressManager(true);
        if (ConfigManager.getInstance() != null) {
            Map<String, String> map = new HashMap<>();

            ApiInterface apiInterface = ConfigManager.getInstance().getHostInterfaceSaved(endPoint, true);
            if (apiInterface != null) {
                apiInterface.downloadPDFFileWithDynamicUrlAsync(fileUrl, map).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                        String fileSizeString = response.headers().get("Content-Length-X");
                        long fileSize = -1 ;
                        if ( !TextUtils.isEmpty(fileSizeString) ){
                            try {
                                fileSize = Long.parseLong(fileSizeString);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        if (response.isSuccessful()) {
                            handleResponseBody(response.body() , fileSize);
                        } else {
                            progress.onDownloadingError(new Exception("Api Failed"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progress.onDownloadingError(new Exception(t.getMessage()));
                    }
                });
            }else {
                progress.onDownloadingError(new Exception("Invalid Interface"));
            }
        }
    }

    private ExecutorService executor ;
    private void handleResponseBody(ResponseBody body , long fileSize) {
        executor = Executors.newCachedThreadPool();
        Handler handler = PDFTaskRunner.getInstance().getHandler();
        executor.execute(() -> {
            try {
                boolean isCompleteDownload = writeResponseBodyToDisk(body);
                handler.post(() -> {
                    onFileDownloadComplete(isCompleteDownload ? "success" : "failure");
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    onFileDownloadComplete(e.getMessage());
                });
            }
        });
    }

    private void onFileDownloadComplete(String result){
        if (progress != null) {
            progress.onProgressManager(false);
            if (!isCancelDownload) {
                if (result.equalsIgnoreCase("success")) {
                    downloadFinished(false);
                } else {
                    progress.onDownloadingError(new Exception(result));
                }
            } else {
                progress.onDownloadingCanceled();
            }
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
//            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");


            File apkStorage = null;
            if (isSDCardPresent()) {
                apkStorage = PDFFileUtil.getFileStoreDirectory(context);
            }
            //If File is not present create directory
            if (apkStorage != null && !apkStorage.exists()) {
                apkStorage.mkdir();
                // Log.e(TAG, "Directory Created.");
            }
            File futureStudioIconFile = new File(apkStorage, fileName);//Create Output file in Main File
            //Create New File if not present
            if (!futureStudioIconFile.exists()) {
                futureStudioIconFile.createNewFile();
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

//                if ( fileSize < 1 ) {
//                    fileSize = body.contentLength();
//                }
//                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                if (PDFFileUtil.isSupportLegacyExternalStorage()) {
                    outputStream = new FileOutputStream(futureStudioIconFile);
                } else {
                    outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                }

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

//                    fileSizeDownloaded += read;
//                    long finalFileSizeDownloaded = fileSizeDownloaded;
//                    if ( handler != null ) {
//                        try {
//                            long finalFileSize = fileSize;
//                            handler.post(() -> { progress.onProgressUpdate((int) ((finalFileSizeDownloaded * 100) / finalFileSize)); });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                if (e.getMessage() != null) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());
                }
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            if (e.getMessage() != null) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            }
            return false;
        }
    }

//    private class DownloadFileFromURL extends AsyncTask<String, Integer, String> {
//
//        private boolean isCanceled = false;
//
//        private ResponseBody body;
//
//        public DownloadFileFromURL(ResponseBody body) {
//            this.body = body;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progress.onProgressManager(true);
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            new DeleteFileTask(fileName, null).execute(context);
//            isCanceled = true;
//        }
//
//        @Override
//        protected String doInBackground(String... f_url) {
//            boolean isCompleteDownload = writeResponseBodyToDisk(new Handler(), body , 0);
//            return isCompleteDownload ? "success" : "failure";
//        }
//
//        public void onProgressUpdateFile(int i) {
//            publishProgress(i);
//        }
//
//        protected void onProgressUpdate(Integer... pro) {
//            // setting progress percentage
//            progress.onProgressUpdate(pro[0]);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (progress != null) {
//                progress.onProgressManager(false);
//                if (!isCanceled) {
//                    if (result.equalsIgnoreCase("success")) {
//                        downloadFinished();
//                    } else {
//                        progress.onDownloadingError(new Exception(result));
//                    }
//                } else {
//                    progress.onDownloadingCanceled();
//                }
//            }
//        }
//    }
/*
    private class DownloadFileFromURL extends AsyncTask<String, Integer, String> {

        private boolean isCanceled = false;

        private ResponseBody body;

        public DownloadFileFromURL(ResponseBody body) {
            this.body = body;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.onProgressManager(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            new DeleteFileTask(fileName, null).execute(context);
            isCanceled = true;
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);

                File apkStorage = null;
                if (isSDCardPresent()) {
                    apkStorage = PDFFileUtil.getFileStoreDirectory(context);
                }
                //If File is not present create directory
                if (apkStorage != null && !apkStorage.exists()) {
                    apkStorage.mkdir();
                    // Log.e(TAG, "Directory Created.");
                }
                File outputFile = new File(apkStorage, fileName);//Create Output file in Main File
                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                if ( context != null ){
                    String auth = PDFSupportPref.getHeaderAuth(context);
                    String authEnc = PDFSupportPref.getHeaderAuthEnc(context);
                    if ( !TextUtils.isEmpty( auth ) ){
                        connection.setRequestProperty( PDFSupportPref.HEADER_AUTH , auth );
                    }
                    if ( !TextUtils.isEmpty( authEnc ) ){
                        connection.setRequestProperty( PDFSupportPref.HEADER_AUTH_ENC , authEnc );
                    }
                }

                connection.connect();
                int lengthOfFile = connection.getContentLength();
                // loadFileIfExists the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                // Output stream
                OutputStream output;
                if (PDFFileUtil.isSupportLegacyExternalStorage()) {
                    output = new FileOutputStream(outputFile);
                } else {
                    output = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                }
                byte[] data = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                connection.disconnect();
            } catch (Exception e) {
                //Log.e("Error: ", e.getMessage());
                return "FileName:-" + fileName + e.toString();
            }

            return "success";
        }

        protected void onProgressUpdate(Integer... pro) {
            // setting progress percentage
            progress.onProgressUpdate(pro[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (progress != null) {
                progress.onProgressManager(false);
                if (!isCanceled) {
                    if (result.equalsIgnoreCase("success")) {
                        downloadFinished();
                    } else {
                        progress.onDownloadingError(new Exception(result));
                    }
                } else {
                    progress.onDownloadingCanceled();
                }
            }
        }
    }*/


    public static boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    public void askPermissions() {
        TedPermission.with(context)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            afterPermissionCode();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };


    private void afterPermissionCode() {
//        String filePath = Environment.getExternalStorageDirectory() + "/" + PDFSupportPref.getDownloadDirectory(context) + "/" + fileName;
//        File file = new File(filePath);
        if (checkFileIsCompleteDownloaded(fileName)) {
            progress.onDownloadedFileStatus(true);
            downloadFinished(true);
            progress.onProgressManager(false);
        } else {
            progress.onProgressManager(false);
            progress.onShowAdsInUi();
            progress.onDownloadedFileStatus(false);
        }
    }


    private void downloadFinished(Boolean isFileAlreadyDownloaded) {
        File file = getFile(PDFFileUtil.getFile(context, fileName));

        if (file.exists()) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            String type = mime.getMimeTypeFromExtension(ext);

            Uri fileUri = getUriFromFile(file);
            progress.onFileDownloaded(file, fileUri, ext, type, isFileAlreadyDownloaded);
        }else {
            if(TextUtils.isEmpty(PDFSupportPref.getDownloadDirectory(context))){
                File pdfViewerPath = getFile(new File(PDFFileUtil.getFile(context, "PDFViewer"), fileName));
                if(pdfViewerPath.exists()) {
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String ext = pdfViewerPath.getName().substring(pdfViewerPath.getName().lastIndexOf(".") + 1);
                    String type = mime.getMimeTypeFromExtension(ext);

                    Uri fileUri = getUriFromFile(pdfViewerPath);
                    progress.onFileDownloaded(pdfViewerPath, fileUri, ext, type, isFileAlreadyDownloaded);
                }
            }
        }
    }

    private Uri getUriFromFile(File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + context.getString(R.string.file_provider), file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    public static void grantAllUriPermissions(Context context, Intent intent, Uri uri) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private boolean isNotConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return !isConnected;
    }

    private boolean checkFileIsCompleteDownloaded(String fileName) {
        try {
            List<String> fileList = PDFFileUtil.getStorageFileList(context);
            if (fileList != null) {
                boolean isContain = fileList.contains(fileName);
                if (!isContain) {
                    String file = fileName.endsWith(".pdf")
                            ? fileName.replace(".pdf", "")
                            : fileName + ".pdf";
                    return fileList.contains(file);
                } else
                    return isContain;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private File getFile(File file) {
        String filePath = file.getAbsolutePath();
        if (file.exists()) {
            return file;
        } else {
            String filePathNew = filePath.endsWith(".pdf")
                    ? filePath.replace(".pdf", "")
                    : filePath + ".pdf";
            file = new File(filePathNew);
        }
        return file;
    }

    private boolean checkFileIsCompleteDownloaded(File file) {
        return file.exists();
    }

}
