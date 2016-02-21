package info.androidhive.materialtabs;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by ido on 30/11/2015.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "YTmi0W4Neoxw3sFOCv1D6ugok04kD6fIqddorHGC", "IJZybbLL3CPe0lWb2cLHyDkhN3bvYOV7kfTCdS9y");

    }
}
