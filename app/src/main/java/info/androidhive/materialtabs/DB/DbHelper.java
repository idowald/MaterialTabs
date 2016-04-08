package info.androidhive.materialtabs.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;

import info.androidhive.materialtabs.MyApplication;
import info.androidhive.materialtabs.MyNotificationManager;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.Message;

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
            MessagesDB.Entries.CONVERSATION_ID+ " char(50), " +
            MessagesDB.Entries.EXTERNAL_KEY + " CHAR(50), "
            + MessagesDB.Entries.IS_NEW + " INT DEFAULT(0), "
           +"FOREIGN KEY("+MessagesDB.Entries.CONVERSATION_ID+") REFERENCES "+ ConversationsDB.Entries.TABLE_NAME+"("+ConversationsDB.Entries.ID+")"+
            ");";

    public static final String SQL_CREATE_ENTRIES_CONVERSATIONS= "CREATE TABLE "+ ConversationsDB.Entries.TABLE_NAME+"(" +
            ConversationsDB.Entries.ID +" CHAR(50) PRIMARY KEY NOT NULL, " +
            ConversationsDB.Entries.CONVERSATION_NAME+" CHAR(50), "+
            ConversationsDB.Entries.ISSILENCED+" INT DEFAULT(0) "+
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

    public static void TestMessages(){
        SQLiteDatabase db =new  DbHelper().getReadableDatabase();

        Cursor cursor = db.rawQuery("select "+ MessagesDB.Entries.ID +" from "+ MessagesDB.Entries.TABLE_NAME,null);
        Log.v("starting db test","");
        while (cursor.moveToNext()){
            Log.v("message", cursor.getString(0));
        }
        db.close();
    }
    public static void ReadfromDB(){
        SQLiteDatabase db =new  DbHelper().getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                ConversationsDB.Entries.ID,
                ConversationsDB.Entries.CONVERSATION_NAME,
                ConversationsDB.Entries.ISSILENCED

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
        db.close();
        Log.v("count of db"," "+ cursor.getCount());

    }
    public static void InsertMessage(MessagesDB messagesDB){
        Log.v("DBHelper","inserting message "+ messagesDB.Text);
        DbHelper helper = new DbHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MessagesDB.Entries.ID, messagesDB.id);
        values.put(MessagesDB.Entries.TEXT,messagesDB.Text);
        values.put(MessagesDB.Entries.DATE,MessagesDB.DATE_FORMAT.format(messagesDB.date));
        values.put(MessagesDB.Entries.IS_INCOMING,messagesDB.is_incoming);
        values.put(MessagesDB.Entries.CONVERSATION_ID, messagesDB.Conversation_id);
        values.put(MessagesDB.Entries.IS_NEW, messagesDB.is_new);
        values.put(MessagesDB.Entries.EXTERNAL_KEY, messagesDB.external_key);

        db.insert(MessagesDB.Entries.TABLE_NAME,"NULL",values);

        helper.close();
        db.close();

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
                MessagesDB.Entries.IS_NEW,

        };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                MessagesDB.Entries.DATE + " DESC";

        Cursor cursor = db.query(
                MessagesDB.Entries.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                MessagesDB.Entries.CONVERSATION_ID +"=?",                                // The columns for the WHERE clause
                new String[]{ conversation_id },                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
     //  Cursor cursor = db.rawQuery("select * from "+ MessagesDB.Entries.TABLE_NAME +" where "+
      //          MessagesDB.Entries.CONVERSATION_ID+ "= '"+ conversation_id+"'" + " order by "+MessagesDB.Entries.DATE +" DESC",null);
        if (cursor.moveToNext()){
            //return null;
        }
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
        messagesDB.is_new= cursor.getInt(5);

        db.close();


        return messagesDB;



    }
    public static void ReadMessage(String messageID) {
        /**
         * this method takes the message object id and turn the message to not new in db
         */
        if (messageID == null)
            return;
        DbHelper helper = new DbHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MessagesDB.Entries.IS_NEW, 0);

        db.update(MessagesDB.Entries.TABLE_NAME, values, MessagesDB.Entries.ID + " = '" + messageID + "'", null);

        //db.insert(MessagesDB.Entries.TABLE_NAME,"NULL",values);

        db = helper.getReadableDatabase();

        String[] projection = {
                MessagesDB.Entries.EXTERNAL_KEY

        };


        Cursor cursor = db.query(
                MessagesDB.Entries.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                MessagesDB.Entries.ID + "=?",                                // The columns for the WHERE clause
                new String[]{messageID},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        cursor.moveToFirst();
        if (cursor.getCount() == 0)
        {
            helper.close();
            db.close();
            return;
        }
        String external_key= cursor.getString(0);

        helper.close();
        db.close();



        MyNotificationManager.RemoveNotification(external_key);
        //todo test why it's not working

    }

    public static void TestDB(){
        SQLiteDatabase db =new  DbHelper().getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from "+ MessagesDB.Entries.TABLE_NAME +" where "+
                MessagesDB.Entries.CONVERSATION_ID+ "= 'NJJkVkk5Cl'" + " order by "+MessagesDB.Entries.DATE +" DESC",null);
        Log.v("TestDB","starting  \n");
        while (cursor.moveToNext()){
            for (int i=0; i<7; i++)
                Log.v("TestDB"+ i,cursor.getString(i));

        }
        Log.v("TestDB","finished  \n");

        db.close();
    }
    public static boolean isSilenced(String Conversation_id){

        SQLiteDatabase db =new  DbHelper().getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                ConversationsDB.Entries.ISSILENCED

        };


        Cursor cursor = db.query(
                ConversationsDB.Entries.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                ConversationsDB.Entries.ID +" = ?",                                // The columns for the WHERE clause
                new String[]{Conversation_id},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        cursor.moveToFirst();
        Boolean isSilenced= cursor.getInt(0)==1;
        db.close();

        return isSilenced;
    }

    public static ArrayList<Message> getUnreadMeassages(){
        //todo implement and attach to mynotification instead of hashmap
        SQLiteDatabase db =new  DbHelper().getReadableDatabase();
/*
// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                MessagesDB.Entries.ID,
                MessagesDB.Entries.EXTERNAL_KEY,
                MessagesDB.Entries.DATE,
                MessagesDB.Entries.TEXT,
                MessagesDB.Entries.CONVERSATION_ID

        };


        Cursor cursor = db.query(
                ConversationsDB.Entries.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                ConversationsDB.Entries.ID +" = ?",                                // The columns for the WHERE clause
                new String[]{Conversation_id},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        cursor.moveToFirst();*/

        db.close();
        return null;
    }


    public static ArrayList<Message> getAllUnreadMessages(){
        ArrayList<Message> messages= new ArrayList<>();


        SQLiteDatabase db =new  DbHelper().getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                MessagesDB.Entries.ID

        };


        Cursor cursor = db.query(
                MessagesDB.Entries.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                MessagesDB.Entries.IS_NEW +" = ?",                                // The columns for the WHERE clause
                new String[]{"1"},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        while (cursor.moveToNext()){
            messages.add(new Message(cursor.getString(0), null));
        }
        db.close();
        return messages;

    }




}
