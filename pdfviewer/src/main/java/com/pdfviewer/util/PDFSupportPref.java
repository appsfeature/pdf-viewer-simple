package com.pdfviewer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by Amit on 1/7/2017.
 */

public class PDFSupportPref {

    private static final String SHOW_BOOKMARK_PAGE_ALERT = "show_bookmark_page_alert";
    private static final String DOWNLOAD_DIRECTORY = "download_directory";
    public static final String HEADER_AUTH = "Authorization";
    public static final String HEADER_AUTH_ENC = "Authorization-Enc";
    public static final String ENABLE_FILE_STREAM_PATH = "enable_file_stream_path";
    public static final String PDF_STATS_DATA = "pdf_stats_data";
    public static final String STORAGE_MIGRATION_COMPLETED = "storage_migration_completed";

    private static SharedPreferences sharedPreferences;

    private static SharedPreferences getSharedPreferenceObj(Context context) {
        if (sharedPreferences == null && context != null)
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        return sharedPreferences;
    }

    public static String getPdfStatsData(Context context) {
        return getSharedPreferenceObj(context).getString(PDF_STATS_DATA, "");
    }

    public static void setPdfStatsData(Context context, String value) {
        getSharedPreferenceObj(context).edit().putString(PDF_STATS_DATA, value).commit();
    }

    public static String getHeaderAuth(Context context) {
        return getSharedPreferenceObj(context).getString(HEADER_AUTH, "" );
    }

    public static String getHeaderAuthEnc(Context context) {
        return getSharedPreferenceObj(context).getString(HEADER_AUTH_ENC, "" );
    }

    public static void setHeaderAuth(Context context, String value) {
        getSharedPreferenceObj(context).edit().putString(HEADER_AUTH, value).commit();
    }

    public static void setHeaderAuthEnc(Context context, String value) {
        getSharedPreferenceObj(context).edit().putString(HEADER_AUTH_ENC, value).commit();
    }

    public static String getString(Context context, String key) {
        return getSharedPreferenceObj(context).getString(key, "");
    }

    public static void setShowBookmarkPageAlert(Context context, boolean value) {
        getSharedPreferenceObj(context).edit().putBoolean(SHOW_BOOKMARK_PAGE_ALERT, value).apply();
    }

    public static boolean isShowBookmarkPageAlert(Context context) {
        return getSharedPreferenceObj(context).getBoolean(SHOW_BOOKMARK_PAGE_ALERT,true);
    }

    public static void setEnableFileStreamPath(Context context, boolean value) {
        getSharedPreferenceObj(context).edit().putBoolean(ENABLE_FILE_STREAM_PATH, value).apply();
    }

    public static boolean isEnableFileStreamPath(Context context) {
        return getSharedPreferenceObj(context).getBoolean(ENABLE_FILE_STREAM_PATH,true);
    }

    public static void setStorageMigrationCompleted(Context context, boolean value) {
        getSharedPreferenceObj(context).edit().putBoolean(STORAGE_MIGRATION_COMPLETED, value).apply();
    }

    public static boolean isStorageMigrationCompleted(Context context) {
        return getSharedPreferenceObj(context).getBoolean(STORAGE_MIGRATION_COMPLETED,false);
    }

    public static void setDownloadDirectory(Context context, String value) {
        getSharedPreferenceObj(context).edit().putString(DOWNLOAD_DIRECTORY, value).apply();
    }

    public static String getDownloadDirectory(Context context) {
        return getSharedPreferenceObj(context).getString(DOWNLOAD_DIRECTORY, PDFConstant.DEFAULT_DOWNLOAD_DIRECTORY);
    }

    private static void setString(Context context, String key, String value) {
        if (getSharedPreferenceObj(context) != null && !TextUtils.isEmpty(key)) {
            final SharedPreferences.Editor editor = getSharedPreferenceObj(context).edit();
            if (editor != null) {
                editor.putString(encrypt(key), encrypt(value));
                editor.apply();
            }
        }
    }

    private static void setInteger(Context context, String key, Integer value) {
        if (getSharedPreferenceObj(context) != null && !TextUtils.isEmpty(key)) {
            final SharedPreferences.Editor editor = getSharedPreferenceObj(context).edit();
            if (editor != null) {
                editor.putInt(encrypt(key), value);
                editor.apply();
            }
        }
    }

    private static String encrypt(String input) {
        // This is base64 encoding, which is not an encryption
        return input;
//        if (ConfigUtil.isEmptyOrNull( input )) {
//            return input ;
//        } else {
//            return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
//        }
    }

}
