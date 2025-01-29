package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FilesActivity extends AppCompatActivity {

    private static final String TAG = "FilesActivity";
    private static final String API_KEY = "AIzaSyBkuIQPRT1Hd2_8AVgnO6lCANbPpvBRXQM";
    private static final String FOLDER_ID = "1guPJGpwECaJ_oPwX3FHWKbxPwhuTA-Pb";

    private Drive driveService;
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fileAdapter = new FileAdapter(new ArrayList<>(), this::openFile);
        recyclerView.setAdapter(fileAdapter);

        // Inicializace Google Drive API
        driveService = new Drive.Builder(
                new NetHttpTransport(),
                new GsonFactory(),
                null
        ).setApplicationName("MyApplication").build();

        // Načtení souborů
        loadPublicFiles();
    }

    private void loadPublicFiles() {
        executorService.execute(() -> {
            try {
                String query = "'%s' in parents and trashed = false";
                query = String.format(query, FOLDER_ID);

                FileList result = driveService.files().list()
                        .setQ(query)
                        .setFields("files(id, name, thumbnailLink, webViewLink)")
                        .setKey(API_KEY)
                        .execute();

                List<File> files = result.getFiles();
                runOnUiThread(() -> {
                    if (files != null && !files.isEmpty()) {
                        fileAdapter.updateFiles(files);
                    }
                });
            } catch (IOException e) {
                Log.e(TAG, "❌ Chyba při načítání souborů", e);
            }
        });
    }

    private void openFile(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
