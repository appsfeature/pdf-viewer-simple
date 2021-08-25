package com.pdfviewer.database;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.pdfviewer.model.PDFHistoryModel;
import com.pdfviewer.model.PDFModel;

//Keyword PDFDatabase filter in logcat
@Database(entities = {PDFModel.class, PDFHistoryModel.class}, version = 1005, exportSchema = false)
public abstract class PDFDatabase extends RoomDatabase {
    private static final String DB_NAME = "pdf-viewer-db";

    public abstract PDFViewerDAO pdfViewerDAO();
    public abstract PDFHistoryDAO pdfHistoryDAO();

    public static PDFDatabase create(Context context) {
        return Room.databaseBuilder(context, PDFDatabase.class, DB_NAME)
                .addMigrations(MIGRATION_0_2, MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build();
    }

    private static final Migration MIGRATION_4_5 = new Migration(1004, 1005) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            try {
                if (database != null) {
                    database.execSQL("ALTER TABLE pdf_viewer "
                            + " ADD COLUMN viewCount INTEGER NOT NULL DEFAULT 0");
                    database.execSQL("ALTER TABLE pdf_viewer "
                            + " ADD COLUMN viewCountFormatted TEXT DEFAULT ''");
                }
            } catch (Exception e) {
                Log.e("PDFDatabase", "MIGRATION_4_5 : " + e.getMessage());
            }
            try {
                database.execSQL("CREATE TABLE pdf_history (" +
                        "autoId INTEGER PRIMARY KEY NOT NULL," +
                        "id INTEGER NOT NULL DEFAULT 0," +
                        "title TEXT," +
                        "subTitle TEXT," +
                        "itemType INTEGER NOT NULL DEFAULT 0," +
                        "viewCount INTEGER NOT NULL DEFAULT 0," +
                        "viewCountFormatted TEXT," +
                        "jsonData TEXT," +
                        "itemState TEXT," +
                        "catId INTEGER NOT NULL DEFAULT 0," +
                        "subCatId INTEGER NOT NULL DEFAULT 0," +
                        "createdAt TEXT DEFAULT '2020-04-10 14:58:00')");
            } catch (SQLException e) {
                Log.e("PDFDatabase", "MIGRATION_4_5 : " + e.getMessage());
            }
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(1003, 1004) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            try {
                if (database != null) {
                    database.execSQL("ALTER TABLE pdf_viewer "
                            + " ADD COLUMN stats_json TEXT DEFAULT ''");
                }
            } catch (Exception e) {
                Log.e("PDFDatabase", "MIGRATION_3_4 : " + e.getMessage());
            }
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(1002, 1003) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            try {
                if (database != null) {
                    database.execSQL("ALTER TABLE pdf_viewer "
                            + " ADD COLUMN last_update TEXT DEFAULT '2020-04-10 14:58:00'");
                }
            } catch (Exception e) {
                Log.e("PDFDatabase", "MIGRATION_2_3 : " + e.getMessage());
            }
        }
    };

    private static final Migration MIGRATION_1_2 = new Migration(1001, 1002) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            addingUpdatedAtColumn(database);
        }
    };

    private static final Migration MIGRATION_0_2 = new Migration(1000, 1002) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            addingUpdatedAtColumn(database);
        }
    };

    private static void addingUpdatedAtColumn(SupportSQLiteDatabase database) {
        try {
            if (database != null) {
                database.execSQL("CREATE TABLE pdf_viewer_new (" +
                        "autoId INTEGER PRIMARY KEY NOT NULL," +
                        "id INTEGER NOT NULL DEFAULT 0," +
                        "title TEXT," +
                        "subTitle TEXT," +
                        "imageUrl TEXT," +
                        "pdf TEXT," +
                        "bookmark_pages TEXT," +
                        "tags TEXT," +
                        "filePath TEXT," +
                        "openPagePosition INTEGER NOT NULL DEFAULT 0," +
                        "updated_at TEXT DEFAULT '2020-04-10 14:58:00')");
                // Copy the data
                if (existsColumnInTable(database, "pdf_viewer", "fileUri")) {
                    database.execSQL(
                            "INSERT INTO pdf_viewer_new (autoId, id, title, subTitle, imageUrl, pdf, bookmark_pages, tags, filePath, openPagePosition) SELECT autoId, id, title, subTitle, imageUrl, pdf, bookmark_pages, tags, fileUri, openPagePosition FROM pdf_viewer");
                } else {
                    database.execSQL(
                            "INSERT INTO pdf_viewer_new (autoId, id, title, subTitle, imageUrl, pdf, bookmark_pages, tags, filePath, openPagePosition) SELECT autoId, id, title, subTitle, imageUrl, pdf, bookmark_pages, tags, filePath, openPagePosition FROM pdf_viewer");
                }
                database.execSQL("DROP TABLE pdf_viewer");
                database.execSQL("ALTER TABLE pdf_viewer_new RENAME TO pdf_viewer");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PDFDatabase", "addingUpdatedAtColumn : " + e.getMessage());
        }
    }

    private static boolean existsColumnInTable(SupportSQLiteDatabase inDatabase, String inTable, String columnToCheck) {
        Cursor mCursor = null;
        try {
            // Query 1 row
            mCursor = inDatabase.query("SELECT * FROM " + inTable + " LIMIT 0", null);
            if (mCursor.getColumnIndex(columnToCheck) != -1)
                return true;
            else
                return false;

        } catch (Exception Exp) {
            Log.e("PDFDatabase", "existsColumnInTable : " + Exp.getMessage());
            return false;
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }

}