package info.androidhive.materialtabs.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.SendSMS;


public class LoginActivity extends Activity {

    private  EditText number;
    private   Button button;
    private int password;

    SharedPreferences userDetails;
    SharedPreferences.Editor edit;
    String destination="";

    private boolean backdoor= false; //makes entrance without SMS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    /*
    this class sending sms of confirm number.
    it checks in the database if the number is allowed
    send sms through https server secured with the code to the mobile.
    the sms will be sent only once by the "sensSMS" paramater. if true- you can send sms, if false. it's locked
     */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         userDetails = getApplicationContext().getSharedPreferences("userdetails", MODE_PRIVATE);
        edit = userDetails.edit();
        //this makes sure that it will be able to send message only once!
        edit.putBoolean("sendSMS",true);
        //generate secret code and store it in android
         password =(int)(120 + (int)(Math.random() * ((10000 - 120) + 1)));
        edit.putInt("code", password);
        edit.commit();



         button= (Button) findViewById(R.id.buttonLogin);
         number= (EditText) findViewById(R.id.phoneinput);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (userDetails.getBoolean("sendSMS", true)) {

                     destination = number.getText().toString();

                    if (destination.length() == 11+5){
                        if (destination.substring(11).equals("12345"))
                            backdoor= true;
                        destination = destination.substring(0,11);
                        Log.v("backdoor",backdoor+destination);
                    }
                    if (destination.length() >= 11) {
                        Log.v("LoginActivity","testing if user is permitted");
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
                        //query.whereEqualTo("username",destination);

                        query.getFirstInBackground(new GetCallback<ParseObject>() {

                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {
                                    Log.v("LoginActivity", "user found sending secret code");
                                    new SendSMS(destination, "CONDOC!"+password).start();

                                   // SendSMS(destination, "CONDOC code! " + password);
                                    edit.putBoolean("sendSMS", false);
                                    edit.putString("username", destination);
                                    User user = new User(parseObject);
                                    edit.putString("userObjectId",parseObject.getObjectId());
                                    edit.putString("first_name",user.getFirstName());
                                    edit.putString("last_name",user.getLastName());

                                    edit.commit();
                                    //startActivity(new Intent(getApplicationContext(),MainActivity.class));

                                    button.setEnabled(backdoor);
                                } else { //username wasn't found
                                    Log.v("LoginActivity", "something went wrong" + e.toString());
                                    Toast.makeText(getApplicationContext(), "Error, Number isn't registered. please contact Admin", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                    } else //if number too short!
                        Toast.makeText(getApplicationContext(), "Error, too short number.", Toast.LENGTH_LONG).show();





                }
            }
        });


    }


    private void SendSMS(final String destination, final String text){


       // mWebview = (WebView) findViewById(R.id.webView);
       // mWebview.getSettings().setJavaScriptEnabled(true);
        String url = "https://secure.telemessage.com/jsp/receiveSMS.jsp";
        String postData = "userid=idow&password=12345678&to="+destination+"&text="+text;
      //  mWebview.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));*//*
        Log.v("in SendSMS",destination+text);

        new Thread() {

            @Override
            public void run() {


                    try {
                        String urlParameters = "userid=idow&password=12345678&to=" + destination + "&text=" + text;
                        String request = "https://secure.telemessage.com/jsp/receiveSMS.jsp?" + urlParameters;
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
        }.start();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }


}
