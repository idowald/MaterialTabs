package info.androidhive.materialtabs.util;

import com.parse.ParseObject;

/**
 * Created by ido on 14/12/2015.
 */
public interface AddParseObjects {
    /**
     * and MVC model
     * this is for adding arraylist of parseObjects.
     * it is used for relational data of Many to Many
     * how to implement:
     * in each class which contains many to many relation on the AddParseObject number n
     * when n == size of the entire relation
     * trigger all the methods "AddParseObjects" array that have been collected in class.
     *
     */
    void NumberOfElements(int number);
    void AddObject(ParseObject object);
}
