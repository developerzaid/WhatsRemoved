package com.hazyaz.whatsRemoved.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hazyaz.whatsRemoved.FileObserverServiceMain;


public class BroadCastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ActiveService.class));
    }
}
