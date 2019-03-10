package in.enzen.taskforum.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.util.Calendar;
@SuppressWarnings("ALL")
/**
 * Created by Rupesh on 3/3/2018.
 */

public class AlarmException extends BroadcastReceiver {
    public static String ACTION_ALARM = "in.enzen.taskforum.ACTION_ALARM";
    private PendingIntent alarmIntent;
    String HOUR_OF_DAY, MINUTE;
    Long time;
    AlarmManager alarmManager;
    // get a Calendar and set the time to 12:00:00
    Calendar startTime = Calendar.getInstance();


    @Override
    public void onReceive(Context context, Intent intent) {
        startTime.set(Calendar.HOUR_OF_DAY, 12);
        startTime.set(Calendar.MINUTE, 0);
        /* get a Calendar at the current time */
        Calendar now = Calendar.getInstance();
        if (now.before(startTime)) {
            // it's not 14:00 yet, start today
            time = startTime.getTimeInMillis();
        } else {
            // start 14:00 tomorrow
            startTime.add(Calendar.DATE, 1);
            time = startTime.getTimeInMillis();
        }

        if (ACTION_ALARM.equals(intent.getAction())) {
            Toast.makeText(context, ACTION_ALARM, Toast.LENGTH_SHORT).show();
        }

        setAlarm();

    }

    private void setAlarm() {
        // set the alarm
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent);
        }
    }
}
