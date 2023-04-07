package com.hazyaz.whatsRemoved.Fragments;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.hazyaz.whatsRemoved.Adapters.CustomRecyclerViewAdapter;
import com.hazyaz.whatsRemoved.Database.DatabaseHelper;
import com.hazyaz.whatsRemoved.R;
import com.hazyaz.whatsRemoved.Utils.OnDeleteClick;
import com.hazyaz.whatsRemoved.Utils.UserData;
import com.hazyaz.whatsRemoved.databinding.UsernameFragmentBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class UserNameFragment extends Fragment implements OnDeleteClick {

    RecyclerView mRecyclerView;
    DatabaseHelper db;
    Cursor cursor;
    private LinearLayout mNoData;
    private String checkdata;
    private FrameLayout mFrameLayout;
    static ArrayList<UserData> list;
    private InterstitialAd mInterstitialAd;
    Dialog dialog;

    private static void printMap(final Map mp, final Map sm) {
        new Thread(() -> {
            Iterator it = mp.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                Log.d("Notification_user", pair.getKey() + " = " + pair.getValue());
                UserData us = new UserData( pair.getKey().toString(), pair.getValue().toString(), sm.get(pair.getKey()).toString());
                list.add(us);
                it.remove(); // avoids a ConcurrentModificationException
            }
        }).start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.hazyaz.whatsRemoved.databinding.UsernameFragmentBinding binding = UsernameFragmentBinding.inflate(inflater);

        mRecyclerView = binding.convList;
        mNoData = binding.NoDataRecyclerView;
        mFrameLayout = binding.bgchanger;
        db = new DatabaseHelper(getContext());

//        List names = new ArrayList<>();
        getAllChats();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllChats();
    }

    private void getAllChats() {
        cursor = db.getAllData();
        HashMap<String, String> hashMap = new LinkedHashMap<>();
        HashMap<String, String> DateMap = new HashMap<String, String>();

        list = new ArrayList<UserData>();

        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_2_NAME));
                String message = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_4_MESSAGE));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_3_DATE));

                if (data != null) {
                    if (data.equals("You") || data.equals("Missed group voice call") || data.equals("Backup paused") || data.equals("Backup in progress")
                            || data.equals("Missed group video call") || data.equals("WhatsApp Web") || data.equals("WhatsApp") || data.equals("Recent WhatsApp login")
                            || data.equals("Missed voice call") || data.equals("Missed video call") || data.matches("Missed calls")
                    ) {


                    } else {
                        hashMap.put(data, message);
                        DateMap.put(data, date);

                    }
                }
                checkdata = message;
            } while (cursor.moveToNext());
        }
        printMap(hashMap, DateMap);


        if (checkdata != null) {
            Collections.sort(list, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    UserData p1 = (UserData) o1;
                    UserData p2 = (UserData) o2;
                    return p1.getTime().compareToIgnoreCase(p2.getTime());
                }
            });

            mNoData.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            CustomRecyclerViewAdapter mAdapter = new CustomRecyclerViewAdapter(list, getContext(), this::deleteChats);

            mRecyclerView.setAdapter(mAdapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setReverseLayout(true);

            linearLayoutManager.setStackFromEnd(true);


            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            mRecyclerView.setNestedScrollingEnabled(false);
        } else {
            mNoData.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }


    }

    private void openDialogForPermission(UserData userData) {
        int theme_dialog = R.style.Theme_Dialog;
        dialog = new Dialog(requireActivity(), theme_dialog);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        );
        dialog.setContentView(R.layout.dialog_ask_delete_permission);

        TextView buttonOne = dialog.findViewById(R.id.button1);
        TextView buttonTwo = dialog.findViewById(R.id.button2);

        buttonOne.setOnClickListener(view -> dialog.dismiss());
        buttonTwo.setOnClickListener(view -> {
            db.deleteCourse(userData.getName());
            refreshData();
            dialog.dismiss();
            Toast.makeText(requireContext(), "Chat Deleted", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }

    private void refreshData() {
        cursor = db.getAllData();
        HashMap<String, String> hashMap = new LinkedHashMap<>();
        HashMap<String, String> DateMap = new HashMap<String, String>();

        list = new ArrayList<>();
        Log.d("Notification_user","Size - "+cursor.getCount());
        if (cursor.getCount()!=0) {
            if (cursor.moveToFirst()) {
                do {
                    int userId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_1_ID));
                    String data = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_2_NAME));
                    String message = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_4_MESSAGE));
                    String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_3_DATE));

                    if (data != null) {
                        if (data.equals("You") || data.equals("Missed group voice call") || data.equals("Backup paused") || data.equals("Backup in progress")
                                || data.equals("Missed group video call") || data.equals("WhatsApp Web") || data.equals("WhatsApp") || data.equals("Recent WhatsApp login")
                                || data.equals("Missed voice call") || data.equals("Missed video call") || data.matches("Missed calls")
                        ) {


                        } else {
                            hashMap.put(data, message);
                            DateMap.put(data, date);

                        }
                    }
                    checkdata = message;
                } while (cursor.moveToNext());
            }
            printMap(hashMap, DateMap);

            if (checkdata != null) {
                Collections.sort(list, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        UserData p1 = (UserData) o1;
                        UserData p2 = (UserData) o2;
                        return p1.getTime().compareToIgnoreCase(p2.getTime());
                    }
                });

                mNoData.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                CustomRecyclerViewAdapter mAdapter = new CustomRecyclerViewAdapter(list, getContext(), this::deleteChats);

                mRecyclerView.setAdapter(mAdapter);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setReverseLayout(true);

                linearLayoutManager.setStackFromEnd(true);


                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                mRecyclerView.setNestedScrollingEnabled(false);
            } else {
                mNoData.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }

        }else {
            mNoData.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void deleteChats(UserData userData) {
        if (userData != null) {
            openDialogForPermission(userData);
        }
    }


}