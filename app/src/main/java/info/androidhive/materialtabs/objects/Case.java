package info.androidhive.materialtabs.objects;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.AddParseObjects;
import info.androidhive.materialtabs.util.FindCallbackObjects;
import info.androidhive.materialtabs.util.GenerateFromObjectId;
import info.androidhive.materialtabs.util.ParseArrayListListener;
import info.androidhive.materialtabs.util.SaveInBackGround;
import info.androidhive.materialtabs.util.fetchIfNeededInBackgroundRelational;


/**
 * Created by ido on 01/12/2015.
 */
public class Case extends AbstractParseObject {
    /*
    each case reo=present a patient's conversation
     */

    private String caseObjectId =null;

    private String gender="";
    private Date age= null;
    private String first_name="";
    private String last_name= "";

    private String information="";

   // protected int sizeOfRemarks = -1;
    //protected  ArrayList<ParseArrayListListener<Remark>> callbacks_remarks= new ArrayList<ParseArrayListListener<Remark>>(); //helps to get "remarks" with mvc model
    //private ArrayList<Remark> remarks= new ArrayList<Remark>();// one to many


    protected ParseObject conversation_object= null;
    protected  ArrayList<AddParseObject> callbacks_conversation = new ArrayList<>();
    private Conversation conversation = null;

    public Case(String CaseObjectId,AddParseObject callback){

        this.caseObjectId = CaseObjectId;
        new GenerateFromObjectId(this,callback);

    }

    public Case(ParseObject object , AddParseObject callback){ //this is called when created by relation query
            new fetchIfNeededInBackgroundRelational(this, object, callback);
    }
    public Case(ParseObject object){ //this is called when creating case from a straight query.
        this.GenerateFromParseObject(object);
    }


   public Case(String Gender, int Age, String information  ) {

        gender= Gender;
       try {
           int year= new Date().getYear();
           String birthyear=""+( year-Age);

           this.age= Message.DATE_FORMAT.parse("01/01/"+birthyear+" 01:01:01");
       } catch (ParseException e) {
           e.printStackTrace();
       }


        this.information = information;
    }


    public ParseObject ToParseObject(){


        ParseObject object = null;
        if (this.GetObjectId() != null) //old objcet- used to update fields!
        {
            object = ParseObject.createWithoutData(getTableName(),GetObjectId());
        }
        else { //new instance
            object = new ParseObject(this.getTableName());
        }

        if (conversation==null)
            object.put("conversation", conversation_object);
        else
            object.put("conversation", ParseObject.createWithoutData(conversation.getTableName(), conversation.GetObjectId()));
        object.put("information",information );
        object.put("gender",gender);
        object.put("age",age);
        object.put("first_name",first_name);
        object.put("last_name",last_name);

       // ParseRelation<ParseObject> remarks = object.getRelation("Remarks");
        //for (Remark remark: this.remarks)
          //  remarks.add(remark.ToParseObject());


        return object;
    }

    @Override
    public void CreateAndSaveNewParseObject() {
      //  ParseObject object = ToParseObject();
        new SaveInBackGround(this);
      //  return object;

    }

    @Override
    public void SetObjectId(String objectId) {
        setCaseObjectId(objectId);

    }

/*    public void getRemarks( ParseArrayListListener<Remark> callback) {
        if (sizeOfRemarks==remarks.size())
            callback.AddList(remarks);
        else{
            this.callbacks_remarks.add(callback);
        }
        //return remarks;
    }*/



    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getCaseObjectId() {
        return caseObjectId;
    }

   /* @Override
    public void NumberOfElements(int number) {
        sizeOfRemarks = number;
        if (number == 0)
        {
            for (ParseArrayListListener listener : callbacks_remarks)
                listener.AddList(remarks);
            callbacks_remarks.clear();
        }

    }
    @Override
    public void AddObject(ParseObject object) {
        remarks.add(new Remark(object));
        if (sizeOfRemarks == remarks.size()) {
            for (ParseArrayListListener listener : callbacks_remarks)
                listener.AddList(remarks);
            callbacks_remarks.clear();
        }
    }*/

    @Override
    public void GenerateFromParseObject(ParseObject parseObject) {
        AddParseObject getConversation= new AddParseObject() {
            @Override
            public void AddObject(AbstractParseObject object) {
                conversation= (Conversation) object;
                informWaiters();
            }
        };

        caseObjectId = parseObject.getObjectId();
        information = parseObject.getString("information");
        gender = parseObject.getString("gender");
        age = parseObject.getDate("age");

        conversation_object= parseObject.getParseObject("conversation") ;
       new Conversation(parseObject.getParseObject("conversation"),getConversation ) ;
        first_name= parseObject.getString("first_name");
        last_name= parseObject.getString("last_name");


        //retrieving "remarks"
       // ParseQuery<ParseObject> queryremarks = parseObject.getRelation("Remarks").getQuery();
        //next method- when it finished getting the relational data it notify anyone who's interested

        //queryremarks.findInBackground(new FindCallbackObjects(this ));
    }

    @Override
    public String GetObjectId() {
        return getCaseObjectId();
    }

    public void setCaseObjectId(String caseObjectId) {
        this.caseObjectId = caseObjectId;
    }

    @Override
    public String getTableName() {
        return this.getClass().getSimpleName()+"s";
    }

    public void getConversation(AddParseObject callback) {
        if (conversation != null)
            callback.AddObject(conversation);
        else
            callbacks_conversation.add(callback);
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getAge() {
        return age;
    }

    public void setAge(int age) {
        try {
            int year= new Date().getYear();
            String birthyear=""+( year-age);

            this.age= Message.DATE_FORMAT.parse("01/01/"+birthyear+" 01:01:01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    @Override
    public void informWaiters() {
/*

        if (remarks != null)
        {
            for (ParseArrayListListener<Remark> parsable : callbacks_remarks){
                parsable.AddList(remarks);
            }
            callbacks_remarks.clear();
        }
*/

        if (conversation!= null)
            for (AddParseObject callback: callbacks_conversation){

            }
        callbacks_conversation.clear();
    }
}
