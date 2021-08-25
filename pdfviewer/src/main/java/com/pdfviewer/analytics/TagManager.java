package com.pdfviewer.analytics;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

public class TagManager {

    private static final String PUSH = "Push-";
    private static final String REMOVE = "Remove-";
    private ArrayList<String> userDirection;
    private ArrayList<String> userBehaviour;
    private ArrayList<String> screenBehaviour;

    public TagManager() {
        super();
        userDirection = new ArrayList<>();
        userBehaviour = new ArrayList<>();
        screenBehaviour = new ArrayList<>();
    }

    public void addScreenTags(Context context) {
        if (context != null && screenBehaviour != null) {
            screenBehaviour.add(PUSH + context.getClass().getSimpleName());
        }
    }

    public void removeScreenTags(Context context) {
        if (context != null && screenBehaviour != null) {
            screenBehaviour.add(REMOVE + context.getClass().getSimpleName());
        }
    }

    public void addTag(String title) {
        if (userDirection != null && !userDirection.contains(title)) {
            userDirection.add(title);
        }
        if (userBehaviour != null) {
            userBehaviour.add(PUSH + title);
        }
    }

    public void removeTag(String title) {
        if (userDirection != null) {
            userDirection.remove(title);
        }
        if (userBehaviour != null) {
            userBehaviour.add(REMOVE + title);
        }
    }

    public String getTags() {
        if(userDirection == null){
            return "";
        }
        return TextUtils.join(",", userDirection);
    }

    public String getUserBehaviourTags() {
        if(userBehaviour == null){
            return "";
        }
        return TextUtils.join(",", userBehaviour);
    }

    public String getScreenBehaviourTags() {
        if(screenBehaviour == null){
            return "";
        }
        return TextUtils.join(",", screenBehaviour);
    }
}
