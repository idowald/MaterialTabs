package info.androidhive.materialtabs.util;

import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by ido on 01/12/2015.
 */
public class SaveInBackGround  <T extends Parsable> implements SaveCallback {
    //TODO get a callback function that is triggered when the object got its ObjectId

    /*
    this class implements the saving in parse cloud and then retrieving the object id to the object
    afterwards it saves the object on localDrive

    -after the saving the object gets his objectID
    -after it saved in cloud it's also saved in localdrive
     */
    T object = null;
    ParseObject parseObject= null;

    public SaveInBackGround(T parsableObject) {
        this.parseObject = parsableObject.ToParseObject();
        object = parsableObject;



        parseObject.saveInBackground(this); //when finish will go to done method
    }

    @Override
    public void done(com.parse.ParseException e) {
        if (e== null)
        {
            object.SetObjectId(parseObject.getObjectId());
            //this is the important thing:
            parseObject.pinInBackground(); //saving in the local drive!
        }
        else{
            e.printStackTrace();
        }
    }
}