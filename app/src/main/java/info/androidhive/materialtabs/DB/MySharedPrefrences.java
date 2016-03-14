package info.androidhive.materialtabs.DB;

import android.content.Context;
import android.content.SharedPreferences;

import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;

/**
 * Created by ido on 07/03/2016.
 */
public class MySharedPrefrences {


    public static User getUser(Context context){
        /*
        returns current username, last name, first name
         */
        User user = null;
        SharedPreferences userDetails = context.getSharedPreferences("userdetails", context.MODE_PRIVATE);
        String username= userDetails.getString("username","");
        String first_name= userDetails.getString("first_name","");
        String last_name= userDetails.getString("last_name","");
        user = new User(first_name, last_name, username, new AddParseObject() {
            @Override
            public void AddObject(AbstractParseObject object) {

            }
        });

        return user;
    }
}
