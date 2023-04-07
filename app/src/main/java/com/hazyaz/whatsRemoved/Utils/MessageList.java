package com.hazyaz.whatsRemoved.Utils;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hazyaz.whatsRemoved.Adapters.CustomMessageAdapter;
import com.hazyaz.whatsRemoved.Database.DatabaseHelper;
import com.hazyaz.whatsRemoved.R;

import java.util.ArrayList;

public class MessageList extends AppCompatActivity {


    DatabaseHelper db;
    RecyclerView mRecyclerView;
    ImageView deleteImage;

    ArrayList<ArrayList> MainMess = new ArrayList<>();
    ArrayList<String> mess;
    ImageButton bannerbtn;
    private String tempMess = "rts3";
    private String min = "wszs";
    Dialog dialog;
    String mUser = "";


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);


        mRecyclerView = findViewById(R.id.message_recycler);
        deleteImage = findViewById(R.id.delete_chats);

        Intent intent = getIntent();
        mUser = intent.getStringExtra("EXTRA_SESSION_ID");


        Toolbar mToolbar = findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mUser);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        db = new DatabaseHelper(getApplicationContext());
        Cursor cursor = db.getAllData();


        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_2_NAME)) != null) {


                    if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_2_NAME)).equals(mUser)) {

                        mess = new ArrayList<>();
                        String message = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_4_MESSAGE));
                        String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_3_DATE));
                        min = message;
                        if (tempMess.equals(min)) {

                        } else {
                            mess.add(message);
                            mess.add(time);

                            MainMess.add(mess);
                        }
                        tempMess = min;
                    }
                }
            }
            while (cursor.moveToNext());
        }

        CustomMessageAdapter mAdapter = new CustomMessageAdapter(MainMess, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        deleteImage.setOnClickListener(view -> {
            if (!mUser.isEmpty()) {
                openDialogForPermission();
            }
        });

    }


    private void openDialogForPermission() {
        int theme_dialog = R.style.Theme_Dialog;
        dialog = new Dialog(this, theme_dialog);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        );
        dialog.setContentView(R.layout.dialog_ask_delete_permission);

        TextView yes = dialog.findViewById(R.id.button1);
        TextView no = dialog.findViewById(R.id.button2);

        no.setOnClickListener(view -> dialog.dismiss());
        yes.setOnClickListener(view -> {
            db.deleteCourse(mUser);
//            refreshData();
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Chat Deleted", Toast.LENGTH_SHORT).show();
            finish();
        });
        dialog.show();
    }


    @Override
    public void onBackPressed() {

    }


}
