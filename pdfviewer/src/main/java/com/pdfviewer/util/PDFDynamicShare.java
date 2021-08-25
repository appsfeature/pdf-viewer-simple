package com.pdfviewer.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.reflect.TypeToken;
import com.helper.callback.Response;
import com.helper.task.TaskRunner;
import com.helper.util.BaseDynamicUrlCreator;
import com.helper.util.BaseUtil;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.R;
import com.pdfviewer.activity.PDFViewerActivity;
import com.pdfviewer.model.PDFModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.Callable;


public class PDFDynamicShare extends BaseDynamicUrlCreator {

    public static final String TYPE_PDF = "pdf";
    private static final String TAG = PDFDynamicShare.class.getSimpleName();

    private Response.Progress progressListener;

    public PDFDynamicShare(Context context) {
        super(context);
    }

    public void share(View view, String title, PDFModel pdfModel, int currentPage) {
        if (progressListener != null) {
            progressListener.onStartProgressBar();
        }
        Screenshot.takeScreenShot(title, view, new TaskRunner.Callback<Uri>() {
            @Override
            public void onComplete(Uri imagePath) {
                sharePdf(pdfModel, currentPage, imagePath);
            }
        });
    }

    public void sharePdf(PDFModel pdfModel, int currentPage, Uri imagePath) {
        pdfModel.setOpenPagePosition(currentPage);
        pdfModel.setFilePath(null);

        HashMap<String, String> params = new HashMap<>();
        params.put(ACTION_TYPE, TYPE_PDF);
        String extraData = toJson(pdfModel, new TypeToken<PDFModel>() {
        });
        generate(params, extraData, new DynamicUrlCallback() {
            @Override
            public void onDynamicUrlGenerate(String url) {
                if (progressListener != null) {
                    progressListener.onStopProgressBar();
                }
                shareMe(url, imagePath);
            }

            @Override
            public void onError(Exception e) {
                if (progressListener != null) {
                    progressListener.onStopProgressBar();
                }
                Log.d(TAG, "sharePdf:onError" + e.toString());
                shareMe(getPlayStoreLink(), imagePath);
            }
        });
    }

    @Override
    protected void onBuildDeepLink(@NonNull Uri deepLink, int minVersion, Context context, BaseDynamicUrlCreator.DynamicUrlCallback callback) {
        String uriPrefix = getDynamicUrl();
        if (!TextUtils.isEmpty(uriPrefix)) {
            DynamicLink.Builder builder = FirebaseDynamicLinks.getInstance()
                    .createDynamicLink()
                    .setLink(deepLink)
                    .setDomainUriPrefix(uriPrefix)
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                            .setMinimumVersion(minVersion)
                            .build());

            // Build the dynamic link
            builder.buildShortDynamicLink().addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                @Override
                public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                    if (task.isComplete() && task.isSuccessful() && task.getResult() != null
                            && task.getResult().getShortLink() != null) {
                        callback.onDynamicUrlGenerate(task.getResult().getShortLink().toString());
                    } else {
                        Log.d(TAG, "onBuildDeepLink:Error" + task.getException());
                        callback.onDynamicUrlGenerate(getPlayStoreLink());
                    }
                }
            });
        } else {
            callback.onError(new Exception("Invalid Dynamic Url"));
        }
    }

    private String getPlayStoreLink() {
        return "http://play.google.com/store/apps/details?id=" + context.getPackageName();
    }

    @Override
    protected void onDeepLinkIntentFilter(Activity activity) {
        if (activity != null && activity.getIntent() != null) {
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(activity.getIntent())
                    .addOnSuccessListener(activity, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData linkData) {
                            if(resultCallBack != null) {
                                if (linkData != null && linkData.getLink() != null) {
                                    resultCallBack.onDynamicUrlResult(linkData.getLink()
                                            , BaseDynamicUrlCreator.EncryptData.decode(linkData.getLink().getQueryParameter(PARAM_EXTRA_DATA)));
                                } else {
                                    resultCallBack.onError(new Exception("Invalid Dynamic Url"));
                                }
                            }
                        }
                    })
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (resultCallBack != null) {
                                resultCallBack.onError(e);
                            }
                        }
                    });
        }
    }

    public void addProgressListener(Response.Progress progressListener) {
        this.progressListener = progressListener;
    }

    public static void open(Activity activity, Uri url, String extraData) {
        if(url != null && url.getQueryParameter(ACTION_TYPE).equals(TYPE_PDF) && !TextUtils.isEmpty(extraData)) {
            PDFModel pdfModel = PDFDynamicShare.fromJson(extraData, new TypeToken<PDFModel>(){});
            PDFViewer.openPdfDownloadActivity(activity, pdfModel, false, false);
        }
    }

    public void shareMe(String deepLink, Uri imageUri) {
        if (BaseUtil.isValidUrl(deepLink)) {
            Log.d(TAG, deepLink);
            String text = "\nChick here to open : \n" + deepLink;

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            intent.setType("text/plain");
            if(imageUri != null) {
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.setType("image/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(intent, "Share With"));
        }
    }
}