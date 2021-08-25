package com.pdfviewer.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.helper.model.HistoryModelResponse;
import com.helper.util.GsonParser;
import com.helper.util.LoggerCommon;

import java.io.Serializable;

@Entity(tableName = "pdf_history", inheritSuperIndices = true)
public class PDFHistoryModel extends HistoryModelResponse implements Serializable, Cloneable {

    @Ignore
    private PDFModel pdfProperty;

    public PDFHistoryModel() {
    }

    public PDFModel getPDFProperty() {
        if(pdfProperty == null) {
            pdfProperty = GsonParser.getGson().fromJson(getJsonData(), PDFModel.class);
        }
        return pdfProperty;
    }

    public void setPdfProperty(PDFModel pdfProperty) {
        this.pdfProperty = pdfProperty;
    }

    @Override
    @NonNull
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public PDFHistoryModel getClone() {
        try {
            return (PDFHistoryModel) clone();
        } catch (CloneNotSupportedException e) {
            LoggerCommon.d(LoggerCommon.getClassPath(this.getClass(),"getClone"),e.toString());
            return new PDFHistoryModel();
        }
    }

}
