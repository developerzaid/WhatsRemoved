package com.hazyaz.whatsRemoved.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hazyaz.whatsRemoved.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CustomMessageAdapter extends RecyclerView.Adapter<CustomMessageAdapter.ViewHolder> {

    private final ArrayList<ArrayList> itemsData;
    private final Context context;

    public CustomMessageAdapter(ArrayList<ArrayList> itemsData, Context context) {
        this.itemsData = itemsData;
        this.context = context;
    }


    @NonNull
    @Override
    public CustomMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemLayoutView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.messages, null);
        CustomMessageAdapter.ViewHolder viewHolder = new CustomMessageAdapter.ViewHolder(itemLayoutView);


        return viewHolder;
    }

    private static String getDate(String milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onBindViewHolder(@NonNull CustomMessageAdapter.ViewHolder viewHolder, final int i) {


        viewHolder.txtViewMessage.setText(itemsData.get(i).get(0).toString());
        viewHolder.txtViewDate.setText(getDate(itemsData.get(i).get(1).toString(), "dd MMM hh:mm a"));


    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewMessage;
        public TextView txtViewDate;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewMessage = itemLayoutView.findViewById(R.id.fActualMessage);
            txtViewDate = itemLayoutView.findViewById(R.id.fActualTime);

        }
    }



}
