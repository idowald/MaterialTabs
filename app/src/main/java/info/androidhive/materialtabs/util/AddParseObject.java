package info.androidhive.materialtabs.util;

/**
 * Created by ido on 05/12/2015.
 */
public interface AddParseObject{
   //this interface with any adapters/ UI objects.
    /**
     * any time addapter wants to get parseObjects he'll ask for an object who implements Parsable
     * when the instance done instantiate himself, he will call the function with his instance
     */

    void AddObject(AbstractParseObject object);
    /**
     * remember to check if instance not already exist with objectId
     */

}
