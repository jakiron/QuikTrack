package com.quiktrack.quiktrack;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;


public class AddTagActivity extends AppCompatActivity implements Constants {

    private boolean writeEnabled;

    private NfcHelper nfcHelper;
    private NfcWriter nfcWriter;
    private NfcReader nfcReader;

    private Animation slide_in_left;
    private Animation slide_out_right;

    private ViewSwitcher viewSwitcher;
    private ToggleButton toggleButtonScanTag;
    private EditText editTextTagContent;
    private Button buttonWriteToTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        writeEnabled = false;

        nfcHelper = new NfcHelper(this);
        nfcWriter = new NfcWriter(this);
        nfcReader = new NfcReader(this);

        slide_in_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        viewSwitcher.setInAnimation(slide_in_left);
        viewSwitcher.setOutAnimation(slide_out_right);

        toggleButtonScanTag = (ToggleButton) findViewById(R.id.toggleButtonScanTag);
        editTextTagContent = (EditText) findViewById(R.id.editTextTagContent);
        buttonWriteToTag = (Button) findViewById(R.id.buttonWriteToTag);

        toggleButtonScanTag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggleButtonScanTag.setTextOn(getResources().getString(R.string.string_togglebutton_on));
                } else {
                    toggleButtonScanTag.setTextOff(getResources().getString(R.string.string_togglebutton_off));
                }
            }
        });

        buttonWriteToTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeEnabled = true;
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

            if (toggleButtonScanTag.isChecked()) {
                if(viewSwitcher.getDisplayedChild() == 0){
                    viewSwitcher.showNext();
                }
                if(!writeEnabled){

                    Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                    if (parcelables != null && parcelables.length > 0) {
                        editTextTagContent.setText(nfcReader.readTextFromTag((NdefMessage) parcelables[0]));
                    } else {
                        Toast.makeText(this, "No NDEF messages found", Toast.LENGTH_SHORT).show();
                        editTextTagContent.setText("");
                    }
                } else {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    String tagContent = editTextTagContent.getText() + "";
                    NdefMessage ndefMessage = nfcWriter.createNdefMessage( tagContent + "");

                    nfcWriter.writeNdefMessage(tag, ndefMessage);

                    DatabaseConnector databaseConnector = new DatabaseConnector(this);
                    databaseConnector.insertTag(tagContent, "", "");

                    toggleButtonScanTag.setChecked(false);
                    writeEnabled = false;

                    Toast.makeText(this, "Tag saved", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

        }
    }
}
