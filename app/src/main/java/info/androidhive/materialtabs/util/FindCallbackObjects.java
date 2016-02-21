package info.androidhive.materialtabs.util;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by ido on 01/12/2015.
 */
public class FindCallbackObjects implements FindCallback<ParseObject>{
    /*
          -it gets AddParseObject- each time it adds the element to the structure who called this class
          -the structure needs to tell whenever it finished his loading from the number of elements
    this generates the object from the local storage,
    if failed to find the instance in local storage- search it in the cloud!
     */
    private AddParseObjects callback= null;

    public FindCallbackObjects(AddParseObjects callback) {
        this.callback = callback;
    }

    @Override
    public void done(List<ParseObject> parseObjects, ParseException e) {
        if (e == null)
        {
            callback.NumberOfElements(parseObjects.size()); //todo need to make sure if size ==0 stills works
            for (ParseObject object : parseObjects)
                callback.AddObject(object);

        }else{

         e.printStackTrace();
        }
    }
}
