package info.androidhive.materialtabs;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;

import info.androidhive.materialtabs.DB.DbHelper;
import info.androidhive.materialtabs.DB.MySharedPrefrences;
import info.androidhive.materialtabs.activity.MainActivity;
import info.androidhive.materialtabs.activity.MessagingActivity;
import info.androidhive.materialtabs.activity.SettingsActivity;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.Duty;
import info.androidhive.materialtabs.objects.Message;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;

/**
 * Created by ido on 07/03/2016.
 */
public class MyNotificationManager {
    /**
     * logic behind:
     * if personal message:
     *      1. if silenced chat (from ConversationDB) no alert.
     *      2. else, alert.
     * if Group/Case conversation message:
     *      if switched off MyPrefrences- then no alerts at all
     *      1.important message: alert.
     *      2.if regular message:
     *                          1.if person is registered to case "inDuty" (cloud parse)-> alert
     *                          2. if not registered inDuty:
     *                                                  1. if silenced chat (COnversationDB) no alert
     *                                                  2. else make alert
     *
     *ALGORITHM:
     *   Service add intents to a Vecotor/Message queue-> pop notification+ alarm with interval
     *   1. if the notification was answered-> can be telled if message was read! turn off alarm
     *   2. if wasn't read-> alarm pops checks if message was read. if not pop notification+alarm in queue
     *
     */

    static HashMap<String, Message> messagesMap = new HashMap<>();
    /*
    if more than one conversation having new messages. pop notification with "X new messages" and the result intent should
    be the main activity


     */

    /*
    the key is the external key of the service.
    the notification will be build up on fire notification function.
    each time the read message in DBhelper is being called. search the external within this list and remove it
    the fire notification function also set the timer with alarm to set off on the needed time with the correct notification

     */

    private AlarmManager alarmMgr = null;
    private PendingIntent alarmIntent = null;
    Context context = null;
    public MyNotificationManager(){

        context = MyApplication.getAppContext();
        int minutes= SettingsActivity.getIntervalTime();
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + minutes*60*1000,alarmIntent);
        //60 * 1000, alarmIntent);
    }

    public static void RemoveNotification(String externalMessageID){
        messagesMap.remove(externalMessageID);
    }


    public static void AddNotification(Message message) {
        messagesMap.put(message.getExternal_key(), message);
        CheckNotification(message);

    }
    public static void CheckNotification(Message message) {
        /**
         * a message that contains it's conversation already from the Service
         */
        if (!messagesMap.containsKey(message.getExternal_key()))
            return; //if a message was read already (from alarm service)
        final Message message2 = message;
        if (SettingsActivity.isNotificationOn()) {
            if (message.isUrgent()) {
                FireNotification(message);
            } else {
                boolean issilence = DbHelper.isSilenced(message.getConversationObjectId());
                if (!issilence) {
                    FireNotification(message);
                } else {
                    //test if it's team conversation if it is, test if onDuty
                    message.getConversation(new AddParseObject() {
                        @Override
                        public void AddObject(AbstractParseObject object) {
                            Conversation conversation = (Conversation) object;
                            if (conversation.IsGROUP()) {
                                //check if user is on duty at the exact group -fire
                                ParseQuery<ParseObject> innerquery = ParseQuery.getQuery("Users");
                                innerquery.whereEqualTo("userName", MyApplication.user.getUserName());
                                ParseQuery<ParseObject> conversationquery = ParseQuery.getQuery("Conversations");
                                conversationquery.whereEqualTo("objectId", conversation.getConversationObjectId()); //todo test if works

                                ParseQuery<ParseObject> queryDuties = ParseQuery.getQuery("Dutys");
                                queryDuties.whereMatchesQuery("user", innerquery);
                                queryDuties.whereMatchesQuery("conversation", conversationquery);


                                queryDuties.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            for (ParseObject object : objects) {

                                                Duty d = new Duty(object, new AddParseObject() {
                                                    @Override
                                                    public void AddObject(AbstractParseObject object) {
                                                        Duty duty = (Duty) object;
                                                        if (duty.isInDuty()) {
                                                            FireNotification(message2);
                                                        }
                                                    }
                                                });


                                            }
                                        } else {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            } else if (conversation.IsCASE()) {
                                //if user is having any onduty fire

                                ParseQuery<ParseObject> innerquery = ParseQuery.getQuery("Users");
                                innerquery.whereEqualTo("userName", MyApplication.user.getUserName());

                                ParseQuery<ParseObject> queryDuties = ParseQuery.getQuery("Dutys");
                                queryDuties.whereMatchesQuery("user", innerquery);


                                queryDuties.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            for (ParseObject object : objects) {

                                                Duty d = new Duty(object, new AddParseObject() {
                                                    @Override
                                                    public void AddObject(AbstractParseObject object) {
                                                        Duty duty = (Duty) object;
                                                        if (duty.isInDuty()) {
                                                            FireNotification(message2);
                                                        }
                                                    }
                                                });


                                            }
                                        } else {
                                            e.printStackTrace();
                                        }

                                    }

                                });
                            }

                        }
                    });
                }

            }
        }



    }
    private static void FireNotification(Message message){
        Context context= MyApplication.getAppContext();
        String notificationText= "";
        boolean ismultiply = isMultiplyConversation();

        Intent resultIntent = null;
        if (ismultiply){
            /*
            pending intent to mainactivity
             */
             resultIntent = new Intent(context, MainActivity.class);



             notificationText = "you've got "+ messagesMap.size() + " new messages!";
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            Notification myNotification = new NotificationCompat.Builder(context)
                    .setContentTitle("CONDOC new message")
                    .setContentIntent(resultPendingIntent)
                    .setContentText(notificationText)
                    .setTicker("Notification!")
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.app_icon)
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, myNotification);


        } else{
            /*
            pending intent to exact messagingactivity
             */
            resultIntent = new Intent(context, MessagingActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("conversationObjectId",message.getConversationObjectId());

            // resultIntent.putExtra("conversationObjectId", myMessage.getConversationObjectId());
            //bundle.putSerializable("to", myMessage.getTo());
            // resultIntent.putExtra("recipients_ids", message.getTo());
// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
            resultIntent.putExtras(bundle);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            message.getFrom(new NotifyCallback( resultPendingIntent,notificationText)) ;


        }


         AlarmManager alarmMgr = null;
        PendingIntent alarmIntent = null;
        int minutes= SettingsActivity.getIntervalTime();
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle b= new Bundle();
        b.putSerializable(AlarmReceiver.MESSAGE,message);
        intent.putExtras(b);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + minutes*60*1000,alarmIntent);








    }

    private static boolean isMultiplyConversation(){
        String conversation_id = null;
        for (Message message : messagesMap.values()){
            if (conversation_id == null) {
                conversation_id = message.getConversationObjectId();
            }
            else{
                if (! message.getConversationObjectId().equals(conversation_id)){
                    return true;
                }
            }
        }
        return false;
    }



}

class NotifyCallback implements AddParseObject {


    PendingIntent resultPendingIntent;
    String MessageText;
    Context context =MyApplication.getAppContext();

    NotifyCallback(PendingIntent resultPendingIntent, String messageText) {

        this.resultPendingIntent = resultPendingIntent;
        MessageText = messageText;
    }

    @Override
    public void AddObject(AbstractParseObject user) {
        String notificationText= "";
        if (MyNotificationManager.messagesMap.size()>1){
            notificationText =MyNotificationManager.messagesMap.size() + " messages from " +((User)user).getFirstName();
        }else{
            notificationText = ((User)user).getFirstName() + ": " + MessageText;
        }

        Notification myNotification = new NotificationCompat.Builder(context)
                .setContentTitle("CONDOC new message")
                .setContentIntent(resultPendingIntent)
                .setContentText(notificationText)
                .setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.app_icon)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, myNotification);
    }



}


