package com.hazyaz.whatsRemoved.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hazyaz.whatsRemoved.R;


public class CustomStoryAdapter extends ArrayAdapter {

    private final Context mContext;

    public String[] files;
    private final LayoutInflater inflater;


    public CustomStoryAdapter(Context c,String[] files){
        super(c,R.layout.bg_img,files);
        this.mContext = c;
        this.files=files;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        if (files!=null){
            Log.d("TAG", "getView:S "+ files.length);
            return files.length;
        }
        else { return 0;}
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }



    public View getView(int position, View convertView, ViewGroup parent) {

        if (files != null) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.bg_img, parent, false);
            }

            Log.d("TAG", "getView: "+ files[position]);

            Glide
                    .with(mContext)
                    .load(files[position])
                    .into((ImageView) convertView);
        }
            return convertView;

    }



}
