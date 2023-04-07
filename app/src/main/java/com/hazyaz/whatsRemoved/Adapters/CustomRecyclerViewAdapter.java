package com.hazyaz.whatsRemoved.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.hazyaz.whatsRemoved.R;
import com.hazyaz.whatsRemoved.Utils.MessageList;
import com.hazyaz.whatsRemoved.Utils.OnDeleteClick;
import com.hazyaz.whatsRemoved.Utils.UserData;

import java.util.ArrayList;


public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {


    private final ArrayList<UserData> userData;
    private final Context context;
    private final OnDeleteClick deleteClick;

    public CustomRecyclerViewAdapter(ArrayList<UserData> userData, Context context, OnDeleteClick onDeleteClick) {

        this.context = context;
        this.userData = userData;
        this.deleteClick = onDeleteClick;
    }

    @NonNull
    @Override
    public CustomRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.usernames, parent, false);

        return new CustomRecyclerViewAdapter.ViewHolder(itemLayoutView);
    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {


        viewHolder.txtViewTitle.setText(userData.get(i).getName());
        viewHolder.imgViewIcon.setImageResource(R.drawable.userprofile);
        viewHolder.userMessage.setText(userData.get(i).getMessage());

        viewHolder.mLinerLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessageList.class);
            intent.putExtra("EXTRA_SESSION_ID", userData.get(i).getName());
            context.startActivity(intent);
        });
        try{
            viewHolder.mLinerLayout.setOnLongClickListener(v->{
                deleteClick.deleteChats(userData.get(i));
                return true;
            });
        }catch (Exception e){
            Log.d("TAG", "onBindViewHolder: "+e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtViewTitle;
        private final ImageView imgViewIcon;
        public TextView userMessage;
        private final RelativeLayout mLinerLayout;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mLinerLayout = itemLayoutView.findViewById(R.id.cons1);
            txtViewTitle = itemLayoutView.findViewById(R.id.xUsername);
            imgViewIcon = itemLayoutView.findViewById(R.id.xUserImage);
            userMessage = itemLayoutView.findViewById(R.id.textView3);

        }
    }

}