package info.androidhive.materialtabs.objects;

import com.parse.ParseObject;

import java.util.Date;

import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.GenerateFromObjectId;
import info.androidhive.materialtabs.util.SaveInBackGround;
import info.androidhive.materialtabs.util.fetchIfNeededInBackgroundRelational;


/**
 * Created by ido on 02/12/2015.
 */
public class Remark extends AbstractParseObject {
    /*
    //TODO see if Date class needs utility for remark and for message together
    Case's remarks. each case can have several remarks
    each remarks has a user who wrote it, a remark, is it urgent and date the time it was written
     */
private String remarkObjectId=null;
protected ParseObject userParseObject= null;
private User user=null;
private String text="";
private boolean isUrgent=false;
private Date date= new Date();

    public Remark(String remarkObjectId, AddParseObject<Remark> callback){
        this.remarkObjectId = remarkObjectId;
        new GenerateFromObjectId<Remark>(this,callback);

    }
    public Remark(ParseObject parseObject, AddParseObject<Remark> callback){
        new fetchIfNeededInBackgroundRelational(this, parseObject,callback);

    }
    public  Remark(ParseObject parseObject){ //fast instance
        this.GenerateFromParseObject(parseObject);
    }
    public Remark(User user, String text, boolean isUrgent, Date date) {
        this.user = user;
        this.text = text;
        this.isUrgent = isUrgent;
        this.date = date;
    }

    @Override
    public ParseObject ToParseObject() {
        ParseObject object = null;
        if (this.GetObjectId() != null) //old objcet- used to update fields!
        {
            object = ParseObject.createWithoutData(getTableName(),GetObjectId());
        }
        else { //new instance
            object = new ParseObject(this.getTableName());
        }

        if (userParseObject != null)
            object.put("user",userParseObject);
        else
            object.put("user", user.ToParseObject());
        object.put("isUrgent",isUrgent);
        object.put("text",text);
        return object;
    }

    @Override
    public void CreateAndSaveNewParseObject() {
      //  ParseObject object  = ToParseObject();
        new SaveInBackGround<Remark>(this);
        //object.saveInBackground(callback);
       // return object;


    }

    @Override
    public void SetObjectId(String objectId) {
        this.remarkObjectId= objectId;

    }

    @Override
    public void GenerateFromParseObject(ParseObject parseObject) {
        remarkObjectId= parseObject.getObjectId();
        userParseObject = parseObject.getParseObject("user");
        isUrgent =  parseObject.getBoolean("isUrgent");
        text= parseObject.getString("text");
        date =parseObject.getCreatedAt();
    }

    @Override
    public String GetObjectId() {
        return remarkObjectId;

    }

    public void getUser(AddParseObject<User> callback) {
        if (user == null)
            user = new User(userParseObject , callback);
        else
            callback.AddObject(user);
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String getTableName() {
        return this.getClass().getSimpleName()+"s";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public void setUrgent(boolean urgent) {
        isUrgent = urgent;
    }
}
