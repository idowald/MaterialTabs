package info.androidhive.materialtabs.DB;

import android.provider.BaseColumns;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

import info.androidhive.materialtabs.MyApplication;
import info.androidhive.materialtabs.objects.Message;

/**
 * Created by ido on 27/02/2016.
 */
public final class MessagesDB {
    public String id="";
    public String Text="";
    public int is_incoming= 0;
    public Date date= new Date();
    public String Conversation_id="";
    public String external_key= "";
    public int is_new =0;

    public MessagesDB(){}
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static abstract class Entries implements BaseColumns{
        public static final String TABLE_NAME = "Messages";
        public static final String ID = "objectId";
        public static final String TEXT= "text";
        public static final String IS_INCOMING= "isIncoming";
        public static final String DATE= "date";
        public static final String CONVERSATION_ID="conversationId";
        public static final String IS_NEW = "IS_NEW";
        public static final String EXTERNAL_KEY = "externalKey";


    }

    public static MessagesDB convertMessageToMessageDB(Message message){
        /*
        this method might brings null pointers (conversation object id and such
         */
        MessagesDB messagesDB= new MessagesDB();

        messagesDB.date = message.getDateObject();
        messagesDB.Text = message.getText();
        messagesDB.is_new = message.isNew()?1:0;
        messagesDB.is_incoming = message.isIncoming(MyApplication.user.getUserName())?1:0;
        messagesDB.Conversation_id = message.getConversationObjectId();
        messagesDB.external_key = message.getExternal_key();
        messagesDB.id = message.GetObjectId();

        return messagesDB;
    }

}
