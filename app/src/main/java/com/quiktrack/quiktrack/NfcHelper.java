package com.quiktrack.quiktrack;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;

/**
 * Created by Goudam on 12/16/15.
 */
public class NfcHelper {

    private Context context;
    private NfcAdapter nfcAdapter;

    public NfcHelper(Context _context){
        this.context = _context;
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(_context);
    }

    public void enableForegroundDispatchSystem(){
        Intent intent = new Intent(context, AddTagActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch((Activity) context, pendingIntent, intentFilters, null);
    }

    public void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch((Activity) context);
    }

}
