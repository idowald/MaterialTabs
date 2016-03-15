package info.androidhive.materialtabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Created by ido on 15/03/2016.
 */
public class LanguagePrefrences {
    /**
     * this class help to change language settings.
     * it can return hebrew/english/null(default)
     */
    public final static  String[] Languages ={"hebrew","english"};

    public final static String PREFRENCES_KEY= "LANGUAGE_PREF_KEY";
    public final static String LANGUAGE_KEY= "LANGUAGE_KEY";



    public static String getLanguagePrefrences(){

        Context context=MyApplication.getAppContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFRENCES_KEY, context.MODE_PRIVATE);
        String language= sharedPreferences.getString(LANGUAGE_KEY,Locale.getDefault().getDisplayLanguage());
        return language;
    }
    public static void setLanguagesPrefrences(String langauge){
        Context context=MyApplication.getAppContext();
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFRENCES_KEY, context.MODE_PRIVATE).edit();
        editor.putString(LANGUAGE_KEY,langauge);
        editor.commit();

        setConfigurationLanguage();


    }

    public static void setConfigurationLanguage(){
        /*
        need to be called on start of application and when the language prefrences is being changed
        ( directly from SetLanguagePref method!)

         */
        Context context = MyApplication.getAppContext();
    String language = getLanguagePrefrences();
        Locale selectedLocale = null;

            if (language.startsWith("he") || language.startsWith("iw")) //hebrew
            {
                selectedLocale = new Locale("he");
            } else { //english
                selectedLocale = new Locale("en");

            }
        Configuration config = new Configuration();
        config.locale = selectedLocale; // set accordingly
        Locale.setDefault(selectedLocale); // has no effect
        Resources res = context.getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());

    }
}
