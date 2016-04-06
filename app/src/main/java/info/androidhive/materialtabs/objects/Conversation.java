package info.androidhive.materialtabs.objects;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;

import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.AddParseObjects;
import info.androidhive.materialtabs.util.FindCallbackObjects;
import info.androidhive.materialtabs.util.GenerateFromConversationName;
import info.androidhive.materialtabs.util.GenerateFromObjectId;
import info.androidhive.materialtabs.util.ParseArrayListListener;
import info.androidhive.materialtabs.util.SaveInBackGround;
import info.androidhive.materialtabs.util.SaveInBackgroundByKey;
import info.androidhive.materialtabs.util.fetchIfNeededInBackgroundRelational;


/**
 * Created by ido on 01/12/2015.
 */
public class Conversation extends AbstractParseObject implements AddParseObjects{
/*

Algorithm to create conversation:
Preset: need another table in DB of relation between USERS To conversations. or use many to many relation
as mentioned in parse.com
when want to open an old conversation:
1. lookup in relation table all entires where my userObjectId appears-> keep all Conversations that found as options to open in ListView
2. user clicked an item in the Conversation:
3. lookup in relation Table again and get all
 */
    private String ConversationObjectId=null;
    private String conversationName= null;
    private  Conversation_type conversation_type=null;


   // protected ArrayList<ParseObject> readersParseObject = new ArrayList<ParseObject>();
    private   ArrayList<User> readers= new ArrayList<User>();

    //private Case my_case=null; //"case" in the cloud


     protected int size= -1;
     protected   ArrayList<ParseArrayListListener<User>> callbacks= new ArrayList<ParseArrayListListener<User>>();




    public Conversation(String ConversationObjectId ,AddParseObject callback){
        /*
        this is used by Message class when a message received and we want to find the correct Conv
         */
        this.ConversationObjectId = ConversationObjectId;
        new GenerateFromObjectId(this, callback);

    }
    public Conversation(Conversation_type conversation_type, String conversationName, AddParseObject callback){
        /**
         * creates conversation by key ConversationName
         */
        this( conversation_type,  conversationName);
        new SaveInBackgroundByKey(this,"conversationName",callback);

    }

    public Conversation(Conversation_type conversation_type, String conversationName) {
        /**
         * create new instance of conversation
         */
        this.conversation_type = conversation_type;
        this.conversationName = conversationName;

    }

    public Conversation(String conversationName, Conversation_type conversation_type,
                        String ConversationObjectId,AddParseObject callback ){
        //this constructor helps to get data fast without cloud and in background sync the fields
        this(ConversationObjectId, callback);
        this.conversationName = conversationName;
        this.conversation_type = conversation_type;

    }


    public Conversation(ParseObject object, AddParseObject callback){
        new fetchIfNeededInBackgroundRelational(this, object, callback);
    }
    public Conversation(ParseObject object){
        this.GenerateFromParseObject(object);
    }

    //public boolean IsCase(){
     //   return conversation_type == Conversation_type.CASE;
    //}
    public boolean IsPrivate(){ return conversation_type == Conversation_type.PRIVATE;}
    public boolean IsGROUP(){ return conversation_type == Conversation_type.GROUP;}
    public boolean IsCASE(){ return conversation_type == Conversation_type.CASE;}
    public String getConversationObjectId() {
        return ConversationObjectId;
    }

    public void SetObjectId(String conversationObjectId) {
        setConversationObjectId(conversationObjectId);
    }
    public String getConversationName() {
        return conversationName;
    }

    public Conversation_type getConversation_type() {
        return conversation_type;
    }


    @Override
    public void NumberOfElements(int number) {
        size = number;
    }

    @Override
    public void AddObject(ParseObject object) {
        this.addReader(new User(object));
    }

    @Override
    public String GetObjectId() {
        return getConversationObjectId();
    }

    @Override
    public void CreateAndSaveNewParseObject() {
        new SaveInBackgroundByKey(this,"conversationName", null);
    }

    public void CreateAndSaveNewParseObject(AddParseObject callback) {
        new SaveInBackgroundByKey(this,"conversationName",callback);
    }

    @Override
    public ParseObject ToParseObject(){
        //TODO all classes with the same technique!! check if done

        ParseObject object = null;
        if (this.GetObjectId() != null) //old object- used to update fields!
        {
            object = ParseObject.createWithoutData(getTableName(),GetObjectId());
        }
        else { //new instance
            object = new ParseObject(this.getTableName());
        }
        object.put("conversationName", conversationName);
        object.put("conversation_type", conversation_type.toString());

        ParseRelation<ParseObject> users = object.getRelation("Users");
            for (User user: readers)
                users.add(user.ToParseObject());


        return object;
    }
    @Override
    public String getTableName() {
        return this.getClass().getSimpleName()+"s";
    }
    @Override
    public void GenerateFromParseObject(ParseObject object) {

        ConversationObjectId = object.getObjectId();
        this.conversationName= object.getString("conversationName");
        this.conversation_type= conversation_type.getConversation(object.getString("conversation_type"));



        //next method- when it finished getting the relational data it notify anyone who's interested


        ParseQuery<ParseObject> queryTo = object.getRelation("Users").getQuery();
        queryTo.findInBackground(new FindCallbackObjects(this ));



    }

    public void setConversationObjectId(String conversationObjectId) {
        ConversationObjectId = conversationObjectId;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public void setConversation_type(Conversation_type conversation_type) {
        this.conversation_type = conversation_type;
    }

    public void getReaders(ParseArrayListListener<User> callback) {
        if (callback== null)
            return;

        if (size==readers.size())
            callback.AddList(readers);
        else
            this.callbacks.add(callback);

    }
    public void addReader(User user){
        this.readers.add(user);
        if (readers.size() == size){
            informWaiters();
        }
    }
    public void setReaders(ArrayList<User> readers) {
        this.readers = readers;
    }

    @Override
    public void informWaiters() {
        if (readers != null)
        {
            for (ParseArrayListListener<User> parsable : callbacks){
                parsable.AddList(readers);
            }
            callbacks.clear();
        }

    }

    public enum Conversation_type {


        PRIVATE, CASE, GROUP;
        static   public Conversation_type getConversation(String conversation_type){
            conversation_type= conversation_type.toUpperCase();
            if (conversation_type.compareTo("PRIVATE")==0)
                return PRIVATE;

            if (conversation_type.compareTo("CASE")==0)
                return CASE;

            return GROUP;
        }
        static   public Conversation_type getConversation(int conversation_type){
            switch(conversation_type) {
                case (0):
                    return PRIVATE;
                case (1):
                    return CASE;
            }
            return GROUP;
        }
        public int getInt(Conversation_type type){
            if (type== PRIVATE)
                return 0;
            if (type== CASE)
                return 1;
            return 2;

        }

        public String getString(){
            if (this== PRIVATE)
                return "PRIVATE";
            if (this== CASE)
                return "CASE";
            return "GROUP";
        }

    }


}
