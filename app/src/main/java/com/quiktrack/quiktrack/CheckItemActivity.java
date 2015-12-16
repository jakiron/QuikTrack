package com.quiktrack.quiktrack;

import android.content.Intent;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;


public class CheckItemActivity extends AppCompatActivity implements Constants {

    private HashMap<String,String> tags;

    private NfcHelper nfcHelper;
    private NfcWriter nfcWriter;
    private NfcReader nfcReader;

    private Spinner spinnerItems;
    private ToggleButton toggleButtonCheckItem;
    private ImageView imageViewCheckStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_item);

        tags = new HashMap<>();

        nfcHelper = new NfcHelper(this);
        nfcWriter = new NfcWriter(this);
        nfcReader = new NfcReader(this);

        spinnerItems = (Spinner) findViewById(R.id.spinnerItems);
        toggleButtonCheckItem = (ToggleButton) findViewById(R.id.toggleButtonCheckItem);
        imageViewCheckStatus = (ImageView) findViewById(R.id.imageViewCheckStatus);

        new LoadTagsTask().execute();

        toggleButtonCheckItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggleButtonCheckItem.setTextOn(getResources().getString(R.string.string_togglebutton_on));
                } else {
                    toggleButtonCheckItem.setTextOff(getResources().getString(R.string.string_togglebutton_off));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcHelper.enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        nfcHelper.disableForegroundDispatchSystem();
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)) {

            if (toggleButtonCheckItem.isChecked()) {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if (parcelables != null && parcelables.length > 0) {
                    String nfcContent = nfcReader.readTextFromTag((NdefMessage) parcelables[0]);
                    String item = spinnerItems.getSelectedItem().toString();
                    if(tags.get(item).equalsIgnoreCase(nfcContent)){
                        imageViewCheckStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick));
                    }
                    else{
                        imageViewCheckStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_wrong));
                    }
                } else {
                    Toast.makeText(this, "No messages found in tag", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    // performs database query outside GUI thread
    private class LoadTagsTask extends AsyncTask<String, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(CheckItemActivity.this);

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

            tags.put("-1", "--");

            while(result.moveToNext()){

                // get the column index for each data item
                int tagsIndex = result.getColumnIndex("tag");
                int itemsIndex = result.getColumnIndex("item");

                Log.d(TAG, ""+ result.toString());

                tags.put(result.getString(itemsIndex), result.getString(tagsIndex));

                result.close();
                databaseConnector.close();
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CheckItemActivity.this, R.layout.support_simple_spinner_dropdown_item, tags.keySet().toArray(new String[tags.size()]));
            spinnerItems.setAdapter(arrayAdapter);

        }
    }
}
