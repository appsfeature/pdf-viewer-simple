package com.pdfviewer.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import com.helper.util.BaseUtil;
import com.helper.util.Logger;
import com.pdfviewer.BuildConfig;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.R;
import com.pdfviewer.model.PDFModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PdfUtil extends BaseUtil {

    public static boolean isNightMode() {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
    }

    public static void initBannerAd(Activity context, RelativeLayout relativeLayout) {
//        if (context != null && relativeLayout != null && AdsSDK.getInstance() != null) {
//            AdsSDK.getInstance().setAdoptiveBannerAdsOnView(relativeLayout, context);
//        } else {
//            if (context != null) {
//                initAds(context);
//                if (relativeLayout != null && AdsSDK.getInstance() != null) {
//                    AdsSDK.getInstance().setAdoptiveBannerAdsOnView(relativeLayout, context);
//                }
//            }
//        }
    }

    public static PDFModel getPdfModel(int id, String pdfTitle, String pdfFileName, String subTitle, String bookmarkPages, Uri fileUri) {
        PDFModel pdfModel = new PDFModel();
        pdfModel.setId(id);
        pdfModel.setTitle(pdfTitle);
        pdfModel.setPdf(pdfFileName);
        pdfModel.setSubTitle(subTitle);
        pdfModel.setBookmarkPages(bookmarkPages);
        pdfModel.setFilePath(fileUri.toString());
        return pdfModel;
    }


    public static void loadNativeAds(Activity activity, RelativeLayout rlNativeAd, int layoutRes, boolean isSmall) {
//        if (activity != null && rlNativeAd != null && AdsSDK.getInstance() != null) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    NativeAdsUIUtil.bindUnifiedNativeAd(activity, getNative(rlNativeAd, layoutRes), isSmall);
//                }
//            }, 10);
//        } else {
//            if (activity != null) {
//                initAds(activity);
//                if (rlNativeAd != null && AdsSDK.getInstance() != null) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            NativeAdsUIUtil.bindUnifiedNativeAd(activity, getNative(rlNativeAd, layoutRes), isSmall);
//                        }
//                    }, 10);
//                }
//            }
//        }
    }
//
//    private static NativeUnifiedAdsViewHolder getNative(RelativeLayout rlNativeAd, int layoutRes) {
//        View view = LayoutInflater.from(rlNativeAd.getContext()).inflate(layoutRes, null, false);
//        rlNativeAd.removeAllViews();
//        rlNativeAd.addView(view);
//        return new NativeUnifiedAdsFullViewHolder(rlNativeAd);
//    }
//
//
//    static private AdsSDK adsSDK;
//
//    public static void initAds(Activity context) {
//        if (!BuildConfig.DEBUG) {
//            adsSDK = new AdsSDK(context.getApplication(), BuildConfig.DEBUG, context.getPackageName());
//        }
//    }

    public static void showToast(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static Integer paresInt(String value) {
        try {
            if (value == null) {
                return 0;
            }
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getDateTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
    }

    public static String getDatabaseDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public static void shareImageWithImagePath(Context context, String message, Uri imageUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (message != null) {
            String appLink = message + "\nDownload " + context.getString(R.string.app_name) + " app. \nLink : http://play.google.com/store/apps/details?id=" + context.getPackageName();
            shareIntent.putExtra(Intent.EXTRA_TEXT, appLink);
        }
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(shareIntent, "Share Using"));
    }

    public static Uri getUriForFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + context.getString(R.string.file_provider), file);
        } else {
            return Uri.fromFile(file);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void doPrint(Activity activity, final String fileName, final File fileToPrint) {
        doPrint(activity, fileName, fileToPrint.getPath());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void doPrint(Activity activity, final String fileName, final String filePath) {
        if(activity != null && fileName != null && filePath != null) {
            PrintDocumentAdapter pda = new PrintDocumentAdapter() {
                @Override
                public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                    InputStream input = null;
                    OutputStream output = null;
                    try {
//                        input = new FileInputStream(fileToPrint);
                        input = activity.getContentResolver().openInputStream(Uri.parse(filePath));
                        output = new FileOutputStream(destination.getFileDescriptor());
                        byte[] buf = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = input.read(buf)) > 0) {
                            output.write(buf, 0, bytesRead);
                        }
                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                    } catch (FileNotFoundException ee) {
                        //Catch exception
                        ee.printStackTrace();
                    } catch (Exception e) {
                        //Catch exception
                        e.printStackTrace();
                    } finally {
                        try {
                            if (input != null && output != null) {
                                input.close();
                                output.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                    if (cancellationSignal.isCanceled()) {
                        callback.onLayoutCancelled();
                        return;
                    }
                    PrintDocumentInfo pdi = new PrintDocumentInfo.Builder(fileName).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                    callback.onLayoutFinished(pdi, true);
                }
            };

            try {
                PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
                String jobName = activity.getString(R.string.app_name) + " Document";
                printManager.print(jobName, pda, null);
            } catch (NullPointerException e) {
                Logger.e("Printer option not support in this Android version");
            }
        }
    }

    public static void log(String message) {
        if(PDFViewer.getInstance().isDebugModeEnabled()){
            Log.d(PDFConstant.LOG_TAG, message);
        }
    }
}
