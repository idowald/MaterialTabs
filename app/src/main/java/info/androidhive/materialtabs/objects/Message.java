package info.androidhive.materialtabs.objects;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import info.androidhive.materialtabs.DB.DbHelper;
import info.androidhive.materialtabs.DB.MessagesDB;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.AddParseObjects;
import info.androidhive.materialtabs.util.FindCallbackObjects;
import info.androidhive.materialtabs.util.GenerateFromExternalKey;
import info.androidhive.materialtabs.util.GenerateFromObjectId;
import info.androidhive.materialtabs.util.ParseArrayListListener;
import info.androidhive.materialtabs.util.SaveInBackGround;
import info.androidhive.materialtabs.util.fetchIfNeededInBackgroundRelational;


/**
 * Created by ido on 21/11/2015.
 */
public class Message extends AbstractParseObject implements AddParseObjects{
    /**
     * check whether i need toParseObject since i made a changes in FindcallbackObjects
     */

    /*
    ! notice: all protected fields aren't part of the Parse- it's a duplicate for faster navigation with pubnub
    ! notice: Date is tricky- it's only created when synced to Parse (Local or cloud doesn't matter)
    ! notice- the messaging Algorithm is written well in MessagingService.class

    message is being sent from a user to another.
    it's send by Pubnub server since it's super fast.
    then it's saved in the Parse cloud
    when it reached to the other side-
    it's saved in the cloud of parse for analyzing history data.
        -take the object_id in the parse cloud and attach it to message.
        -save the message in local storage containing the object_id! :D
    to make it faster to get messages from the cloud the next algorithem is used:
    populate old messages from local storage only.
    -if message/conversation doesn't found. search it in the cloud.
    -when sending a message through PUBNUB send it by
    -if user tries to see history messages. it will send a request from the cloud:

           1.retrieve all messages that the date is older than Requested Date + belong to conversation.
           2.sync all messages in local storage with the old messages. use object id as a key :)
     */

    private String messageObjectId=null;
    private String external_key="";

    protected ParseObject userParseObject= null;
    public String fromUserName= null;//used for UI like ConversationMessagesAdapter class
    private User from= null;

    protected int size= -1; //size of readers in db
    protected  ArrayList<ParseArrayListListener<User>> callbacks_to= new ArrayList<ParseArrayListListener<User>>(); //helps to get "TO" with mvc model
    protected ArrayList<AddParseObject> callbacks_from = new ArrayList<>();
    protected ArrayList<AddParseObject> callbacks_conversation = new ArrayList<>();

    private ArrayList<User> to= new ArrayList<User>();//many to many
    private String text="";
    private boolean isUrgent= false;
    private boolean isNew = true;
    private Date date= new Date();
    protected Conversation.Conversation_type conversationType= null;
    protected String conversationName= null;
    protected String conversationObjectId= null;
    protected ParseObject conversationParseObject =null;
    private Conversation conversation=null;


    public static final  SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public Message(String messageObjectId, AddParseObject callback){
        this.messageObjectId =messageObjectId;
        new GenerateFromObjectId(this,callback);
    }

/*    public Message(ParseObject object, AddParseObject<Message> callback){
        new fetchIfNeededInBackgroundRelational(this, object, callback);

    }*/
    public Message(ParseObject object){
        this.GenerateFromParseObject(object);
    }
    public Message(ParseObject parseObject, AddParseObject callback){

        new fetchIfNeededInBackgroundRelational(this, parseObject, callback);

    }

    public Message(JSONObject message){

        /* to: username|username|username */
        try {
            external_key = message.getString("external_key");
            conversationObjectId = message.getString("conversationObjectId");
            conversationType= Conversation.Conversation_type.getConversation(message.getString("conversationType"));
            conversationName= message.getString("conversationName");
            //conversation= new Conversation(message.getString("conversationObjectId"));
            from = new User(message);
            fromUserName= message.getString("fromUserName");
            //from= message.getString("from");
            String arrayTo=  message.getString("to"); //getting list of userNames with | between each one
            AddParseObject callback = null;
            for (String userName : new ArrayList<String>(Arrays.asList(arrayTo.split("|")))){
                to.add(new User("","",userName, callback)); //again it's not important the userNames to be get fast
            }

            text= message.getString("text");
            isUrgent= (message.getString("isUrgent").matches("true"))? true: false ;
            setDate(message.getString("date"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AddParseObject callback = new AddParseObject() { //after the message have object id you can save it locally
            @Override
            public void AddObject(AbstractParseObject msg) {
                msg.CreateAndSaveNewParseObject();
            }
        };
        new GenerateFromExternalKey(this, callback);// this gets the Objectid within the Cloud


    }

    public Message(User from, ArrayList<User> to, String text, boolean isUrgent, Conversation conversation) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.isUrgent = isUrgent;
        this.conversation = conversation;
    }

    public Message( String fromUserName,String fromFirstName,String fromLastName, ArrayList<String> toUserNames, String text, boolean isUrgent,boolean isNew ,Conversation conversation) {
        this.isNew = isNew;
        AddParseObject callback= null;
        this.from = new User(fromFirstName, fromLastName,fromUserName,callback ); //user will generate himself with username

        for (String userName: toUserNames){
            this.to.add(new User("","", userName,callback)); //because it's not important to be fast.
        }

        this.text = text;
        this.isUrgent = isUrgent;
        this.conversation = conversation;
    }



    public void setDate(Date date) {
        this.date = date;
    }
    public void setDate(String date) {
        /** date format is 31/07/2015 13:48 */
        try {
            this.date= DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getExternal_key() {
        return external_key;
    }

    public void setExternal_key(String external_key) {
        this.external_key = external_key;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public boolean isNew(){
        return isNew();
    }

    public void messageRead(){ //mark message as read
        isNew=false;
    }
    public String getDate() {
        return DATE_FORMAT.format(date);
    }
    public Date getDateObject() {
        return date;
    }

    @Override
    public String GetObjectId() {
        return this.messageObjectId;
    }



    @Override
    public void GenerateFromParseObject(ParseObject object) {

        external_key = object.getString("external_key");
        messageObjectId= object.getObjectId();

        AddParseObject getFrom = new AddParseObject() {
            @Override
            public void AddObject(AbstractParseObject object) {
                from =  (User) object;
                informWaiters();
            }
        };
        AddParseObject getConv= new AddParseObject() {
            @Override
            public void AddObject(AbstractParseObject object) {
                conversation= (Conversation ) object;
                informWaiters();
            }
        };
        userParseObject = object.getParseObject("from");


        new User(object.getParseObject("from"),getFrom);


        date= object.getCreatedAt();
        isUrgent= object.getBoolean("isUrgent");
        text = object.getString("text");

        new Conversation(object.getParseObject("conversation"),getConv);
        conversationParseObject = object.getParseObject("conversation");


        //retrieving "to"
        ParseQuery<ParseObject> queryTo = object.getRelation("to").getQuery();
        //next method- when it finished getting the relational data it notify anyone who's interested

        queryTo.findInBackground(new FindCallbackObjects(this ));

    }



    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Message{" +
                ", from='" + from + '\'' +
                ", to=" + to +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }

    public JSONObject toJSONObject(String To){


        //when sending a json through PUBNUB only send the conversation's id ONLY!
        JSONObject json= new JSONObject();
        try {
            this.external_key= GenerateExternalKey();
            json.put("external_key", external_key);
            //json.put("messageObjectId", messageObjectId); because messageId isn't exist when sending
            json.put("conversationType",conversation.getConversation_type().toString());
            json.put("conversationName", conversation.getConversationName());
            json.put("conversationObjectId",conversation.getConversationObjectId());
            json.put("fromUserName", from.getUserName());
            json.put("fromFirstName", from.getFirstName());
            json.put("fromLastName", from.getLastName());
            json.put("fromObjectId", from.GetObjectId());
            json.put("text", text);

            json.put("isUrgent", (isUrgent()? "true":"false"));
            json.put("date",getDate()); // ONLY TO PUBNUB!
            json.put("to",To);
            // new inflateJSON(json,callback); //this is to inflate "To" list from cloud

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;

    }


    public String getObjectId() {
        return messageObjectId;
    }

    @Override
    public void SetObjectId(String objectId) {
        /*
        because object id can be saved in later work.
         */
        messageObjectId = objectId;
        /*
        save the object within the DB
         */


    }

    @Override
    public void CreateAndSaveNewParseObject() {
            /*
            this method converts the message into a ParseMessage that saved in the cloud
            afterwards get the object id and attach it to the message.
            sync the message in the local storage
            -it implements with Parsable and used by ?
             */

        new SaveInBackGround(this);

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

        object.put("external_key",external_key);
        ArrayList<ParseObject> users = new ArrayList<ParseObject>();
        for (User  user : to){
            users.add(ParseObject.createWithoutData(user.getTableName(),user.GetObjectId()));
        }

        object.put("to",users);

        object.put("isUrgent",isUrgent);
        if (from == null)
            object.put("from", userParseObject);
        else
        object.put("from", ParseObject.createWithoutData(from.getTableName(),from.GetObjectId()));
        object.put("text", text);
        if (conversation == null)
            object.put("conversation", conversationParseObject);
        else {
            ParseObject conv = ParseObject.createWithoutData(conversation.getTableName(), conversation.GetObjectId());

            object.put("conversation", conv);
        }
        //todo i add this fix down - to make it work.. not sure if correct way to fix
        object.saveInBackground();
        return object;
    }

    public boolean isIncoming(String username){
        return !fromUserName.matches(username);

    }

    public void getTo(ParseArrayListListener<User> callback) {
        if (size==to.size())
            callback.AddList(to);
        else{
            this.callbacks_to.add(callback);
        }
    }



    public void getFrom(AddParseObject callback) {
        if (from != null)
            callback.AddObject(from);
        else{
            callbacks_from.add(callback);
        }
    }

    @Override
    public void NumberOfElements(int number) {
        size = number;
    }


    @Override
    public void AddObject(ParseObject object) {
        to.add(new User(object));
        if (size == to.size()) {
           informWaiters();
        }
    }
    public Conversation.Conversation_type getConversationType() {
        /*
        this methods can be called only by Service since it's relay on JSONObject!!
         */
        //  if (conversationType!= null)
        return conversationType;
        //  else
        //     return getConversation().getConversation_type();
    }

    public String getConversationObjectId() {
        // if (conversationObjectId != null)
        if (conversation!= null)
            return conversation.getConversationObjectId();
        return conversationObjectId;
        // else
        //   return  getConversation().getConversationObjectId();
    }

    public String getConversationName() {
        // if (conversationType!= null)
        return conversationName;
        // else{

        //    return  getConversation().getConversationName();
        //  }
    }

    public void getConversation(AddParseObject callback) {
        /*
        slow method
         */
        if (conversation == null)
            conversation = new Conversation(conversationObjectId,callback);
        else
            callback.AddObject(conversation);

    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String GenerateExternalKey(){
        return date.toString()+""+ (int)(Math.random()*1000);
    }

    @Override
    public String getTableName() {
        return this.getClass().getSimpleName()+"s";
    }

    @Override
    public void informWaiters() {
        if (to != null)
        {
            for (ParseArrayListListener<User> parsable : callbacks_to){
                parsable.AddList(to);
            }
            callbacks_to.clear();
        }
        if (from!= null){
            for (AddParseObject callback: callbacks_from){
                callback.AddObject(from);
            }
            callbacks_from.clear();

        }
       if (conversation != null){
           for (AddParseObject callback : callbacks_conversation){
               callback.AddObject(conversation);
           }
           callbacks_conversation.clear();
       }
    }
}

