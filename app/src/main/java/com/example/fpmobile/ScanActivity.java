package com.example.fpmobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fpmobile.database.ScanDatabase;
import com.example.fpmobile.database.ScanEntity;

import java.util.Locale;

public class ScanActivity extends AppCompatActivity {

    private TextView btnStartScan;
    private TextView tvDecibels, tvLightLevel, tvRecommendation, tvStatus;
    private float scannedDecibels = 0; // Variabel untuk menyimpan hasil scan decibels
    private float scannedLux = 0;      // Variabel untuk menyimpan hasil scan Lux

    private TFLiteModelHelper modelHelper; // Helper untuk memuat model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Inisialisasi elemen UI
        btnStartScan = findViewById(R.id.btnStartScan);
        tvDecibels = findViewById(R.id.tvDecibels);
        tvLightLevel = findViewById(R.id.tvLightLevel);
        tvRecommendation = findViewById(R.id.tvRecommendation);
        tvStatus = findViewById(R.id.tvTitle);
        Button btnBackToMain = findViewById(R.id.btnNavigateScan); // Tombol untuk kembali ke MainActivity

        // Atur teks awal
        tvDecibels.setText("0.00 dB");
        tvLightLevel.setText("0.00 Lux");
        tvRecommendation.setText("");

        // Load model TFLite
        try {
            modelHelper = new TFLiteModelHelper(this, "comfort_model.tflite");
        } catch (Exception e) {
            e.printStackTrace();
            tvRecommendation.setText("Error loading model");
            return;
        }

        // Tombol Mulai Scan
        btnStartScan.setOnClickListener(v -> startScan());

        // Tombol Kembali ke MainActivity
        btnBackToMain.setOnClickListener(v -> navigateToMain());
    }

    private void startScan() {
        tvStatus.setText("Scanning...");
        btnStartScan.setEnabled(false);

        new Handler().postDelayed(() -> {
            // Simulasi data hasil scan
            scannedDecibels = (float) (Math.random() * 100 + 200); // 200-300 dB
            scannedLux = (float) (Math.random() * 10 + 1); // 1-10 Lux

            // Tampilkan hasil scan di UI
            tvDecibels.setText(String.format(Locale.getDefault(), "%.2f dB", scannedDecibels));
            tvLightLevel.setText(String.format(Locale.getDefault(), "%.2f Lux", scannedLux));

            // Gunakan model TFLite untuk menentukan Comfort Level
            int comfortLevel = modelHelper.predictComfortLevel(scannedDecibels, scannedLux);

            // Dapatkan rekomendasi berdasarkan hasil prediksi
            String recommendation = getSuggestions(comfortLevel, scannedDecibels, scannedLux);

            // Update rekomendasi di UI
            tvRecommendation.setText(recommendation);
            tvRecommendation.setBackgroundColor(getComfortLevelColor(comfortLevel));

            // Simpan ke database
            saveScanToDatabase(comfortLevel, recommendation);

            tvStatus.setText("Environment Scan Complete");
            btnStartScan.setEnabled(true);
        }, 3000); // Simulasi scanning 3 detik
    }

    private String getSuggestions(int comfortLevel, float noise, float brightness) {
        // Berikan rekomendasi berdasarkan Comfort Level
        switch (comfortLevel) {
            case 0: // Sangat Baik
                return "Lingkungan sangat ideal. Tidak perlu tindakan tambahan.";
            case 1: // Baik
                return brightness < 300
                        ? "Tambahkan lampu LED 10 watt untuk pencahayaan lebih baik."
                        : "Lingkungan cukup baik. Pertimbangkan mengurangi kebisingan.";
            case 2: // Buruk
                return noise > 70
                        ? "Kurangi kebisingan dengan penyerap suara atau pindah ke lokasi lebih tenang."
                        : "Pertimbangkan pencahayaan tambahan untuk meningkatkan kenyamanan.";
            case 3: // Sangat Buruk
                return "Lingkungan sangat buruk. Perbaiki pencahayaan dan kurangi kebisingan segera.";
            default:
                return "Tidak ada rekomendasi.";
        }
    }

    private int getComfortLevelColor(int comfortLevel) {
        // Warna berdasarkan Comfort Level
        switch (comfortLevel) {
            case 0: // Sangat Baik
                return getResources().getColor(android.R.color.holo_green_light);
            case 1: // Baik
                return getResources().getColor(android.R.color.holo_blue_light);
            case 2: // Buruk
                return getResources().getColor(android.R.color.holo_orange_light);
            case 3: // Sangat Buruk
                return getResources().getColor(android.R.color.holo_red_light);
            default:
                return getResources().getColor(android.R.color.darker_gray);
        }
    }

    private void saveScanToDatabase(int comfortLevel, String recommendation) {
        // Buat objek ScanEntity dan simpan ke database
        ScanEntity scan = new ScanEntity(scannedDecibels, scannedLux, recommendation, System.currentTimeMillis());
        new Thread(() -> {
            ScanDatabase.getInstance(this).scanDao().insertScan(scan);
        }).start();
    }

    private void navigateToMain() {
        // Kirim hasil scan ke MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SCAN_DECIBELS", scannedDecibels);
        resultIntent.putExtra("SCAN_LUX", scannedLux);
        setResult(RESULT_OK, resultIntent); // Kirim hasil kembali ke MainActivity
        finish(); // Kembali ke MainActivity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (modelHelper != null) {
            modelHelper.close(); // Tutup model saat activity dihancurkan
        }
    }
}