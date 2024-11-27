package com.example.fpmobile;

import android.content.Context;
import org.tensorflow.lite.Interpreter;
import java.nio.MappedByteBuffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;

public class TFLiteModelHelper {
    private final Interpreter interpreter;

    // Constructor: Muat model TFLite dari file assets
    public TFLiteModelHelper(Context context, String modelPath) throws Exception {
        interpreter = new Interpreter(loadModelFile(context, modelPath));
    }

    // Prediksi Comfort Level
    public int predictComfortLevel(float noise, float brightness) {
        // Input untuk model (1 sample, 2 fitur: Noise, Brightness)
        float[][] input = new float[1][2];
        input[0][0] = noise;
        input[0][1] = brightness;

        // Output dari model (4 kelas: Sangat Baik, Baik, Buruk, Sangat Buruk)
        float[][] output = new float[1][4];

        // Jalankan inferensi
        interpreter.run(input, output);

        // Temukan kelas dengan probabilitas tertinggi (argmax)
        return argmax(output[0]);
    }

    // Helper untuk menemukan indeks dengan probabilitas tertinggi
    private int argmax(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    // Muat file model TFLite dari assets
    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws Exception {
        FileInputStream inputStream = new FileInputStream(context.getAssets().openFd(modelPath).getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = context.getAssets().openFd(modelPath).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelPath).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Tutup interpreter saat tidak digunakan
    public void close() {
        interpreter.close();
    }
}