package com.pdfviewer.util;

import android.text.TextUtils;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArraysUtil {

    public static List<String> shortArrayList(List<String> sortedList) {
        if (sortedList.size() > 1) {
            Collections.sort(sortedList, new Comparator<String>() {
                @Override
                public int compare(String item, String item2) {
                    Integer value = PdfUtil.paresInt(item);
                    Integer value2 = PdfUtil.paresInt(item2);
                    return value.compareTo(value2);
                }
            });
        }
//        String[] finalList = (String[]) sortedList.toArray();
//        if (finalList != null) {
//            return finalList;
//        } else {
//            return pages;
//        }
        return sortedList;
    }

    public static String shortStringArrayList(@Nullable String commaSeparatedList) {
        return join(shortArrayList(split(commaSeparatedList)), commaSeparatedList);
    }

    public static String join(List<String> list, String defaultValue) {
        if (list != null && list.size() > 0) {
            return TextUtils.join(", ", list);
        } else {
            return defaultValue;
        }
    }

    public static List<String> split(String commaSeparatedList) {
        if (commaSeparatedList == null || TextUtils.isEmpty(commaSeparatedList)) {
            return new ArrayList<>();
        }
        if (!commaSeparatedList.contains(",")) {
            return Collections.singletonList(commaSeparatedList);
        } else {
            try {
                return Arrays.asList(commaSeparatedList.split(", "));
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    }
}
