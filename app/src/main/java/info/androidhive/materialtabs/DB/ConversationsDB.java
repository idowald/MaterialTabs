package info.androidhive.materialtabs.DB;

import android.provider.BaseColumns;

import com.parse.ParseCloud;

/**
 * Created by ido on 27/02/2016.
 */
public final class ConversationsDB {
    public boolean isSilenced= false;
    public String conversationName="";
    public String id="";
    public ConversationsDB(){}

    public static abstract class Entries implements BaseColumns {
        public static final String TABLE_NAME = "Conversations";
        public static final String ID = "objectId";
        public static final String ISSILENCED = "isSilenced";
        public static final String CONVERSATION_NAME= "conversationName";






    }

}
