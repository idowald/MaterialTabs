package info.androidhive.materialtabs.util;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import info.androidhive.materialtabs.objects.Conversation;


/**
 * Created by ido on 02/12/2015.
 */
public class GenerateFromConversationName implements GetCallback<ParseObject>{
    /**
     * this class helps to find users by username
     */
    private Conversation conversation;
    private AddParseObject<Conversation> CallMethod= null;

    public GenerateFromConversationName(Conversation object) {
        this(object,null);
    }

    public GenerateFromConversationName(Conversation conversation, AddParseObject<Conversation> callMethod) {
        this.conversation = conversation;
        CallMethod = callMethod;
        ParseQuery<ParseObject> query  = ParseQuery.getQuery(conversation.getTableName()); //for class Remark it will go to Remarks
        query.whereEqualTo("conversationName", conversation.getConversationName());
        //query.fromLocalDatastore();

        query.getFirstInBackground(this);
    }



    @Override
    public void done(ParseObject parseObject, ParseException e) {
        if (e == null)
        {
            conversation.GenerateFromParseObject(parseObject);
            if (CallMethod!= null)
                CallMethod.AddObject(conversation);

            ParseQuery<ParseObject> query  = ParseQuery.getQuery(conversation.getTableName()); //for class Remark it will go to Remarks
            query.whereEqualTo("conversationName", conversation.getConversationName());

            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if ( e == null)
                    {
                        conversation.GenerateFromParseObject(parseObject);
                        if (CallMethod!= null)
                            CallMethod.AddObject(conversation);

                    } else {
                        //not found in cloud- can't be. a user that found in local but not in db
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            //not found Username in local Drive. search in cloud:
            ParseQuery<ParseObject> query  = ParseQuery.getQuery(conversation.getTableName()); //for class Remark it will go to Remarks
            query.whereEqualTo("conversationName", conversation.getConversationName());

            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if ( e == null)
                    {
                        conversation.GenerateFromParseObject(parseObject);
                        if (CallMethod!= null)
                            CallMethod.AddObject(conversation);

                    } else {
                        //not exist nor on local or db!
                        e.printStackTrace();
                    }
                }
            });

        }
    }
}
