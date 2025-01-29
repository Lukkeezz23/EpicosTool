package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class ZapisPoruchyActivity extends AppCompatActivity {
    private static final String TAG = "ZapisPoruchyActivity";
    private static final String SPREADSHEET_ID = "15PTdTofFIdI8GdNvIW84lCmKXedGz-zXi7ZNlRxjwmU";
    private static final String RANGE_STROJE = "List1!A:A";
    private static final String RANGE_RESENI = "List1!B:B";
    private static final String SPREADSHEET_ID_DATA = "13Z_yNW4uDPHoaWuPimqWS-DeXRn_GCJ27j14_P4C7ms";
    private static final String RANGE_DATA = "list1!A2:H";
    private static final int REQUEST_PICK_IMAGE = 1;

    private TextView tvUnikatniID;
    private EditText etDatumVzniku, etPopis, etZapisovatel, etKomponent;
    private Spinner spinnerStroj, spinnerReseni;
    private Button btnOdeslat, btnPridatFoto;
    private RecyclerView recyclerViewFotos;
    private FotoAdapter fotoAdapter;
    private List<Uri> seznamFotek = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zapis_poruchy);

        tvUnikatniID = findViewById(R.id.tvUnikatniID);
        etDatumVzniku = findViewById(R.id.etDatumVzniku);
        etPopis = findViewById(R.id.etPopis);
        etZapisovatel = findViewById(R.id.etZapisovatel);
        etKomponent = findViewById(R.id.etKomponent);
        spinnerStroj = findViewById(R.id.spinnerStroj);
        spinnerReseni = findViewById(R.id.spinnerReseni);
        btnOdeslat = findViewById(R.id.btnOdeslat);
        btnPridatFoto = findViewById(R.id.btnPridatFoto);
        recyclerViewFotos = findViewById(R.id.recyclerViewFotos);

        recyclerViewFotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fotoAdapter = new FotoAdapter(seznamFotek);
        recyclerViewFotos.setAdapter(fotoAdapter);

        String datum = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvUnikatniID.setText(datum + "-" + System.currentTimeMillis());
        etDatumVzniku.setText(datum);

        etDatumVzniku.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(ZapisPoruchyActivity.this, (view, year, month, dayOfMonth) ->
                    etDatumVzniku.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        new LoadGoogleSheetData(spinnerStroj, RANGE_STROJE).execute();
        new LoadGoogleSheetData(spinnerReseni, RANGE_RESENI).execute();

        btnOdeslat.setOnClickListener(v -> new SaveToGoogleSheetTask().execute());
    }

    private class LoadGoogleSheetData extends AsyncTask<Void, Void, List<String>> {
        private Spinner spinner;
        private String range;

        public LoadGoogleSheetData(Spinner spinner, String range) {
            this.spinner = spinner;
            this.range = range;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            try {
                InputStream jsonStream = getResources().openRawResource(R.raw.service_account_key);
                GoogleCredential credential = GoogleCredential.fromStream(jsonStream)
                        .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));

                Sheets sheetsService = new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                        .setApplicationName("Zapis")
                        .build();

                ValueRange response = sheetsService.spreadsheets().values()
                        .get(SPREADSHEET_ID, range)
                        .execute();

                List<List<Object>> values = response.getValues();
                List<String> items = new ArrayList<>();
                if (values != null) {
                    for (List<Object> row : values) {
                        items.add(row.get(0).toString());
                    }
                }
                return items;
            } catch (Exception e) {
                Log.e(TAG, "Chyba při načítání Google Sheets", e);
                return Collections.emptyList();
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ZapisPoruchyActivity.this, android.R.layout.simple_spinner_item, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    private class SaveToGoogleSheetTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                InputStream jsonStream = getResources().openRawResource(R.raw.service_account_key);
                GoogleCredential credential = GoogleCredential.fromStream(jsonStream)
                        .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));

                Sheets sheetsService = new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                        .setApplicationName("Zapis")
                        .build();

                String stroj = (spinnerStroj.getSelectedItem() != null) ? spinnerStroj.getSelectedItem().toString() : "NEVYBRÁNO";
                String reseni = (spinnerReseni.getSelectedItem() != null) ? spinnerReseni.getSelectedItem().toString() : "NEVYBRÁNO";

                List<Object> values = Arrays.asList(
                        tvUnikatniID.getText().toString(),
                        etDatumVzniku.getText().toString(),
                        stroj,
                        etKomponent.getText().toString(),
                        reseni,
                        etPopis.getText().toString(),
                        etZapisovatel.getText().toString()
                );

                sheetsService.spreadsheets().values()
                        .append(SPREADSHEET_ID_DATA, RANGE_DATA, new ValueRange().setValues(Collections.singletonList(values)))
                        .setValueInputOption("RAW")
                        .execute();
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Chyba při zápisu do Google Sheets", e);
                return false;
            }
        }
    }
}