package com.pdfviewer.analytics;

import android.content.Context;
import android.os.Bundle;

public class AppAnalytics extends AnalyticsUtil {

    private static volatile AppAnalytics sSoleInstance;

    private AppAnalytics(Context context) {
        super(context);
    }

    public static AppAnalytics getInstance(Context context) {
        if (sSoleInstance == null) {
            synchronized (AppAnalytics.class) {
                if (sSoleInstance == null) sSoleInstance = new AppAnalytics(context);
            }
        }
        return sSoleInstance;
    }

}
