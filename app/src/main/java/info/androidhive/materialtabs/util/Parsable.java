package info.androidhive.materialtabs.util;

import com.parse.ParseObject;
/**
 * All parseObjects have 2 constructors at least:
 * one that creates the object from Relational. - use callback method "AddParseObject" because it takes time for the object to instanciate himself
 * the other creates the object from queries. if it's from queries- the object is being created fast
 */

/**
 * Created by ido on 01/12/2015.
 */
public interface Parsable {
    /*
    this interface makes sure that any element that wants to use parse.com have the correct methods
     */
    public ParseObject ToParseObject();
    /*
        use it to convert the object to ParseObject- without objectID!
        make sure if the objectId isn't initialized don't add it to the object!
     */
    public void CreateAndSaveNewParseObject();
    /*
    this takes the instance and save it in the cloud and in the storage.
    make sure that afterwards  that you take the objectId and put it in the instance!
     */

    public void SetObjectId(String objectId);
    /*
    this makes sure that after saving to the cloud you can set the objectId
     */

    public void GenerateFromParseObject(ParseObject parseObject);
    /*
    this to generate the instance after you query successfully the cloud or storage

     */

    public String GetObjectId();
    /*
    this helps to get the object Id
     */

    public String getTableName();






}
