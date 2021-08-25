package com.pdfviewer.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

//import com.google.firebase.BuildConfig;
//import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsUtil extends TagManager {

//    private final FirebaseAnalytics mFirebaseAnalytics;

    AnalyticsUtil(Context context) {
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }


    public void setCurrentScreen(Activity activity) {
//        if (activity != null && mFirebaseAnalytics != null) {
//            mFirebaseAnalytics.setCurrentScreen(activity,
//                    AnalyticsKeys.ParamValue.CURRENT_SCREEN + " : " + activity.getClass().getSimpleName(), null);
//        }
    }

    public void setUserProperty(String key, String value) {
//        if (mFirebaseAnalytics != null) {
//            mFirebaseAnalytics.setUserProperty(key, value);
//            if (BuildConfig.DEBUG) {
//                Log.d("@Analytics-user", key + ":" + value);
//            }
//        }
    }

    // custom parameter must be <= 24 characters
    // custom value must be <= 36 characters
    public void sendEvent(String eventName, Bundle params) {
        // custom event must be <= 32 characters
//        if (mFirebaseAnalytics != null && params != null) {
//            mFirebaseAnalytics.logEvent(eventName, params);
//            if (BuildConfig.DEBUG) {
//                for (String key : params.keySet()) {
//                    Log.d("@Analytics-" + eventName, key + ":" + params.get(key));
//                }
//            }
//        }
    }
}
