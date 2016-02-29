package info.androidhive.materialtabs.DB;

import android.provider.BaseColumns;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ido on 27/02/2016.
 */
public final class MessagesDB {
    public String id="";
    public String Text="";
    public int is_incoming= 0;
    public Date date= new Date();
    public String Conversation_id="";

    public MessagesDB(){}
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static abstract class Entries implements BaseColumns{
        public static final String TABLE_NAME = "Messages";
        public static final String ID = "objectId";
        public static final String TEXT= "text";
        public static final String IS_INCOMING= "isIncoming";
        public static final String DATE= "date";
        public static final String CONVERSATION_ID="conversationId";


    }

}
