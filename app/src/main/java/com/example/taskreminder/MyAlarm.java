package com.example.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.allyants.notifyme.NotifyMe;

public class MyAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        MediaPlayer mediaPlayer = MediaPlayer.create(context,
                Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();



        Log.d("MyAlarmBelal", "Alarm just fired");

        Toast.makeText(context, "TASK REMINDER", Toast.LENGTH_SHORT).show();






    }


}
