package com.quiktrack.quiktrack;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AddItemActivity extends AppCompatActivity implements Constants{

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;

    private ArrayList<String> tags;
    private File photoFile;
    private EditText editTextItemName;
    private Spinner spinnerTags;
    private Button buttonCamera;
    private ImageView imageViewItemPicture;
    private Button buttonSaveItem;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        tags = new ArrayList<>();
        photoFile = null;
        editTextItemName = (EditText) findViewById(R.id.editTextItemName);
        spinnerTags = (Spinner) findViewById(R.id.spinnerTags);
        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        imageViewItemPicture = (ImageView) findViewById(R.id.imageViewItemPicture);
        buttonSaveItem = (Button) findViewById(R.id.buttonSaveItem);
        mCurrentPhotoPath = "";

        new LoadTagsTask().execute();

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        buttonSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageViewItemPicture.setImageURI(Uri.fromFile(photoFile));
            imageViewItemPicture.setVisibility(View.VISIBLE);
            buttonCamera.setVisibility(View.INVISIBLE);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    // performs database query outside GUI thread
    private class LoadTagsTask extends AsyncTask<String, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(AddItemActivity.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(String... params)
        {
            databaseConnector.open();

            return databaseConnector.getAllTags();
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);

            tags.add("--");

            while(result.moveToNext()){

                // get the column index for each data item
                int tagsIndex = result.getColumnIndex("tag");
                Log.d(TAG, ""+tagsIndex);

                tags.add(result.getString(tagsIndex));

                result.close();
                databaseConnector.close();
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddItemActivity.this, R.layout.support_simple_spinner_dropdown_item, tags.toArray(new String[tags.size()]));
            spinnerTags.setAdapter(arrayAdapter);

        }
    }

    private void saveItem(){
        if (editTextItemName.getText().length() != 0)
        {
            AsyncTask<Object, Object, Object> saveTagTask =
                    new AsyncTask<Object, Object, Object>()
                    {
                        @Override
                        protected Object doInBackground(Object... params)
                        {
                            saveTag();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object result)
                        {
                            Toast.makeText(AddItemActivity.this, "Item saved", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    };

            saveTagTask.execute((Object[]) null);
        }
        else
        {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(AddItemActivity.this);

            builder.setTitle(R.string.string_error_itemSaveTitle);
            builder.setMessage(R.string.string_error_itemSaveMessage);
            builder.setPositiveButton(R.string.string_error_itemSavePositiveButton, null);
            builder.show();
        }
    }

    private void saveTag()
    {
        DatabaseConnector databaseConnector = new DatabaseConnector(this);

        databaseConnector.insertTag(
                editTextItemName.getText().toString(),
                spinnerTags.getSelectedItem().toString(),
                mCurrentPhotoPath);
    }




}
