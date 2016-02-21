package info.androidhive.materialtabs.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ido on 30/11/2015.
 */
public class SendSMS extends Thread {
    //this class send a sms message to destination contain the text "CONDOC!
    //if needed, this class can return result of sending message in future from the input
    String destination;
    String text;

    public SendSMS(String destination, String text) {
        this.destination = destination;
        this.text = text;
    }

    @Override
    public void run() {


        Log.v("sendSMS input:", "sending");


        try {
            String urlParameters  = "userid=idow&password=12345678&to="+destination +"&text="+text;
            String request        = "https://secure.telemessage.com/jsp/receiveSMS.jsp?" +urlParameters;
            Log.v("SendSMS: request", request);
            URL obj = new URL(request);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
          //  con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // print result
                System.out.println(response.toString());
            } else {
                System.out.println("GET request not worked");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
