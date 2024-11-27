package com.example.fpmobile.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ScanEntity.class}, version = 1, exportSchema = false)
public abstract class ScanDatabase extends RoomDatabase {

    private static ScanDatabase instance;

    public abstract ScanDao scanDao();

    public static synchronized ScanDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            ScanDatabase.class, "scan_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}