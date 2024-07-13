package com.pdfviewer.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.helper.util.DayNightPreference;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.util.PDFCallback;

public class MainActivity extends AppCompatActivity {

    private static final String PDF_FILE_URL = "http://www.pdf995.com/samples/pdf.pdf";
    private static final String PDF_FILE_URL_1 = "https://www.selfstudys.com/api/v7/download-pdf/144711/" +
            "x76J9GCVeL5xjpPfmid5" +
            ".pdf";
    private static final String PDF_FILE_URL_2 = "https://www.selfstudys.com:8082/api/v7/download-pdf/175360/9DXYYAmLrBD0JWr2g4Vo.pdf";
    private static final String PDF_FILE_URL_3 = "https://ars.els-cdn.com/content/image/1-s2.0-S0968089612008322-mmc1.pdf";//20 mb file size
    private static final String PDF_FILE_URL_4 = "https://ars.els-cdn.com/content/image/1-s2.0-S0308814616314601-mmc1.pdf";//40 mb file size
    private static final String PDF_FILE_URL_5 = "https://ars.els-cdn.com/content/image/1-s2.0-S1525001616328027-mmc2.pdf";//100mb file size
    private static final String PDF_FILE_NAME = "6df10173d82c32b47e851779e22a8bad.pdf";
    private static final String PDF_FILE_NAME2 = "sample_2";
    private static final String PDF_TITLE = "Sample Pdf File with extra ordinary features";
    private static final String PDF_TITLE2 = "Sample Pdf File 2";
    private static final String PDF_PREFIX = "http://www.pdf995.com/samples/";
//Large pdf file size 6Mb
//    private static final String PDF_FILE_URL = "https://www.hq.nasa.gov/alsj/a410/AS08_CM.PDF";
//    private static final String PDF_FILE_NAME = "AS08_CM";
//    private static final String PDF_TITLE = "AS08_CM Pdf File";

//    private static final String PDF_FILE_URL2 = "https://www.emapsite.com/downloads/product_guides/Land-Form-Profile-user-guide.pdf";
//    private static final String PDF_FILE_NAME2 = "Land-Form-Profile-user-guide";
//    private static final String PDF_TITLE2 = "Land Form Profile user guide";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (findViewById(R.id.btn_open)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PDFViewer.openPdfDownloadActivity(MainActivity.this, PDF_TITLE, PDF_FILE_NAME, PDF_FILE_URL, true);
                String fileUrl = AppApplication.BASE_URL_PDF_DOWNLOAD + PDF_FILE_NAME;
                PDFViewer.openPdfDownloadActivity(MainActivity.this, 500, PDF_TITLE, PDF_FILE_NAME, "" , fileUrl, "Category 1");
            }
        });
        (findViewById(R.id.btn_open2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PDFViewer.openPdfDownloadActivity(MainActivity.this, PDF_TITLE, PDF_FILE_NAME, PDF_FILE_URL, true);
                PDFViewer.openPdfDownloadActivity(MainActivity.this, 501, PDF_TITLE2, PDF_FILE_NAME2, PDF_PREFIX , PDF_FILE_URL_2, "Category 2");
            }
        });
        (findViewById(R.id.btn_open1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFViewer.openPdfDownloadActivity(MainActivity.this, 0, PDF_TITLE, PDF_FILE_NAME, PDF_FILE_URL, "Category 1", false, true);
//                PDFViewer.openPdfDownloadActivity(MainActivity.this, 0, PDF_TITLE2, PDF_FILE_NAME2, PDF_FILE_URL2, "Category 1", false, true);
            }
        });
        (findViewById(R.id.btn_open_bookmark)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFViewer.openPdfBookmarkActivity(v.getContext());
            }
        });

        (findViewById(R.id.btn_open_download)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFViewer.openPdfDownloadedListActivity(v.getContext());
            }
        });

        SwitchCompat switchCompat = findViewById(R.id.switchCompat);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            switchCompat.setChecked(true);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               DayNightPreference.setNightMode(MainActivity.this, isChecked);
            }
        });

        PDFViewer.getInstance().addStatisticsCallbacks(new PDFCallback.StatsListener() {
            @Override
            public void onStatsUpdated() {
                publishStatsResult();
            }
        });

    }

    private void publishStatsResult() {

    }

}
