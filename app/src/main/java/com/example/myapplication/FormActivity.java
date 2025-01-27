package com.example.myapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FormActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private String currentPhotoPath;
    private static final String TAG = "Zapis";
    private static final String SPREADSHEET_ID = "1B2XoPtQpPKJqrn56u-6IrUFtm1y--0fmLpDgUyTj7Jo"; // ID Google Sheets dokumentu
    private static final String RANGE = "List1"; // Rozsah pro zapisování dat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Button btnAddImage = findViewById(R.id.buttonAddImage);
        EditText editTextSerizovac = findViewById(R.id.editTextSerizovac);
        EditText editTextDate = findViewById(R.id.editTextDate);
        Spinner spinnerShift = findViewById(R.id.spinnerShift);
        RadioGroup radioGroupType = findViewById(R.id.radioGroupType);
        Spinner spinnerWeight = findViewById(R.id.spinnerWeight);
        EditText editTextNumPalets = findViewById(R.id.editTextNumPalets);
        EditText editTextNumNOK = findViewById(R.id.editTextNumNOK);
        Button buttonSend = findViewById(R.id.buttonSend);
        ImageView imageView = findViewById(R.id.imageView);

        // Nastavení Spinner pro směny
        String[] shifts = {"Ranní", "Odpolední", "Noční"};
        ArrayAdapter<String> adapterShift = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, shifts);
        spinnerShift.setAdapter(adapterShift);

        // Nastavení možností váhy podle typu výroby
        String[] weightsKanystry = {"144g", "148g", "160g"};
        String[] weightsMagenta = {"60g"};
        radioGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            ArrayAdapter<String> adapterWeight;
            if (checkedId == R.id.radioButtonKanystry) {
                adapterWeight = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, weightsKanystry);
            } else {
                adapterWeight = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, weightsMagenta);
            }
            spinnerWeight.setAdapter(adapterWeight);
        });

        // Otevření dialogu s kalendářem při kliknutí na pole datum
        editTextDate.setOnClickListener(v -> showDatePicker(editTextDate));

        // Přidání obrázku
        btnAddImage.setOnClickListener(v -> showImagePickerDialog());

        // Odeslání dat
        buttonSend.setOnClickListener(v -> {
            String date = editTextDate.getText().toString();
            String shift = spinnerShift.getSelectedItem().toString();
            String type = radioGroupType.getCheckedRadioButtonId() == R.id.radioButtonKanystry ? "Kanystry" : "Magenta";
            String weight = spinnerWeight.getSelectedItem().toString();
            String numPalets = editTextNumPalets.getText().toString();
            String numNOK = editTextNumNOK.getText().toString();
            String serizovac = editTextSerizovac.getText().toString();

            String message = "Výroba palet:\n" +
                    "Datum: " + date + "\n" +
                    "Směna: " + shift + "\n" +
                    "Typ výroby: " + type + "\n" +
                    "Váha: " + weight + "\n" +
                    "Počet palet: " + numPalets + "\n" +
                    "Počet NOK ks: " + numNOK + "\n" +
                    "Seřizovač: " + serizovac;

            List<List<Object>> data = Arrays.asList(
                    Arrays.asList(date, shift, type, weight, numPalets, numNOK, serizovac)
            );
            new SaveToGoogleSheetTask().execute(data);

            // Odeslání zprávy do WhatsApp
            sendWhatsAppMessage(message, imageView);
        });
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vyberte možnost");
        builder.setItems(new String[]{"Galerie", "Fotoaparát"}, (dialog, which) -> {
            if (which == 0) {
                openGallery();
            } else {
                openCamera();
            }
        });
        builder.show();
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(FormActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else {
            ActivityCompat.requestPermissions(FormActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, CAMERA_REQUEST);
                }
            }
        }
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendWhatsAppMessage(String message, ImageView imageView) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_TEXT, message);

        Uri imageUri = getImageUriFromImageView(imageView);
        if (imageUri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        }

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp není nainstalován.", Toast.LENGTH_LONG).show();
        }
    }

    private Uri getImageUriFromImageView(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        return Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), imageView.getDrawingCache(), "Title", null));
    }

    private void showDatePicker(EditText editTextDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedDay + "." + (selectedMonth + 1) + "." + selectedYear;
            editTextDate.setText(date);
        }, year, month, day);

        datePickerDialog.show();
    }

    private class SaveToGoogleSheetTask extends AsyncTask<List<List<Object>>, Void, Boolean> {
        @Override
        protected Boolean doInBackground(List<List<Object>>... params) {
            try {
                InputStream jsonStream = getResources().openRawResource(R.raw.service_account_key);
                GoogleCredential credential = GoogleCredential.fromStream(jsonStream)
                        .createScoped( List.of ( "https://www.googleapis.com/auth/spreadsheets" ) );

                Sheets sheetsService = new Sheets.Builder(
                        new NetHttpTransport(),
                        GsonFactory.getDefaultInstance(),
                        credential
                ).setApplicationName("Zapis")
                        .build();

                ValueRange body = new ValueRange().setValues(params[0]);

                sheetsService.spreadsheets().values()
                        .append(SPREADSHEET_ID, RANGE, body)
                        .setValueInputOption("RAW")
                        .execute();

                return true;
            } catch (Exception e) {
                Log.e(TAG, "Chyba při zápisu do Google Sheets", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(FormActivity.this, "Data byla úspěšně odeslána!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FormActivity.this, "Chyba při odesílání dat!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}