package com.pdfviewer.analytics;

public interface AnalyticsKeys {

    String HOME_PAGE_TABS_CLICK = "home_page_tabs_click";
    String HOME_PAGE_DRAWER_CLICK = "home_page_drawer_click";
    String HOME_PAGE_MENU_CLICK = "home_page_menu_click";
    String HOME_PAGE_POSITION_CHANGE_CLICK = "home_page_position_change_click";

    String UPDATE_PAGE_ARTICLE_CLICK = "update_page_article_click";
    String HOME_PAGE_CATEGORY_CLICK = "home_page_category_click";
    String CLASS_LANG_TAB = "class_lang_tab";//ClassFragment
    String CLASS_CLICKED = "class_clicked";//ClassFragment
    String SUBJECT_SEARCH_CLICK = "subject_search_click";
    String SUBJECT_CLICKED = "subject_clicked";//SubjectFragment
    String CHAPTER_BUTTON_CLICK = "chapter_button_click";//BooksFragment
    String CHAPTER_CLICKED = "chapter_clicked";//BooksFragment
    String PDF_DOWNLOAD_CLICK = "pdf_download_click";
    String LOG_EVENT = "log_event";

    interface Param {
        String TAG = "tag";
        String ARTICLE_ID = "article_id";
        String ARTICLE_TITLE = "article_title";
        String CATEGORY_NAME = "category_name";
        String CATEGORY_ID = "category_id";
        String TABS_TITLE = "tabs_title";
        String CLASS_ID = "class_id";
        String CLASS_NAME = "class_name";
        String SUBJECT_NAME = "subject_name";
        String SUBJECT_ID = "subject_id";
        String TYPE = "type";
        String CHAPTER_NAME = "chapter_name";
        String CHAPTER_ID = "chapter_id";
        String PDF_TITLE = "pdf_title";
    }

    interface ParamValue {
        String CURRENT_SCREEN = "CurrentScreen";
        String BASIC = "Basic";
        String ADVANCE = "Advance";
        String DEFAULT = "Default"; // ( Default as advance like anywhere in whole single item )
        String UPDATE = "Update";
        String HOME = "Home";
        String TEST_SERIES = "Test Series";
        String RESULT = "Result";
        String NOTIFICATION = "Notification";
        String SHARE = "Share";
        String RATE_US = "RateUs";
        String SEARCH = "Search";
    }

}
