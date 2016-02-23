package info.androidhive.materialtabs.objects;

import com.parse.ParseObject;

import java.util.ArrayList;
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
private User user=null; //creator
    protected ArrayList<AddParseObject> callbacks_user = new ArrayList<>();
private String text="";
private boolean isUrgent=false;
    protected ParseObject caseParseObject= null;
    private Case remark_case= null;
    protected ArrayList<AddParseObject> listeners_case= new ArrayList<>();

private Date date= new Date();

    public Remark(String remarkObjectId, AddParseObject callback){
        this.remarkObjectId = remarkObjectId;
        new GenerateFromObjectId(this,callback);

    }
    public Remark(ParseObject parseObject, AddParseObject callback){
        new fetchIfNeededInBackgroundRelational(this, parseObject,callback);

    }
    public  Remark(ParseObject parseObject){ //fast instance
        this.GenerateFromParseObject(parseObject);
    }
    public Remark(User user,Case remark_case, String text, boolean isUrgent, Date date) {
        this.user = user;
        this.text = text;
        this.isUrgent = isUrgent;
        this.date = date;
        this.remark_case=remark_case;
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

        if (caseParseObject != null)
            object.put("case",caseParseObject);
        else
            object.put("case", remark_case.ToParseObject());

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

        new User(userParseObject, new AddParseObject() {
            @Override
            public void AddObject(AbstractParseObject object) {
                user= (User) object;
                informWaiters();
            }
        });

        caseParseObject= parseObject.getParseObject("case");
        new Case(caseParseObject, new AddParseObject() {
            @Override
            public void AddObject(AbstractParseObject object) {
                remark_case= (Case) object;
                informWaiters();
            }
        });
        isUrgent =  parseObject.getBoolean("isUrgent");
        text= parseObject.getString("text");
        date =parseObject.getCreatedAt();
    }

    @Override
    public String GetObjectId() {
        return remarkObjectId;

    }

    public void getUser(AddParseObject callback) {
        if (user == null)
            callbacks_user.add(callback);
        else
            callback.AddObject(user);
    }
    public void getCase(AddParseObject callback){
        if (remark_case == null)
            listeners_case.add(callback);
        else
            callback.AddObject(remark_case);
    }
    public void setCase(Case remark_case){
        this.remark_case= remark_case;
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

    @Override
    public void informWaiters() {

        if (user!= null) {
            for (AddParseObject callback : callbacks_user ) {
                callback.AddObject(user);
            }
            callbacks_user.clear();
        }

        if (remark_case != null){
            for (AddParseObject callback : listeners_case){
                callback.AddObject(remark_case);
            }
            listeners_case.clear();
        }

    }
}
