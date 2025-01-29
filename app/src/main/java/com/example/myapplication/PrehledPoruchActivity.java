package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PrehledPoruchActivity extends AppCompatActivity {

    private static final String TAG = "PrehledPoruchActivity";
    private static final String SPREADSHEET_ID = "13Z_yNW4uDPHoaWuPimqWS-DeXRn_GCJ27j14_P4C7ms";  // 🔹 Nahraď ID tabulky
    private static final String RANGE = "Data!A2:H"; // 🔹 Název listu + rozsah
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private Sheets sheetsService;

    private ListView listView;
    private Spinner spinnerFiltr, spinnerMesic;
    private ArrayAdapter<String> adapter;
    private List<String> poruchyList = new ArrayList<>();
    private List<List<Object>> vsechnyPoruchy = new ArrayList<>();
    private String vybranyFiltr = "Vše";
    private String vybranyMesic = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Calendar.getInstance().getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prehled_poruch);

        listView = findViewById(R.id.listView);
        spinnerFiltr = findViewById(R.id.spinnerFiltr);
        spinnerMesic = findViewById(R.id.spinnerMesic);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, poruchyList);
        listView.setAdapter(adapter);

        // Nastavení filtrů
        nastavFiltry();

        // Načtení dat v novém vlákně
        new Thread(this::nacistData).start();
    }

    private void nastavFiltry() {
        // Spinner pro filtraci stavu
        ArrayAdapter<String> filtrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Vše", "Vyřešeno", "V řešení"});
        filtrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltr.setAdapter(filtrAdapter);
        spinnerFiltr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vybranyFiltr = parent.getItemAtPosition(position).toString();
                aktualizovatSeznam();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner pro výběr měsíce
        ArrayAdapter<String> mesicAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, generovatMesice());
        mesicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMesic.setAdapter(mesicAdapter);
        spinnerMesic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vybranyMesic = parent.getItemAtPosition(position).toString();
                aktualizovatSeznam();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private List<String> generovatMesice() {
        List<String> mesice = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 12; i++) {
            mesice.add(new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.getTime()));
            cal.add(Calendar.MONTH, -1);
        }
        return mesice;
    }

    private void nacistData() {
        try {
            // Inicializace Google Sheets API
            InputStream credentialsStream = getResources().openRawResource(R.raw.service_account_key);
            HttpTransport httpTransport = new NetHttpTransport ();
            GoogleCredential credential = GoogleCredential.fromStream(credentialsStream)
                    .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets"));

            sheetsService = new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName("MyApplication")
                    .build();

            // Načtení dat
            ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, RANGE).execute();
            vsechnyPoruchy = response.getValues();

            runOnUiThread(this::aktualizovatSeznam);
        } catch (IOException e) {
            Log.e(TAG, "Chyba při načítání dat", e);
            runOnUiThread(() -> Toast.makeText(this, "❌ Chyba při načítání dat!", Toast.LENGTH_SHORT).show());
        }
    }

    private void aktualizovatSeznam() {
        poruchyList.clear();
        for (List<Object> porucha : vsechnyPoruchy) {
            if (porucha.size() < 7) continue; // Ochrana před nekompletními řádky

            String id = porucha.get(0).toString();
            String datum = porucha.get(1).toString();
            String stroj = porucha.get(2).toString();
            String komponent = porucha.get(3).toString();
            String reseni = porucha.get(4).toString();
            String popis = porucha.get(5).toString();
            String stav = porucha.get(6).toString();

            // Filtr podle měsíce
            if (!datum.startsWith(vybranyMesic)) continue;

            // Filtr podle stavu
            if (vybranyFiltr.equals("Vyřešeno") && !stav.equals("Vyřešeno")) continue;
            if (vybranyFiltr.equals("V řešení") && !(stav.equals("V řešení") || stav.equals("Externí servis"))) continue;

            // Ikona podle stavu
            String ikona;
            if (stav.equals("Vyřešeno")) {
                ikona = "✅";
            } else if (stav.equals("V řešení")) {
                ikona = "🟡";
            } else {
                ikona = "🟠";
            }

            poruchyList.add(id + " | " + datum + " | " + stroj + " | " + komponent + " | " + ikona);
        }
        adapter.notifyDataSetChanged();
    }
}
