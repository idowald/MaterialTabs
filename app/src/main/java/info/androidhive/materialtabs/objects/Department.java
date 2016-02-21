/*
package info.androidhive.materialtabs.objects;

import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.lang.reflect.Array;
import java.util.ArrayList;

import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.GenerateFromObjectId;
import info.androidhive.materialtabs.util.SaveInBackGround;
import info.androidhive.materialtabs.util.SaveInBackgroundByKey;
import info.androidhive.materialtabs.util.fetchIfNeededInBackgroundRelational;


*/
/**
 * Created by ido on 14/12/2015.
 *//*

public class Department  extends AbstractParseObject implements AddParseObject<Conversation> {
    */
/**
     * health department have many to many users.
     * it have name
     *//*

    private String DepartmentObjectId=null;
    private String departmentName= null;

    private Conversation conversation = null;
    private ArrayList<User> users= new ArrayList<User>();


    protected ArrayList<AddParseObject<Conversation>> callbacks= new ArrayList<>();


    public Department(String departmentObjectId, AddParseObject<Department> callback){
        this.DepartmentObjectId = departmentObjectId;
        new GenerateFromObjectId<Department>(this,callback);
    }

    public Department(ParseObject parseObject, AddParseObject<Department> callback){
        new fetchIfNeededInBackgroundRelational(this, parseObject,callback);
    }
    public  Department(ParseObject parseObject){ //fast instance
        this.GenerateFromParseObject(parseObject);
    }
    public Department(String departmentName, Conversation conversation) {
        */
/**
         * make sure conversation.type= group
         *//*

        this.departmentName = departmentName;
        this.conversation = conversation;
    }

    public Department(String departmentName) {
        this.departmentName = departmentName;
        GenarateConversation(departmentName);
    }

    public void GenarateConversation(String conversationName){

        //TODO look at the code bellow- need to implement same way to all class:
        // have all class with relation have AddparseObject implemented and make it MVC
        //todo remove conversation= new in all items
         new Conversation(Conversation.Conversation_type.GROUP, conversationName, this);

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
        object.put("departmentName", departmentName);
    // TODO here lies the problem: we create new conversation in ToParseObject
        //solution! don't ever save an item before conversation is up
        object.put("conversation",conversation.ToParseObject());

        ParseRelation<ParseObject> users = object.getRelation("Users");
        for (User user: this.users)
            users.add(user.ToParseObject());
        return object;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public void CreateAndSaveNewParseObject() {
        new SaveInBackgroundByKey<Department>(this,"departmentName");

    }

    @Override
    public void SetObjectId(String objectId) {
        DepartmentObjectId = objectId;
    }

    @Override
    public void GenerateFromParseObject(ParseObject parseObject) {
        this.conversation = new Conversation(parseObject.getParseObject("conversation"));
        this.DepartmentObjectId= parseObject.getObjectId();
        this.departmentName= parseObject.getString("departmentName");

    }

    @Override
    public String GetObjectId() {
        return DepartmentObjectId;
    }

    @Override
    public String getTableName() {
        return "Departments";
    }

    @Override
    public void AddObject(Conversation conversation) {
        this.conversation= conversation;
        for (AddParseObject<Conversation> callback: callbacks){
            callback.AddObject(conversation);
        }

        callbacks.clear();
    }
    public void GetConversation(AddParseObject<Conversation> callback){
        if (conversation!= null)
            callback.AddObject(conversation);
        else
            callbacks.add(callback);
    }

}

*/
