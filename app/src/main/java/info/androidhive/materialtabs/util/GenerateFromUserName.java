package info.androidhive.materialtabs.util;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import info.androidhive.materialtabs.objects.User;


/**
 * Created by ido on 02/12/2015.
 */
public class GenerateFromUserName implements GetCallback<ParseObject>{
    /**
     * this class helps to find users by username
     */
    private User user;
    private AddParseObject CallMethod= null;

    public GenerateFromUserName(User object) {
        this(object,null);
    }

    public GenerateFromUserName(User user, AddParseObject callMethod) {
        this.user = user;
        CallMethod = callMethod;
        Log.v("generate", "GenerateFromUserName");

        ParseQuery<ParseObject> query  = ParseQuery.getQuery(user.getTableName()); //for class Remark it will go to Remarks
        query.whereEqualTo("userName", user.getUserName());
        //query.fromLocalDatastore();

        query.getFirstInBackground(this);
    }



    @Override
    public void done(ParseObject parseObject, ParseException e) {
        Log.v("generate", "GenerateFromUserName done");
        if (e == null)
        {
                user.GenerateFromParseObject(parseObject);
                if (CallMethod!= null)
                    CallMethod.AddObject(user);

                ParseQuery<ParseObject> query  = ParseQuery.getQuery(user.getTableName()); //for class Remark it will go to Remarks
                query.whereEqualTo("userName", user.getUserName());

                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if ( e == null)
                        {
                                user.GenerateFromParseObject(parseObject);
                                if (CallMethod!= null)
                                    CallMethod.AddObject(user);

                        } else {
                            //not found in cloud- can't be. a user that found in local but not in db
                            e.printStackTrace();
                        }
                    }
                });
            }
         else{
            //not found Username in local Drive. search in cloud:
            ParseQuery<ParseObject> query  = ParseQuery.getQuery(user.getTableName()); //for class Remark it will go to Remarks
            query.whereEqualTo("userName", user.getUserName());

            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if ( e == null)
                    {
                        user.GenerateFromParseObject(parseObject);
                        if (CallMethod!= null)
                            CallMethod.AddObject(user);

                    } else {
                      //not exist nor on local or db!
                        e.printStackTrace();
                    }
                }
            });

        }
    }
}
