package info.androidhive.materialtabs.util;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Created by ido on 02/12/2015.
 */
public class fetchIfNeededInBackgroundRelational  implements GetCallback<ParseObject> {
    /**
     * -you get relational objects from other objects.
     * -sometimes you create objects fastly without relational, it will mostly happen in "GetCallBackObject" i've create
     * this class used for relational parseobject. it's activated everytime an object being called by relation
     * -when object created by relation, it's not really exist.
     * so if it's not exist, you can call fetchIfNeededInBackground to get it
     */
    private AbstractParseObject object = null;
    private AddParseObject CallMethod= null;

    public fetchIfNeededInBackgroundRelational(AbstractParseObject object, ParseObject parseObject) {
        this(object,parseObject,null);
    }

    public fetchIfNeededInBackgroundRelational(AbstractParseObject object, ParseObject parseObject, AddParseObject callMethod) {
        this.object = object;
        CallMethod = callMethod;
        parseObject.fetchIfNeededInBackground(this);

    }

    @Override
    public void done(ParseObject parseObject, ParseException e) {
        if (e == null) {
            object.GenerateFromParseObject(parseObject);
            object.SetObjectId(parseObject.getObjectId());
            if (CallMethod!= null)
                CallMethod.AddObject(object);
            object.informWaiters();
        } else {
            e.printStackTrace();
        }
    }
}
