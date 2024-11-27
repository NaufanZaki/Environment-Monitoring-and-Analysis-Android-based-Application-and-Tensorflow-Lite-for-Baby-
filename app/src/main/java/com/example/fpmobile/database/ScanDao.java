package com.example.fpmobile.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScanDao {

    @Insert
    void insertScan(ScanEntity scan);

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    List<ScanEntity> getAllScans();

    @Query("DELETE FROM scan_history")
    void deleteAll();
}