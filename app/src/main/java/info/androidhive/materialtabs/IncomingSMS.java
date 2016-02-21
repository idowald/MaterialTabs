package info.androidhive.materialtabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import info.androidhive.materialtabs.activity.MainActivity;

/**
 * Created by ido on 30/11/2015.
 */
public class IncomingSMS extends BroadcastReceiver {
    //TEST if

    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {
        /*
        recieves incoming messages from phone-
        if see a message starts with "condoc code!" it's a sign up code
         */
        Bundle myBundle = intent.getExtras();
        SmsMessage[] messages = null;
        String strMessage = "";
        String text = "";
        if (myBundle != null) {
            Object[] pdus = (Object[]) myBundle.get("pdus");
            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                strMessage += "SMS From: " + messages[i].getOriginatingAddress();
                strMessage += " : ";
                strMessage += messages[i].getMessageBody();
                text+= messages[i].getMessageBody();
                strMessage += "\n";
            }

            Log.v("new sms message", text);
            if (text.startsWith("CONDOC!")) {

               int code= Integer.parseInt(text.substring(7));
                SharedPreferences userDetails =context.getSharedPreferences("userdetails", context.MODE_PRIVATE);
                SharedPreferences.Editor edit = userDetails.edit();
               int system_code= userDetails.getInt("code",0);
             if (system_code == code){


                 edit.putBoolean("login", true);

                 edit.commit();
                Intent intent2 = new Intent(context,MainActivity.class);
                 intent2.addFlags(intent2.FLAG_ACTIVITY_NEW_TASK);
                 context.startActivity(intent2);

             }

            }


        }
    }


    public void SendSMS(String target, String text) {


        sms.sendTextMessage(target, null, text, null, null);


    }
}
