package com.quiktrack.quiktrack;

import android.app.Activity;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

/**
 * Created by Goudam on 12/16/15.
 */
public class NfcReader implements Constants {

    private Context context;

    public NfcReader(Context _context){
        this.context = _context;
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord){
        String tagContent = null;
        try{
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = (payload[0] & 128) == 0? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        }
        catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage(), e);
        }
        return tagContent;
    }

    public String readTextFromTag(NdefMessage ndefMessage) {

        String tagContent = null;
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length > 0){
            NdefRecord ndefRecord = ndefRecords[0];

            tagContent = getTextFromNdefRecord(ndefRecord);
        }
        else{
            Log.d(TAG, "No NDEF records found");
        }

        return tagContent;
    }
}
