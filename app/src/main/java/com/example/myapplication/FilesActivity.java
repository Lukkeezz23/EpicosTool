package com.example.myapplication;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.util.Collections;
import java.util.List;

public class FilesActivity extends AppCompatActivity {
    private static final int REQUEST_ACCOUNT_PICKER = 1001; // Kód pro výběr účtu
    private Drive driveService; // Google Drive API klient

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        // Spustit výběr účtu
        chooseAccount();
    }

    /**
     * Spustí dialog pro výběr účtu uživatelem.
     */
    private void chooseAccount() {
        Intent intent = AccountPicker.newChooseAccountIntent (
                null, // Account - vybraný účet (null pro žádný výchozí)
                null, // ArrayList<Account> - seznam účtů (null, pokud se má zobrazit všechny dostupné)
                new String[]{"com.google"}, // Typ účtu - Google
                true, // AllowableAccounts - povolit přístup k účtům
                null, // DescriptionTextOverride - volitelný text dialogu
                null, // AuthTokenType - typ autentizačního tokenu
                null, // AddAccountAuthTokenType - typ přidání účtu
                null  // AddAccountOptions - dodatečné parametry
        );
        startActivityForResult ( intent, REQUEST_ACCOUNT_PICKER );
    }
    /**
     * Zpracování výsledku výběru účtu.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ACCOUNT_PICKER && resultCode == RESULT_OK && data != null) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            if (accountName != null) {
                initializeDriveService(accountName);
            } else {
                System.out.println("Žádný účet nebyl vybrán.");
            }
        } else {
            System.out.println("Výběr účtu byl zrušen.");
        }
    }

    /**
     * Inicializace služby Google Drive API s vybraným účtem.
     *
     * @param accountName Název účtu Google
     */
    private void initializeDriveService(String accountName) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                this, Collections.singleton(DriveScopes.DRIVE)
        );
        credential.setSelectedAccountName(accountName);

        driveService = new Drive.Builder(
                new NetHttpTransport(),
                new JacksonFactory(),
                credential
        )
                .setApplicationName("My Application")
                .build();

        // Spuštění načítání složek
        new LoadFoldersTask(driveService).execute();
    }

    /**
     * Asynchronní úkol pro načítání složek z Google Drive.
     */
    private static class LoadFoldersTask extends AsyncTask<Void, Void, List<File>> {
        private final Drive driveService;

        public LoadFoldersTask(Drive driveService) {
            this.driveService = driveService;
        }

        @Override
        protected List<File> doInBackground(Void... params) {
            try {
                // Vytvoření požadavku na seznam složek
                Drive.Files.List request = driveService.files().list()
                        .setQ("mimeType = 'application/vnd.google-apps.folder' and trashed = false");
                return request.execute().getFiles();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<File> files) {
            if (files != null && !files.isEmpty()) {
                for (File file : files) {
                    System.out.println("Složka: " + file.getName());
                }
            } else {
                System.out.println("Nepodařilo se načíst složky.");
            }
        }
    }
}