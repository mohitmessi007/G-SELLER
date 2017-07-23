package com.example.mohit.xmohit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;


public class imagelistadapter extends ArrayAdapter<ImageUpload> {

    private Activity context;
    private int resource;
    private List<ImageUpload> listimage;

    public imagelistadapter(@NonNull Activity context, @LayoutRes int resource, List<ImageUpload> objects) {
        super(context, resource, objects);
        this.context =context;
        this.resource = resource;
        listimage = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertview, @NonNull ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);
        TextView tvname = (TextView) v.findViewById(R.id.imgname);
        ImageView img = (ImageView) v.findViewById(R.id.imgview);

        TextView tv = (TextView) v.findViewById(R.id.descr);

        tvname.setText(listimage.get(position).getName());
        tv.setText(listimage.get(position).getDescription());
        Glide.with(context).load(listimage.get(position).getUrl()).into(img);

        return v;
    }


}
