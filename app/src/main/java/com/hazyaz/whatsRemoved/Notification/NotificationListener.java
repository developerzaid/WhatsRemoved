package com.hazyaz.whatsRemoved.Notification;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;


import com.hazyaz.whatsRemoved.Database.DatabaseHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NotificationListener extends NotificationListenerService {

    DatabaseHelper db;

    @Override
    public IBinder onBind(Intent mIntent) {
        IBinder mIBinder = super.onBind(mIntent);
        return mIBinder;
    }

    @Override
    public boolean onUnbind(Intent mIntent) {
        return super.onUnbind(mIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("AndroidNotification", "Notification Created");
        Log.d("AndroidNotification", "Notification Created");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {


        db = new DatabaseHelper(getApplicationContext());

        Bundle extras = sbn.getNotification().extras;

        Log.d("AndroidNotification", sbn.toString());


        if (sbn.getPackageName().equals("com.whatsapp")) {
            if (extras.containsKey("android.text") && extras.getCharSequence("android.text") != null) {
                String text = extras.getCharSequence("android.text").toString();

                Log.d("AndroidNotification", text);
                if (!text.contains("new message")) {
                    if (extras.containsKey("android.title")) {

                        String name = extras.getString("android.title");

                        if (Pattern.matches("(.*):.*", name)) {

                            Pattern p = Pattern.compile("(.*)\\([0-9]*\\smessages\\):(.*)");
                            Matcher m = p.matcher(name);
                            if (m.lookingAt()) {
                                name = m.group(1).trim();
                                text = m.group(2) + ": " + text;

                            } else {
                                Matcher m1 = Pattern.compile("(.*):(.*)").matcher(name);
                                if (m1.lookingAt()) {
                                    name = m1.group(1).trim();
                                    text = m1.group(2) + ": " + text;
                                }
                            }
                        }
                        // when received msg -- individual_chat_defaults_1
                        // when received msg is deleted -- silent_notifications_3
                        try {
                            db.insertData(name, "" + System.currentTimeMillis(), text);
                        } catch (Exception e) {
                            Log.d("AndroidNotification_e", e.getMessage());
                        }


                    }

                }

            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        db = new DatabaseHelper(getApplicationContext());

        Bundle extras = sbn.getNotification().extras;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("Android10ServiceN1", sbn.getNotification().getChannelId());
        }


//        if (sbn.getPackageName().equals("com.whatsapp")) {
//            if (extras.containsKey("android.text") && extras.getCharSequence("android.text") != null) {
//                String text = extras.getCharSequence("android.text").toString();
//
//                Log.d("Android10ServiceN1", text);
//                if (!text.contains("new message")) {
//                    if (extras.containsKey("android.title")) {
//
//                        String name = extras.getString("android.title");
//
//                        if (Pattern.matches("(.*):.*", name)) {
//
//                            Pattern p = Pattern.compile("(.*)\\([0-9]*\\smessages\\):(.*)");
//                            Matcher m = p.matcher(name);
//                            if (m.lookingAt()) {
//                                name = m.group(1).trim();
//                                text = m.group(2) + ": " + text;
//
//                            } else {
//                                Matcher m1 = Pattern.compile("(.*):(.*)").matcher(name);
//                                if (m1.lookingAt()) {
//                                    name = m1.group(1).trim();
//                                    text = m1.group(2) + ": " + text;
//                                }
//                            }
//                        }
//                        // when received msg -- individual_chat_defaults_1
//                        // when received msg is deleted -- silent_notifications_3
//                        try {
//                            db.insertData(name, "" + System.currentTimeMillis(), text);
//                        } catch (Exception e) {
//                            Log.d("Android10ServiceN1", "ERROR - "+e.getMessage());
//                        }
//
//
//                    }
//
//                }
//
//            }
//        }
    }
}