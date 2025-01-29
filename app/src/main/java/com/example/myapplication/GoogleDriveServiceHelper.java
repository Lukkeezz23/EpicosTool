package com.example.myapplication;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;

public class GoogleDriveServiceHelper {

    private static final String API_KEY = "TVŮJ_API_KLÍČ"; // 🔹 Sem vlož svůj API klíč
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Inicializuje službu Google Drive bez přihlášení.
     *
     * @return Drive instance s API klíčem.
     */
    public static Drive getDriveService() {
        HttpTransport transport = new NetHttpTransport();

        return new Drive.Builder(transport, JSON_FACTORY, null)
                .setApplicationName("MyApplication")
                .build();
    }
}
