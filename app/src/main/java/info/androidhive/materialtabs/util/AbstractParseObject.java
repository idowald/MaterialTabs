package info.androidhive.materialtabs.util;

import java.io.Serializable;

/**
 * Created by ido on 07/12/2015.
 */
 public abstract class AbstractParseObject implements Parsable, Comparable<AbstractParseObject>, Serializable {
/*
this abstarct class define that each item is compared by object id
 */

    @Override
    public int compareTo(AbstractParseObject abstractParseObject) {
        if (this!= null && abstractParseObject!= null){
            if (this.GetObjectId()==null)
                return -1;
            if (abstractParseObject.GetObjectId() ==null)
                return 1;
            return this.GetObjectId().compareTo(abstractParseObject.GetObjectId());
        }

        else {
            if(this.GetObjectId()==null)
                return -1;
            if (abstractParseObject.GetObjectId() == null)
                return 1;
        }
        return 0;
    }
    abstract informWaiters
}
