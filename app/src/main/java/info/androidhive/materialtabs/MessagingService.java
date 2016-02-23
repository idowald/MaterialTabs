package info.androidhive.materialtabs;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import info.androidhive.materialtabs.activity.MessagingActivity;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.Message;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;


/**
 * Created by ido on 21/11/2015.
 * the purpose of this class is to get messages from the pubnub server and if registered to UI to pop them in the correct window.
 * also it's in charge of the notification
 *
 * Message Flow Algorithm:
 * Sender-side:
 * create in message an external_id key- generated fast on system by Random+Date stamp
 * user send through Pubnub the new Message fastly including the External key in JSON
 * at the same time: the user update his View with new message.
 * send request to Parse cloud to save a new Message including the external_id
 *
 * Receiver-side:
 * got a new message from PUBNUB that includes an external_id
 * refresh VIEW that attached to the message with it's content.
 * send request to Parse cloud to generate the message that contain the same external_id
 */

public class MessagingService extends IntentService{
    // Binder given to clients- it helps to send messages- saving data and network time
    //also gives a better object oriented
    private final IBinder mBinder = new LocalBinder();
    private String Myusername=null;
    private User my_user = null ;

    Pubnub pubnub= null;
    LocalBroadcastManager broadcast = null;

    public MessagingService(){
        super("");
    }
    public MessagingService(String name){
        super(name);
    }

    @Override
    public void onCreate() {




        super.onCreate();
        Myusername = getSharedPreferences("userdetails", MODE_PRIVATE).getString("username","");
        AddParseObject callback =null;
        my_user = new User("","",Myusername,callback );
        broadcast=  LocalBroadcastManager.getInstance(this);

        pubnub = new Pubnub("pub-c-363f7383-eae3-44f3-adc5-5f7092ffd6b5", "sub-c-1da00c8a-86d4-11e5-9320-02ee2ddab7fe");
        try {
            pubnub.subscribe(Myusername, new Callback() {


                public void successCallback(String channel, Object message) {

                    Message myMessage =new Message((JSONObject)message);
                    if (myMessage.isUrgent() | myMessage.getConversationType() == Conversation.Conversation_type.PRIVATE) {
                       ThrowNotification(myMessage); //TODO add this
                    }
                    User senderUser = null;
                    try {
                        senderUser = new User((JSONObject) message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    UpdateUIifExist(myMessage, senderUser);

                }

                public void errorCallback(String channel, PubnubError error) {
                    System.out.println(error.getErrorString());
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }

    }



    @Override
    protected void onHandleIntent(Intent workIntent) {

        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        // Do work here, based on the contents of dataString

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    public class LocalBinder extends Binder {
        public MessagingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MessagingService.this;
        }
    }
    public void UpdateUIifExist(Message message, User senderUser){
        /**
         * update MessagingActivity if it's on
         */
        Intent intent = new Intent(MessagingActivity.BROADCAST_ACTION);
        // You can also include some extra data.
        Bundle bundle = new Bundle();
        //this guy sending in bundle the intent it wants to the activity
        bundle.putSerializable("message", message);
        bundle.putSerializable("senderuser",senderUser );
        intent.putExtras(bundle);
        broadcast.sendBroadcast(intent);

        Log.v("t", message.toString());
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * this is binded to the messagingActivity to send messages
     */

    public void sendMessage(Message message, ArrayList<User> recipientsIds){
        //this binder is attached to sending messages class and it activated by it
        Log.v("sending"," SendMessage");
        String toArray="";



        /* Publish a message to channel */
        for(User recipient : recipientsIds) {
            if (recipient.getUserName().compareTo(Myusername)!= 0)
            toArray += recipient.getUserName() + "|";
        }
        if (toArray.length() <0)
            return; //empty to
        toArray= toArray.substring(0,toArray.length()-1); // remove last |

        JSONObject jsonMessage= message.toJSONObject(toArray);

        for(User recipient : recipientsIds) {
            if (recipient.getUserName().compareTo(Myusername)!= 0)
                pubnub.publish(recipient.getUserName(), jsonMessage, new Callback() {

            });
        }
        message.CreateAndSaveNewParseObject();

        Log.v("finished class service","");

    }



    public void ThrowNotification(Message myMessage){


        Intent resultIntent = new Intent(this, MessagingActivity.class);
      //  Bundle bundle = new Bundle();
      //  bundle.putString("conversationObjectId",myMessage.getConversationObjectId());
       // resultIntent.putExtra("conversationObjectId", myMessage.getConversationObjectId());
       // bundle.putSerializable("to", myMessage.getTo());
        //resultIntent.putExtra("recipients_ids", myMessage.getTo());
// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        myMessage.getFrom(new NotifyCallback( resultPendingIntent, myMessage.getText())) ;


    }

    class NotifyCallback implements AddParseObject {


        PendingIntent resultPendingIntent;
        String MessageText;

        NotifyCallback(PendingIntent resultPendingIntent, String messageText) {

            this.resultPendingIntent = resultPendingIntent;
            MessageText = messageText;
        }

        @Override
        public void AddObject(AbstractParseObject user) {
            String notificationText = user + ": " + MessageText;
            Notification myNotification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("CONDOC new message")
                    .setContentIntent(resultPendingIntent)
                    .setContentText(notificationText)
                    .setTicker("Notification!")
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, myNotification);
        }
    }
}
