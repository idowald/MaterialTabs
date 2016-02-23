package info.androidhive.materialtabs.objects;

import com.parse.ParseObject;

import java.util.ArrayList;

import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.GenerateFromObjectId;
import info.androidhive.materialtabs.util.SaveInBackGround;
import info.androidhive.materialtabs.util.fetchIfNeededInBackgroundRelational;


/**
 * Created by ido on 02/12/2015.
 */
public class Duty extends AbstractParseObject{
    /*
    each user in the system can have many duties
    each duty represents a person and it's duty by text.
    -in the first version we will not see context of duty between "Mast" or just "Head of"
     */
    String dutyObjectId= null;

    String dutyName="";
    String inDuty="false";

    protected ArrayList<AddParseObject> callbacks_conversation = new ArrayList<>();

    protected ParseObject conversationObject =null;
    private Conversation conversation= null;
    protected boolean IsnullConversation = false;

    protected ArrayList<AddParseObject> callbacks_user = new ArrayList<>();
    private User user=null;
    protected ParseObject userObject = null;



    public Duty(String dutyObjectId, AddParseObject callback){
        this. dutyObjectId = dutyObjectId;
        new GenerateFromObjectId(this, callback);
    }
    public Duty(ParseObject parseObject , AddParseObject callback){
        new fetchIfNeededInBackgroundRelational(this, parseObject, callback);
    }

    public Duty(ParseObject parseObject ){
        this.GenerateFromParseObject(parseObject);
    }

    public Duty(String dutyName,Boolean inDuty, User user, Conversation conversation) {
        /**
         * can send conversation = null if needed
         */
        this.conversation=conversation;
        this.dutyName = dutyName;
        this.user = user;
        this.inDuty= inDuty? "true": "false";
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

        object.put("dutyName", dutyName);
       if (userObject == null) {
            object.put("user", ParseObject.createWithoutData(user.getTableName(),user.GetObjectId()));
        }
        else
            object.put("user",userObject);

        if (conversationObject != null)
            object.put("conversation",conversationObject);
         else if (conversation !=null )
            object.put("conversation",ParseObject.createWithoutData(conversation.getTableName(),conversation.GetObjectId()));
        object.put("inDuty",inDuty);

        return object;
    }

    @Override
    public void CreateAndSaveNewParseObject() {
        new SaveInBackGround<Duty>(this);

    }

    @Override
    public void SetObjectId(String objectId) {
        dutyObjectId = objectId;

    }

    @Override
    public void GenerateFromParseObject(ParseObject parseObject) {
        ParseObject conv_object= null;
        try {
            conv_object = parseObject.getParseObject("conversation");
        } catch (Exception e){
            //no needed object
            IsnullConversation = true;
        }
        if (!IsnullConversation)
        { AddParseObject getConv= new AddParseObject() {
            @Override
            public void AddObject(AbstractParseObject object) {
                conversation= (Conversation) object;
                informWaiters();
            }
        };
            new Conversation(conv_object,getConv);


        }

        AddParseObject getUser= new AddParseObject() {
            @Override
            public void AddObject(AbstractParseObject object) {
                user= (User) object;
            }
        };


        this.dutyName = parseObject.getString("dutyName");
        new User( parseObject.getParseObject("user"),getUser ) ;
        this.inDuty= parseObject.getString("inDuty");

        userObject=  parseObject.getParseObject("user");
        conversationObject = conv_object;

    }

    @Override
    public String GetObjectId() {
        return dutyObjectId;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public void getUser(AddParseObject callback) {
        if (user == null)
            callbacks_user.add(callback);
        else
            callback.AddObject(user);

    }

    public void setUser(User user) {
        this.user = user;
    }


    public void getConversation(AddParseObject callback) {
        if (conversation == null) {
            callbacks_conversation.add(callback);
        }
        else
            callback.AddObject(conversation);

    }

    public void setConversation(Conversation conv) {
        this.conversation = conv;
    }

    public String getInDuty() {
        return inDuty;
    }

    public void setInDuty(Boolean inDuty) {
        this.inDuty = inDuty? "true": "false";
    }

    @Override
    public String getTableName() {
        return this.getClass().getSimpleName()+"s";
    }

    @Override
    public void informWaiters() {

        if (conversation!= null) {
            for (AddParseObject callback : callbacks_conversation) {
                callback.AddObject(conversation);
            }
            callbacks_conversation.clear();
        }

        if (user!= null) {
            for (AddParseObject callback : callbacks_user) {
                callback.AddObject(user);
            }
            callbacks_user.clear();
        }

    }
}
