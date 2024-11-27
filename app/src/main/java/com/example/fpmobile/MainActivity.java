package com.example.fpmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TFLiteModelHelper modelHelper; // Helper untuk memuat model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load model TFLite
        try {
            modelHelper = new TFLiteModelHelper(this, "comfort_model.tflite");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Navigasi ke halaman scanning
        TextView btnNavigateScan = findViewById(R.id.btnNavigateScan);
        btnNavigateScan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
            startActivityForResult(intent, 1); // Request code 1 untuk scan
        });

        // Tombol untuk membuka riwayat
        TextView btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Tombol untuk membuka analisis
        TextView btnAnalysis = findViewById(R.id.btnAnalysis);
        btnAnalysis.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
            startActivity(intent);
        });

        // Reset UI saat pertama kali aplikasi dibuka
        resetUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Ambil hasil scan
            float scanDecibels = data.getFloatExtra("SCAN_DECIBELS", 0);
            float scanLux = data.getFloatExtra("SCAN_LUX", 0);
            int comfortLevel = data.getIntExtra("COMFORT_LEVEL", -1); // Pastikan ScanActivity mengembalikan level ini

            // Tampilkan hasil scan di UI
            updateUI(scanDecibels, scanLux);

            // Dapatkan rekomendasi berdasarkan comfort level
            String recommendation = getRecommendation(comfortLevel, scanDecibels, scanLux);

            // Update rekomendasi di UI
            updateRecommendations(recommendation, comfortLevel);
        }
    }

    private void resetUI() {
        // Set nilai default untuk TextView
        TextView decibelTextView = findViewById(R.id.decibels);
        TextView lightLevelTextView = findViewById(R.id.lightLevel);
        TextView suggestionTextView = findViewById(R.id.suggestion);

        decibelTextView.setText("0.00 dB");
        lightLevelTextView.setText("0.00 Lux");
        suggestionTextView.setText("No data available");
        suggestionTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void updateUI(float decibels, float lux) {
        TextView decibelTextView = findViewById(R.id.decibels);
        TextView lightLevelTextView = findViewById(R.id.lightLevel);

        decibelTextView.setText(String.format(Locale.getDefault(), "%.2f dB", decibels));
        lightLevelTextView.setText(String.format(Locale.getDefault(), "%.2f Lux", lux));
    }

    private String getRecommendation(int comfortLevel, float decibels, float lux) {
        switch (comfortLevel) {
            case 0:
                return "Lingkungan sangat ideal untuk bayi. Tidak perlu tindakan.";
            case 1:
                return lux < 300
                        ? "Tambahkan lampu LED 10 watt untuk pencahayaan lebih baik."
                        : "Lingkungan baik, namun bisa pertimbangkan mengurangi kebisingan.";
            case 2:
                return decibels > 70
                        ? "Kurangi kebisingan dengan penyerap suara atau pindah ke lokasi lebih tenang."
                        : "Pertimbangkan pencahayaan tambahan untuk meningkatkan kenyamanan.";
            case 3:
                return "Lingkungan sangat buruk untuk bayi. Perbaiki pencahayaan dan kurangi kebisingan segera.";
            default:
                return "Tidak ada rekomendasi.";
        }
    }

    private void updateRecommendations(String recommendation, int comfortLevel) {
        TextView suggestionTextView = findViewById(R.id.suggestion);

        suggestionTextView.setText(recommendation);

        // Atur warna teks sesuai comfort level
        int textColor;
        switch (comfortLevel) {
            case 0: // Sangat Baik
                textColor = getResources().getColor(android.R.color.holo_green_dark);
                break;
            case 1: // Baik
                textColor = getResources().getColor(android.R.color.holo_blue_dark);
                break;
            case 2: // Buruk
                textColor = getResources().getColor(android.R.color.holo_orange_dark);
                break;
            case 3: // Sangat Buruk
                textColor = getResources().getColor(android.R.color.holo_red_dark);
                break;
            default:
                textColor = getResources().getColor(android.R.color.darker_gray);
                break;
        }

        // Terapkan warna teks ke TextView
        suggestionTextView.setTextColor(textColor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (modelHelper != null) {
            modelHelper.close(); // Tutup model saat activity dihancurkan
        }
    }
}