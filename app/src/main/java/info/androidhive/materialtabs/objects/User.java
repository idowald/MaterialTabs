package info.androidhive.materialtabs.objects;



import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.GenerateFromObjectId;
import info.androidhive.materialtabs.util.GenerateFromUserName;
import info.androidhive.materialtabs.util.SaveInBackGround;
import info.androidhive.materialtabs.util.SaveInBackgroundByKey;
import info.androidhive.materialtabs.util.fetchIfNeededInBackgroundRelational;


/**
 * Created by ido on 02/12/2015.
 */
public class User extends AbstractParseObject{

    private String userObjectId=null;
    private String firstName=null;
    private String lastName= null;
    private String userName=null;



    public User(String userObjectId, AddParseObject<User> callback){
        this.userObjectId = userObjectId;
        new GenerateFromObjectId<User>(this,callback);
    }
    public User(String firstName, String lastName, String userName, String userObjectId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.userObjectId = userObjectId;
    }
    public User(String firstName, String lastName, String userName, AddParseObject callback) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;

        new GenerateFromUserName(this,callback);
    }
    public User(JSONObject user) throws  JSONException{
        /**
         * this is used by Messaging service to get a fast UI with the instance!
         */
            this(user.getString("fromFirstName"),user.getString("fromLastName"),user.getString("fromUserName"), user.getString("fromObjectId"));

    }
    public User(ParseObject parseObject, AddParseObject<User> callback){

            new fetchIfNeededInBackgroundRelational(this, parseObject,callback);

    }
public User(ParseObject parseObject){
    this.GenerateFromParseObject(parseObject);
}
    @Override
    public ParseObject ToParseObject() {
        //TODO here lies problem- if user is defined by key UserName, it might create a new instance of user
        ParseObject object = null;
        if (this.GetObjectId() != null)
        {
            object = ParseObject.createWithoutData(getTableName(),GetObjectId());
        }
        else { //new instance

                object = new ParseObject(this.getTableName());
        }
        object.put("firstName", firstName);
        object.put("lastName",lastName);
        object.put("userName",userName);


        return object;

    }

    @Override
    public void CreateAndSaveNewParseObject() {

            new SaveInBackgroundByKey<User>(this,"userName");
    }

    @Override
    public void SetObjectId(String objectId) {
            userObjectId= objectId;
    }

    @Override
    public void GenerateFromParseObject(ParseObject parseObject) {
        this.userObjectId = parseObject.getObjectId();
        this.lastName= parseObject.getString("lastName");
        this.firstName= parseObject.getString("firstName");
        this.userName= parseObject.getString("userName");


    }


    @Override
    public String GetObjectId() {
        return userObjectId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    @Override
    public String getTableName() {
        return this.getClass().getSimpleName()+"s";
    }
}
