package info.androidhive.materialtabs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

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
 *
 */
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    Context context = null;
    public MyNotificationManager(){

        context = MyApplication.getAppContext();
    alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(context, AlarmReceiver.class);
    alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

    alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 10*1000,alarmIntent);
                    //60 * 1000, alarmIntent);
}

}

