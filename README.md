# pdf-viewer  

#### Library size is : Kb
  
## Setup Project

Add this to your project build.gradle
``` gradle
allprojects {
    repositories {
        maven {
            url "https://jitpack.io"
        }
    }
}
```

Add this to your project build.gradle

#### Dependency
[![](https://jitpack.io/v/org.bitbucket.android-dennislabs/pdf-viewer.svg)](https://jitpack.io/#org.bitbucket.android-dennislabs/pdf-viewer)
```gradle
dependencies {
        implementation 'org.bitbucket.android-dennislabs:pdf-viewer:3.4'
}
```


### Statistics Usage methods
```java
    Statistics maintain only if pdf has been ReOpened or open from bookmark list.
    Stats save in following conditions:
        1. ReOpen pdf from Download screen.
        2. Open pdf from bookmark list.

    Stats Tracking Method are:
         PDFViewer.getInstance().addStatisticsCallbacks(new PDFCallback.StatsListener() {
             @Override
             public void onStatsUpdated() {
                 publishStatsResultInServer();
             }
         });

    Getting PdfViewer statistics by using following method are:
        1. Run on MainThread
            PDFStatsCreator.getStatsJsonData(this, new PDFCallback.Statistics() {
                @Override
                public void onStatsUpdate(PDFStatsResponse response, String jsonData) {
                    Toast.makeText(MainActivity.this, "" + jsonData, Toast.LENGTH_SHORT).show();
                }
            });
        2. Run on BackgroundThread
            String pdfStatsJson = PDFStatsCreator.getStatsJsonData(this);


```