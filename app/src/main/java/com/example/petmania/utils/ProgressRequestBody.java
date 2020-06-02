package com.example.petmania.utils;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.example.petmania.activities.ImagesSelectionActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.reactivex.annotations.NonNull;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private File file;
    private static final int DEFAULT_BUFFER_SITE = 4096;
    private UploadCallBack listener;

    public ProgressRequestBody(File file, UploadCallBack listener) {
        this.file = file;
        this.listener = listener;
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @NonNull
    @Override
    public MediaType contentType() {
        return MediaType.parse("image/*");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = file.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SITE];
        FileInputStream fileInputStream = new FileInputStream(file);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = fileInputStream.read(buffer)) != -1) {
                handler.post(new ProgressUpdater(uploaded, fileLength));
                uploaded += read;
                sink.write(buffer, 0, read);
            }
        } finally {
            fileInputStream.close();
        }

    }

    private class ProgressUpdater implements Runnable {
        private long uploaded, fileLength;

        public ProgressUpdater(long uploaded, long fileLength) {
            this.uploaded = uploaded;
            this.fileLength = fileLength;

        }

        @Override
        public void run() {
            listener.onProgressUpdate((int) (100 * uploaded / fileLength));
        }
    }
}
