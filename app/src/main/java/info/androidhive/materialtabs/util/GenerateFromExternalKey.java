package info.androidhive.materialtabs.util;

import android.util.Log;

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
public class GenerateFromExternalKey extends Thread implements GetCallback<ParseObject>{
    /**
     * this class helps to get messages with external key instead of object id
     */
    private int tries= 2;
    private Message message = null;
    private AddParseObject CallMethod= null;
    private ParseQuery<ParseObject> query;



    public GenerateFromExternalKey(Message message, AddParseObject callMethod) {
        this.message = message;
        CallMethod = callMethod;
         query = ParseQuery.getQuery(message.getTableName()); //for class Remark it will go to Remarks

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

          /*  MessagesDB messagesDB = new MessagesDB();
            messagesDB.date= message.getDateObject();
            messagesDB.Text= message.getText();
            messagesDB.is_incoming = 1;
            messagesDB.id= message.GetObjectId();
            messagesDB.Conversation_id = message.getConversationObjectId();
            messagesDB.is_new = 1;

            DbHelper.InsertMessage(messagesDB);*/

        } else{
            if (tries-- >0) {
                new myThread(this).start();
            } else
            Log.e("",e.getMessage());
        }
    }


    class myThread extends Thread{
        GenerateFromExternalKey generator= null;

        myThread(GenerateFromExternalKey generateFromExternalKey){
            generator= generateFromExternalKey;
        }

        @Override
        public void run() {
            try {
                sleep(1300);
                query.getFirstInBackground(generator);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

        }
    }
}
