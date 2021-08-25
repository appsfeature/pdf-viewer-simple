package com.pdfviewer.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.helper.model.HistoryModelResponse;
import com.pdfviewer.model.PDFHistoryModel;

import java.util.List;

@Dao
public interface PDFHistoryDAO {

    @Insert
    Long insertHistory(PDFHistoryModel record);

    @Query("SELECT * FROM pdf_history order by datetime(createdAt) DESC")
    List<HistoryModelResponse> getPDFHistory();

    @Query("SELECT * FROM (SELECT * FROM pdf_history ORDER BY createdAt DESC) AS x GROUP BY id ORDER BY createdAt DESC")
    List<HistoryModelResponse> getPDFHistoryUnique();

    @Query("DELETE FROM pdf_history WHERE autoId ==:autoId AND id ==:id")
    void delete(int autoId, int id);

    @Query("UPDATE pdf_history SET jsonData =:jsonData WHERE autoId ==:autoId")
    void updateMigrationJsonData(int autoId, String jsonData);

    @Query("DELETE FROM pdf_history")
    void clearAllRecords();
}
