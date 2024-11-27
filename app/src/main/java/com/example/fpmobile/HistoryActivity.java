package com.example.fpmobile;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fpmobile.database.ScanDatabase;
import com.example.fpmobile.database.ScanEntity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TextView historyTextView = findViewById(R.id.historyTextView);

        // Ambil data dari database dan tampilkan
        new Thread(() -> {
            List<ScanEntity> scans = ScanDatabase.getInstance(this).scanDao().getAllScans();
            runOnUiThread(() -> {
                if (scans.isEmpty()) {
                    historyTextView.setText("No history available.");
                } else {
                    StringBuilder history = new StringBuilder();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    for (ScanEntity scan : scans) {
                        history.append(String.format("Decibels: %.2f dB, Lux: %.2f\n", scan.getDecibels(), scan.getLux()));
                        history.append(String.format("Recommendation: %s\n", scan.getRecommendation()));
                        history.append(String.format("Time: %s\n\n", sdf.format(scan.getTimestamp())));
                    }
                    historyTextView.setText(history.toString());
                }
            });
        }).start();
    }
}