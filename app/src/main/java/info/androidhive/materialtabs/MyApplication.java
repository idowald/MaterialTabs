package info.androidhive.materialtabs;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

/**
 * Created by ido on 30/11/2015.
 */
public class MyApplication extends Application {

    private static Context context = null;
    @Override
    public void onCreate() {
        super.onCreate();
        context= getApplicationContext();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "YTmi0W4Neoxw3sFOCv1D6ugok04kD6fIqddorHGC", "IJZybbLL3CPe0lWb2cLHyDkhN3bvYOV7kfTCdS9y");
        LanguagePrefrences.setConfigurationLanguage(); //being called to make sure that every time the app will fly the correct language


    }
    public static Context getAppContext() {
        return MyApplication.context;
    }
}
