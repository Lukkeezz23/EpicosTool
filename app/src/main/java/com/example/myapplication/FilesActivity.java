package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.util.Collections;
import java.util.List;

public class FilesActivity extends AppCompatActivity {
    private Drive driveService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        // Inicializace GoogleAccountCredential
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                this,
                Collections.singleton( DriveScopes.DRIVE)
        );
        credential.setSelectedAccountName("epicoshala1@gmail.com");

        // Inicializace Drive API klienta
        driveService = new Drive.Builder(
                new NetHttpTransport (),
                new JacksonFactory (),
                credential
        )
                .setApplicationName("My Application")
                .build();

        // Spuštění AsyncTask
        new LoadFilesTask(driveService).execute();
    }

    private class LoadFilesTask extends AsyncTask<Void, Void, List<File>> {
        private Drive driveService;

        public LoadFilesTask(Drive driveService) {
            this.driveService = driveService;
        }

        @Override
        protected List<File> doInBackground(Void... params) {
            try {
                Drive.Files.List request = driveService.files().list();
                // Zpracuj výsledky...
                return request.execute().getFiles();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
