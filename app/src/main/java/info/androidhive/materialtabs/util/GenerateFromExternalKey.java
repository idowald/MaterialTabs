package info.androidhive.materialtabs.util;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import info.androidhive.materialtabs.DB.DbHelper;
import info.androidhive.materialtabs.DB.MessagesDB;
import info.androidhive.materialtabs.objects.Message;


/**
 * Created by ido on 02/12/2015.
 */
public class GenerateFromExternalKey  implements GetCallback<ParseObject>{
    /**
     * this class helps to get messages with external key instead of object id
     */
    private Message message = null;
    private AddParseObject CallMethod= null;

    public GenerateFromExternalKey(Message message, AddParseObject callMethod) {
        this.message = message;
        CallMethod = callMethod;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(message.getTableName()); //for class Remark it will go to Remarks

        query.whereEqualTo("external_key", message.getExternal_key());
        query.getFirstInBackground(this);

    }

    public GenerateFromExternalKey(Message object) {
        this(object,null);
    }

    @Override
    public void done(ParseObject parseObject, ParseException e) {
        if (e == null )
        {
            message.GenerateFromParseObject(parseObject);
            if (CallMethod!= null)
                CallMethod.AddObject(message);
            /**
             * save in the DB
             */

            MessagesDB messagesDB = new MessagesDB();
            messagesDB.date= message.getDateObject();
            messagesDB.Text= message.getText();
            messagesDB.is_incoming = 1;
            messagesDB.id= message.getObjectId();
            messagesDB.Conversation_id = message.getConversationObjectId();

            DbHelper.InsertMessage(messagesDB);

        } else{
            e.printStackTrace();
        }
    }
}
