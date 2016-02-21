package info.androidhive.materialtabs.util;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by ido on 02/12/2015.
 */
public class GenerateFromObjectId <T extends Parsable> implements GetCallback<ParseObject> {
    /*
  utility i've created.
  give your DB table name and i'll create your object from objectid
  first locate it in localdrive, if exist or not, afterwards lookup in the DB for a refreshed version!
   */
    private T object;

    private AddParseObject<T> CallMethod= null;

    public GenerateFromObjectId(T object) {

        this.object = object;


    }

    public GenerateFromObjectId(T object,  AddParseObject<T> callMethod) {
        this.object = object;

        CallMethod = callMethod;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(object.getTableName()); //for class Remark it will go to Remarks

        query.fromLocalDatastore();

        //GetCallbackObject callback = new GetCallbackObject(object, query);
        //when finish callback it calls the GenerateFromParseObject interface function
        query.getInBackground(object.GetObjectId(), this);

    }

    @Override
    public void done(ParseObject parseObject, ParseException e) {


        if (e == null)
        {
            //search in localdrive
            object.GenerateFromParseObject(parseObject);
            if (CallMethod!= null)
                CallMethod.AddObject(object);
            //search in cloud for refresh version

              /*
            this code used to search in cloud- in case object doesn't exist in local drive
             */
            ParseQuery<ParseObject>  query= ParseQuery.getQuery(object.getTableName());

            query.getInBackground(object.GetObjectId(), new GetCallback<ParseObject>(){
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null){
                        object.GenerateFromParseObject(parseObject);
                        if (CallMethod!= null)
                            CallMethod.AddObject(object);
                    }
                    else{
                        //found on localdrive but not on DB- probably not refreshed the db :(
                        e.printStackTrace();
                    }
                }
            });


        }else{
            /*
            this code used to search in cloud- in case object doesn't exist in local drive
             */
            ParseQuery<ParseObject>  query= ParseQuery.getQuery(object.getTableName());

            query.getInBackground(object.GetObjectId(), new GetCallback<ParseObject>(){
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null){
                        object.GenerateFromParseObject(parseObject);
                        if (CallMethod!= null)
                            CallMethod.AddObject(object);
                    }
                    else{
                        //not found nor on local storage or in cloud!
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
