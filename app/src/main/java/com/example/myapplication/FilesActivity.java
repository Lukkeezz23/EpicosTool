package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.List;

public class FilesActivity extends AppCompatActivity {

    private static final String API_KEY = "AIzaSyBkuIQPRT1Hd2_8AVgnO6lCANbPpvBRXQM"; // Vložte svůj API klíč
    private static final String FOLDER_ID = "1ReqSiZuplnAz7I8Lbh9kvx65DDlq7Txz"; // Vložte ID veřejné složky (nebo nechte prázdné pro všechny veřejné soubory)

    private Drive driveService;
    private TextView textViewFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        textViewFiles = findViewById(R.id.textViewFiles);

        // Inicializace Google Drive API klienta
        driveService = new Drive.Builder(
                new NetHttpTransport(),
                new JacksonFactory(),
                (HttpRequestInitializer) request -> request.setInterceptor(intercepted -> {
                    intercepted.getUrl().put("key", API_KEY);
                })
        ).setApplicationName("MyApplication")
                .build();

        // Spuštění načítání souborů
        new LoadPublicFilesTask().execute();
    }

    private class LoadPublicFilesTask extends AsyncTask<Void, Void, List<File>> {

        @Override
        protected List<File> doInBackground(Void... voids) {
            try {
                String query = "visibility = 'anyoneCanFind' or visibility = 'anyoneWithLink'";
                if (!FOLDER_ID.isEmpty()) {
                    query += " and '" + FOLDER_ID + "' in parents";
                }

                FileList result = driveService.files().list()
                        .setQ(query)
                        .setFields("files(id, name, webViewLink)")
                        .execute();

                return result.getFiles();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<File> files) {
            if (files != null && !files.isEmpty()) {
                StringBuilder fileNames = new StringBuilder("Veřejné soubory:\n");
                for (File file : files) {
                    fileNames.append("📄 ").append(file.getName()).append("\n🔗 ").append(file.getWebViewLink()).append("\n\n");
                }
                textViewFiles.setText(fileNames.toString());
            } else {
                textViewFiles.setText("❌ Žádné veřejné soubory nebyly nalezeny.");
            }
        }
    }
}
