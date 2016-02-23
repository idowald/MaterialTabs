package info.androidhive.materialtabs.util;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Created by ido on 01/12/2015.
 */
public class GetCallbackObject implements GetCallback<ParseObject>{
    /*
    this class used on an outside query that activates it.
    it's don't the logic part of saving and not quering on specific thing


     */
   private AbstractParseObject ParsableObject;
    AddParseObject callback =null;
    public GetCallbackObject(AbstractParseObject parsableObject, AddParseObject callback) {
        this.callback= callback;
        ParsableObject = parsableObject;

    }

    @Override
    public void done(ParseObject parseObject, ParseException e) {

        if (e == null)
        {
            ParsableObject.GenerateFromParseObject(parseObject);
            if (callback !=null)
            callback.AddObject(ParsableObject);
        }else{
         e.printStackTrace();
        }
    }
}
