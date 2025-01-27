package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "Zapis";
    private static final String SPREADSHEET_ID = "1B2XoPtQpPKJqrn56u-6IrUFtm1y--0fmLpDgUyTj7Jo";
    private static final String RANGE = "List1";

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyAdapter = new HistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(historyAdapter);

        new FetchGoogleSheetDataTask().execute();
    }

    private class FetchGoogleSheetDataTask extends AsyncTask<Void, Void, List<List<Object>>> {
        @Override
        protected List<List<Object>> doInBackground(Void... voids) {
            try {
                InputStream jsonStream = getResources().openRawResource(R.raw.service_account_key);
                GoogleCredential credential = GoogleCredential.fromStream(jsonStream)
                        .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets"));

                Sheets sheetsService = new Sheets.Builder(
                        new NetHttpTransport(),
                        GsonFactory.getDefaultInstance(),
                        credential
                ).setApplicationName("Zapis")
                        .build();

                ValueRange response = sheetsService.spreadsheets().values()
                        .get(SPREADSHEET_ID, RANGE)
                        .execute();

                return response.getValues();
            } catch (Exception e) {
                Log.e(TAG, "Chyba při čtení z Google Sheets", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<List<Object>> data) {
            if (data != null && !data.isEmpty()) {
                historyAdapter.updateData(data);
            } else {
                Toast.makeText(HistoryActivity.this, "Žádná data k zobrazení", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter {
        public <E> HistoryAdapter(ArrayList<E> es) {
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public void updateData(List<List<Object>> data) {
        }
    }
}
