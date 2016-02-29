package info.androidhive.materialtabs.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;

import info.androidhive.materialtabs.MyApplication;

/**
 * Created by ido on 27/02/2016.
 */
public class DbHelper extends SQLiteOpenHelper{
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    public static final String SQL_CREATE_ENTRIES_MESSAGES= "CREATE TABLE "+ MessagesDB.Entries.TABLE_NAME+"(" +
             MessagesDB.Entries.ID +" CHAR(50) PRIMARY KEY NOT NULL," +
            MessagesDB.Entries.DATE +" DATE," +
            MessagesDB.Entries.TEXT+ " CHAR(100),"+
            MessagesDB.Entries.IS_INCOMING+" INT,"+
            MessagesDB.Entries.CONVERSATION_ID+ " char(50),"
           +"FOREIGN KEY("+MessagesDB.Entries.CONVERSATION_ID+") REFERENCES "+ ConversationsDB.Entries.TABLE_NAME+"("+ConversationsDB.Entries.ID+")"+
            ");";

    public static final String SQL_CREATE_ENTRIES_CONVERSATIONS= "CREATE TABLE "+ ConversationsDB.Entries.TABLE_NAME+"(" +
            ConversationsDB.Entries.ID +" CHAR(50) PRIMARY KEY NOT NULL," +
            ConversationsDB.Entries.CONVERSATION_NAME+" CHAR(50)"+
            ");";

    public static final String SQL_DELETE_ENTRIES="drop table "+ MessagesDB.Entries.TABLE_NAME+";"+
    "drop table "+ ConversationsDB.Entries.TABLE_NAME +";";


    public DbHelper() {
        super(MyApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_CONVERSATIONS);
        db.execSQL(SQL_CREATE_ENTRIES_MESSAGES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static void ReadfromDB(){
        SQLiteDatabase db =new  DbHelper().getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                ConversationsDB.Entries.ID,
                ConversationsDB.Entries.CONVERSATION_NAME,

        };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                ConversationsDB.Entries.ID + " DESC";

        Cursor cursor = db.query(
                ConversationsDB.Entries.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        cursor.moveToFirst();
        Log.e("count of db"," "+ cursor.getCount());

    }
    public static void InsertMessage(MessagesDB messagesDB){

        DbHelper helper = new DbHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MessagesDB.Entries.ID, messagesDB.id);
        values.put(MessagesDB.Entries.TEXT,messagesDB.Text);
        values.put(MessagesDB.Entries.DATE,MessagesDB.DATE_FORMAT.format(messagesDB.date));
        values.put(MessagesDB.Entries.IS_INCOMING,messagesDB.is_incoming);
        values.put(MessagesDB.Entries.CONVERSATION_ID, messagesDB.Conversation_id);

        db.insert(MessagesDB.Entries.TABLE_NAME,"NULL",values);


    }
    public static MessagesDB getLastMessage(String conversation_id){


        SQLiteDatabase db =new  DbHelper().getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                MessagesDB.Entries.ID,
                MessagesDB.Entries.IS_INCOMING,
                MessagesDB.Entries.TEXT,
                MessagesDB.Entries.DATE,
                MessagesDB.Entries.CONVERSATION_ID,

        };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                MessagesDB.Entries.ID + " DESC";


        Cursor cursor = db.query(
                MessagesDB.Entries.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                MessagesDB.Entries.CONVERSATION_ID +"=?",                                // The columns for the WHERE clause
                new String[]{ conversation_id },                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        cursor.moveToFirst();
        if ( cursor.getCount() ==0)
            return null; //no messages yet
        MessagesDB messagesDB = new MessagesDB();
        messagesDB.id = cursor.getString(0);
        messagesDB.is_incoming = cursor.getInt(1);
        messagesDB.Text = cursor.getString(2);
        try {
            messagesDB.date = MessagesDB.DATE_FORMAT.parse(cursor.getString(3));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        messagesDB.Conversation_id= cursor.getString(4);

        return messagesDB;



    }
}
