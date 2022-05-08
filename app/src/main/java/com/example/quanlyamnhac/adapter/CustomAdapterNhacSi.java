package com.example.quanlyamnhac.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.quanlyamnhac.R;
import com.example.quanlyamnhac.model.NhacSiModel;

import java.util.ArrayList;

    public class CustomAdapterNhacSi extends ArrayAdapter<NhacSiModel> {
    Context context;
    int resource;
    ArrayList data;

    @Override
    public int getCount() {
        return data.size();
    }

    @NonNull
    @Override
    public View getView(int i, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(resource, null);


       TextView tvID = convertView.findViewById(R.id.tv_id);
       TextView tvName = convertView.findViewById(R.id.tv_name);
        ImageView im_image = convertView.findViewById(R.id.im_image);
        NhacSiModel musicianDatabase = (NhacSiModel) data.get(i);
        tvID.setText(musicianDatabase.getId());
        tvName.setText(musicianDatabase.getName());
        byte[] hinhAnh =    musicianDatabase.getHinhAnh();
        System.out.println("hinh anh " + hinhAnh);
        if (hinhAnh!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnh,0,hinhAnh.length);
            im_image.setImageBitmap(bitmap);
        }

        return  convertView;



    }

    public CustomAdapterNhacSi(Context context, int resource, ArrayList data) {
        super(context, resource);
        this.context = context;
        this.data = data;
        this.resource = resource;
    }
}

