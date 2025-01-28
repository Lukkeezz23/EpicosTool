package com.example.myapplication;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleDriveServiceHelper {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Inicializuje službu Google Drive.
     *
     * @param context Kontext aplikace.
     * @return Objekt Drive pro přístup k API.
     */
    public static Drive getDriveService(Context context) {
        try {
            // Inicializace Google Account Credential
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    context, Collections.singletonList( DriveScopes.DRIVE_FILE));

            // Přesměrování uživatele pro výběr účtu (pokud není vybrán)
            if (credential.getSelectedAccount() == null) {
                context.startActivity(credential.newChooseAccountIntent());
                return null; // Vrátí null, dokud uživatel nevybere účet
            }

            // Vytvoření instance služby Drive
            return new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    credential
            ).setApplicationName("MyApplication").build();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
