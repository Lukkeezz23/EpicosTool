package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "Zapis";
    private static final String SPREADSHEET_ID = "1B2XoPtQpPKJqrn56u-6IrUFtm1y--0fmLpDgUyTj7Jo";
    private static final String RANGE = "List1!A2:G";

    private HistoryAdapter historyAdapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyAdapter = new HistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(historyAdapter);

        fetchGoogleSheetData();
    }

    private void fetchGoogleSheetData() {
        executor.execute(() -> {
            try {
                InputStream jsonStream = getResources().openRawResource(R.raw.service_account_key);
                GoogleCredentials credentials = GoogleCredentials.fromStream(jsonStream)
                        .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets.readonly"));

                Sheets sheetsService = new Sheets.Builder(
                        new NetHttpTransport(),
                        GsonFactory.getDefaultInstance(),
                        new HttpCredentialsAdapter(credentials)
                ).setApplicationName("Zapis").build();

                ValueRange response = sheetsService.spreadsheets().values()
                        .get(SPREADSHEET_ID, RANGE)
                        .execute();

                List<List<Object>> values = response.getValues();

                mainHandler.post(() -> {
                    if (values != null && !values.isEmpty()) {
                        historyAdapter.updateData(values);
                    } else {
                        Toast.makeText(HistoryActivity.this, "Žádná data k zobrazení", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Chyba při čtení z Google Sheets: " + e.getMessage(), e);
                mainHandler.post(() -> Toast.makeText(HistoryActivity.this, "Chyba při načítání dat", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private final List<List<Object>> data;

        public HistoryAdapter(List<List<Object>> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            List<Object> row = data.get(position);
            holder.dateShift.setText(String.format("%s / %s / %s",
                    row.size() > 0 ? row.get(0).toString() : "N/A",
                    row.size() > 1 ? row.get(1).toString() : "N/A",
                    row.size() > 6 ? row.get(6).toString() : "N/A"));

            holder.palletNok.setText(String.format("Palet: %s / NOK: %s",
                    row.size() > 5 ? row.get(5).toString() : "0",
                    row.size() > 4 ? row.get(4).toString() : "0"));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void updateData(List<List<Object>> newData) {
            data.clear();
            data.addAll(newData);
            notifyDataSetChanged();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView dateShift;
            final TextView palletNok;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                dateShift = itemView.findViewById(R.id.textDateShiftOperator);
                palletNok = itemView.findViewById(R.id.textPaletNok);
            }
        }
    }
}
