package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private String currentPhotoPath;

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

        // Set onClickListener for "Add Image" button
        btnAddImage.setOnClickListener(v -> showImagePickerDialog());

        // Spinner for shifts
        String[] shifts = {"Ranní", "Odpolední", "Noční"};
        ArrayAdapter<String> adapterShift = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, shifts);
        spinnerShift.setAdapter(adapterShift);

        // Adjust weight options based on production type
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

        // Open calendar dialog when clicking on date field
        editTextDate.setOnClickListener(v -> showDatePicker(editTextDate));

        // Send the data via WhatsApp
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

            sendWhatsAppMessage(message, imageView); // Add image to WhatsApp
        });

        // Request permissions if needed
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = findViewById(R.id.imageView);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                Uri selectedImageUri = data.getData();
                imageView.setImageURI(selectedImageUri);
            } else if (requestCode == CAMERA_REQUEST) {
                File imgFile = new File(currentPhotoPath);
                if (imgFile.exists()) {
                    imageView.setImageURI(Uri.fromFile(imgFile));
                }
            }
        }
    }

    private void sendWhatsAppMessage(String message, ImageView imageView) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_TEXT, message);

        // Attach the image from the imageView to the intent
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
        // Get the URI from ImageView if available
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        return Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), imageView.getDrawingCache(), "Title", null));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, "Oprávnění bylo zamítnuto", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePicker(EditText editTextDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            Log.d("FormActivity", "Datum vybráno: " + selectedDay + "." + (selectedMonth + 1) + "." + selectedYear);
            String date = selectedDay + "." + (selectedMonth + 1) + "." + selectedYear;
            editTextDate.setText(date);
        }, year, month, day);

        datePickerDialog.show();
    }
}