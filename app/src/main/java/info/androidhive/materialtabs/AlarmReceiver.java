package info.androidhive.materialtabs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import info.androidhive.materialtabs.activity.MessagingActivity;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.Message;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;

public class AlarmReceiver extends BroadcastReceiver{
    /*
    this class gets notification alert from the alarm and pop the notification by the selected interval from mynotification manager
     */
    private Context context = null;
    private Message message =null;
    private String messageText= "";
    final static String MESSAGE= "message";
    private PendingIntent resultPendingIntent= null;


    @Override
    public void onReceive(final Context context, Intent intent) {
        Toast.makeText(context,"alarm received!", Toast.LENGTH_LONG).show();
        this.context= context;
        Bundle bundle = intent.getExtras();
        message = (Message) bundle.getSerializable(MESSAGE);
        messageText = message.getText();


        //getting the conversation for the message

        message.getConversation(new AddParseObject() {
            @Override
            public void AddObject(AbstractParseObject object) {
                Intent resultIntent =  new Intent(context, MessagingActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("conversation",(Conversation) object);
                resultIntent.putExtras(b);

                resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                message.getFrom(new AddParseObject() {
                    @Override
                    public void AddObject(AbstractParseObject user) {
                        String notificationText = ((User)user).getFirstName() + ": " + messageText;
                        Notification myNotification = new NotificationCompat.Builder(context)
                                .setContentTitle("CONDOC new message")
                                .setContentIntent(resultPendingIntent)
                                .setContentText(notificationText)
                                .setTicker("Notification!")
                                .setWhen(System.currentTimeMillis())
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setAutoCancel(true)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .build();
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(1, myNotification);

                    }
                });
            }
        });






    }










}
