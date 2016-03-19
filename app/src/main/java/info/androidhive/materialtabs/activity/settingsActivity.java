package info.androidhive.materialtabs.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;

import info.androidhive.materialtabs.LanguagePrefrences;
import info.androidhive.materialtabs.MyApplication;
import info.androidhive.materialtabs.R;

public class settingsActivity extends AppCompatActivity {

    static final String ALERT_TEXT= "MY_SHARED_PREFRENCES";
    static final String INTERVAL_KEY= "MY_INTERVAL_KEY";
    static final String NOTIFICATION_KET= "MY_NOTIFICATION_KEY";

    EditText intervalText= null;
    CheckBox notificationSwitch= null;
    Spinner LanguageSpinner = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intervalText = (EditText) findViewById(R.id.interval_time);

        notificationSwitch = (CheckBox) findViewById(R.id.switch1);
        LanguageSpinner = (Spinner) findViewById(R.id.language_spinner);

        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, android.R.layout.simple_spinner_item);
        LanguageSpinner.setAdapter(spinner_adapter);


        Button save_button= (Button) findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intervalText.getText().length()== 0) //no input
                {
                    Toast.makeText(v.getContext(),"please enter interval time",Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences preferences = v.getContext().getSharedPreferences(ALERT_TEXT,v.getContext().MODE_PRIVATE );
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(INTERVAL_KEY,Integer.parseInt(intervalText.getText().toString()) );
                editor.putBoolean(NOTIFICATION_KET, notificationSwitch.isChecked());
                editor.commit();
                String language = null;
                switch (LanguageSpinner.getSelectedItemPosition()){
                    case (1): //hebrew
                        language= "he";
                        break;
                    case(0) : //english
                        language = "en";
                        break;
                    default :
                        language="he";
                }
                LanguagePrefrences.setLanguagesPrefrences(language);

                Toast.makeText(v.getContext(),"saved",Toast.LENGTH_LONG).show();

            }
        });

        intervalText.setText(""+getIntervalTime());
        notificationSwitch.setChecked(isNotificationOn());
        String language = LanguagePrefrences.getLanguagePrefrences();
        if (language.startsWith("he")||language.startsWith("iw") )
        {
            LanguageSpinner.setSelection(1);
        }
        else{
            LanguageSpinner.setSelection(0);
        }





    }

    public static boolean isNotificationOn(){
        Context context = MyApplication.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(ALERT_TEXT,context.MODE_PRIVATE );
        return preferences.getBoolean(NOTIFICATION_KET,true);
    }

    public static int getIntervalTime(){
        /*
        if 1 it's not saved yet so it's default value
         */
        Context context = MyApplication.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(ALERT_TEXT,context.MODE_PRIVATE );
        int num = preferences.getInt(INTERVAL_KEY, 1);

        return num;
    }

}
