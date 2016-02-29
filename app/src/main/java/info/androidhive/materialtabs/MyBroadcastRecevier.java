package info.androidhive.materialtabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastRecevier extends BroadcastReceiver {
    public MyBroadcastRecevier() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, MessagingService.class);
        context.startService(startServiceIntent);
    }
}
