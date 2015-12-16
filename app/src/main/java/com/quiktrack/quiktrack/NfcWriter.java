package com.quiktrack.quiktrack;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Created by Goudam on 12/16/15.
 */
public class NfcWriter implements Constants{

    private Context context;

    public NfcWriter(Context _context){
        this.context = _context;
    }

    public void formatTag(Tag tag, NdefMessage ndefMessage){
        try{
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if(ndefFormatable == null){
                Log.e(TAG, "Tag is not ndef formatable!");
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
        }
        catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    public void writeNdefMessage(Tag tag, NdefMessage ndefMessage){
        try{

            if(tag == null){
                Log.e(TAG, "Tag object is null");
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if(ndef == null){
                formatTag(tag, ndefMessage);
            }
            else{
                ndef.connect();
                if(!ndef.isWritable()){
                    Log.e(TAG, "Tag is not writable");
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Log.d(TAG, "Tag written");
            }
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content){
        try{
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1 + languageSize + textLength);

            byteArrayOutputStream.write((byte) (languageSize & 0x1F));
            byteArrayOutputStream.write(language, 0, languageSize);
            byteArrayOutputStream.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], byteArrayOutputStream.toByteArray());
        }
        catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    public NdefMessage createNdefMessage(String content){

        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ ndefRecord });

        return ndefMessage;
    }
}
