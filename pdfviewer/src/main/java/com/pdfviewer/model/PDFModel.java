package com.pdfviewer.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.helper.util.GsonParser;
import com.pdfviewer.util.PDFConstant;

@Entity(tableName = "pdf_viewer")
public class PDFModel implements Parcelable, Cloneable{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "autoId")
    @Expose
    private int autoId;

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @Expose
    private int id;

    //contains pdf title visible to user
    @SerializedName("title")
    @ColumnInfo(name = "title")
    @Expose
    private String title;

    @SerializedName("subTitle")
    @ColumnInfo(name = "subTitle")
    @Expose
    private String subTitle;

    @SerializedName("imageUrl")
    @ColumnInfo(name = "imageUrl")
    @Expose
    private String imageUrl;

    //contains pdf file name
    @SerializedName("pdf")
    @ColumnInfo(name = "pdf")
    @Expose
    private String pdf;

    @ColumnInfo(name = "bookmark_pages")
    @Expose
    private String bookmarkPages;

    @ColumnInfo(name = "tags")
    private String tags = "";

    @ColumnInfo(name = "filePath")
    @Expose
    private String filePath;

    @ColumnInfo(name = "viewCount")
    @Expose
    private int viewCount;

    @ColumnInfo(name = "viewCountFormatted")
    @Expose
    private String viewCountFormatted;

    @Ignore
    private Uri fileUri;

    @Ignore
    private String pdfUrl;

    @Ignore
    private String pdfBaseUrlPrefix;

    @ColumnInfo(name = "openPagePosition")
    @Expose
    private int openPagePosition = PDFConstant.INITIAL_PAGE_POSITION;

    @ColumnInfo(name = "updated_at", defaultValue = PDFConstant.DEFAULT_UPDATED_AT)
    @Expose
    private String updated_at = PDFConstant.DEFAULT_UPDATED_AT;

    @ColumnInfo(name = "last_update", defaultValue = PDFConstant.DEFAULT_UPDATED_AT)
    @Expose
    private String lastUpdate = PDFConstant.DEFAULT_UPDATED_AT;

    @ColumnInfo(name = "stats_json", defaultValue = "")
    @Expose
    private String statsJson = "";

    @Ignore
    private int isFileAlreadyDownloaded = 1;

    public PDFModel() {
    }

    public String toJson() {
        return GsonParser.getGson().toJson(this, PDFModel.class);
    }

    protected PDFModel(Parcel in) {
        autoId = in.readInt();
        id = in.readInt();
        title = in.readString();
        subTitle = in.readString();
        imageUrl = in.readString();
        pdf = in.readString();
        bookmarkPages = in.readString();
        tags = in.readString();
        filePath = in.readString();
        viewCountFormatted = in.readString();
        viewCount = in.readInt();
        pdfUrl = in.readString();
        pdfBaseUrlPrefix = in.readString();
        fileUri = in.readParcelable(Uri.class.getClassLoader());
        openPagePosition = in.readInt();
        updated_at = in.readString();
        lastUpdate = in.readString();
        statsJson = in.readString();
        isFileAlreadyDownloaded = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(autoId);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeString(imageUrl);
        dest.writeString(pdf);
        dest.writeString(bookmarkPages);
        dest.writeString(tags);
        dest.writeString(filePath);
        dest.writeString(viewCountFormatted);
        dest.writeInt(viewCount);
        dest.writeString(pdfUrl);
        dest.writeString(pdfBaseUrlPrefix);
        dest.writeParcelable(fileUri, flags);
        dest.writeInt(openPagePosition);
        dest.writeString(updated_at);
        dest.writeString(lastUpdate);
        dest.writeString(statsJson);
        dest.writeInt(isFileAlreadyDownloaded);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PDFModel> CREATOR = new Creator<PDFModel>() {
        @Override
        public PDFModel createFromParcel(Parcel in) {
            return new PDFModel(in);
        }

        @Override
        public PDFModel[] newArray(int size) {
            return new PDFModel[size];
        }
    };

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getAutoId() {
        return autoId;
    }

    public void setAutoId(int autoId) {
        this.autoId = autoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getBookmarkPages() {
        return bookmarkPages;
    }

    public void setBookmarkPages(String bookmarkPages) {
        this.bookmarkPages = bookmarkPages;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getViewCountFormatted() {
        return viewCountFormatted;
    }

    public void setViewCountFormatted(String viewCountFormatted) {
        this.viewCountFormatted = viewCountFormatted;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getOpenPagePosition() {
        return openPagePosition;
    }

    public void setOpenPagePosition(int openPagePosition) {
        this.openPagePosition = openPagePosition;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getPdfBaseUrlPrefix() {
        return pdfBaseUrlPrefix;
    }

    public void setPdfBaseUrlPrefix(String pdfBaseUrlPrefix) {
        this.pdfBaseUrlPrefix = pdfBaseUrlPrefix;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getStatsJson() {
        return statsJson;
    }

    public void setStatsJson(String statsJson) {
        this.statsJson = statsJson;
    }

    public boolean isFileAlreadyDownloaded() {
        return isFileAlreadyDownloaded == 1;
    }

    public void setFileAlreadyDownloaded(boolean isFileAlreadyDownloaded) {
        this.isFileAlreadyDownloaded = isFileAlreadyDownloaded ? 1 : 0;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public PDFModel getClone() {
        try {
            return (PDFModel) clone();
        } catch (CloneNotSupportedException e) {
            return new PDFModel();
        }
    }
}
