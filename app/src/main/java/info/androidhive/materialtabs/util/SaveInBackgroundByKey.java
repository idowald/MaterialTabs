package info.androidhive.materialtabs.util;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * Created by ido on 29/12/2015.
 */
public class SaveInBackgroundByKey implements SaveCallback,GetCallback<ParseObject> {
    AbstractParseObject object = null;
    ParseObject parseObject= null;
    String primary_key= null;
    String value= null;
    AddParseObject callback = null;

    public SaveInBackgroundByKey( AbstractParseObject parsableObject, String key) {
        this(parsableObject,key, null);
    }


    public SaveInBackgroundByKey( AbstractParseObject parsableObject, String key, AddParseObject callback) {
        primary_key = key;
        this.callback = callback;
        this.parseObject = parsableObject.ToParseObject();
        object = parsableObject;
        value= parseObject.getString(primary_key);
       saveItem();

    }


    public void saveItem(){


        ParseQuery<ParseObject> query= ParseQuery.getQuery(object.getTableName());
        query.whereEqualTo(primary_key,value);
        query.getFirstInBackground(this);

    }

    @Override //first we look for instance in DB and only if no one exist- create new instance
    public void done(ParseObject parseObject, ParseException e) {
        if (e== null)
        { //found the object
            object.SetObjectId(parseObject.getObjectId());
            object.ToParseObject().saveInBackground(this);
            if (callback!= null)
                callback.AddObject(object);
        }else{
            //not found
            this.parseObject.saveInBackground(this); //when finish will go to done method
        }
    }

    @Override
    public void done(ParseException e) {
        if (e== null)
        {
           // object.SetObjectId(parseObject.getObjectId());
            //this is the important thing:
            parseObject.pinInBackground(); //saving in the local drive!
            if (callback!= null)
                callback.AddObject(object);
        }
        else{
            e.printStackTrace();
        }
    }

}
