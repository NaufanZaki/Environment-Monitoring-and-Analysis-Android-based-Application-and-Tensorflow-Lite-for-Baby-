package com.example.fpmobile.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scan_history")
public class ScanEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private float decibels;
    private float lux;
    private String recommendation;
    private long timestamp;

    public ScanEntity(float decibels, float lux, String recommendation, long timestamp) {
        this.decibels = decibels;
        this.lux = lux;
        this.recommendation = recommendation;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getDecibels() {
        return decibels;
    }

    public float getLux() {
        return lux;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public long getTimestamp() {
        return timestamp;
    }
}