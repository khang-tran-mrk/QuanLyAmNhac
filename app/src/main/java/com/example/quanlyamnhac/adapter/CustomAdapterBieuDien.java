package com.example.quanlyamnhac.adapter;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quanlyamnhac.Database;
import com.example.quanlyamnhac.R;
import com.example.quanlyamnhac.model.BieuDienModel;

import java.util.ArrayList;

public class CustomAdapterBieuDien extends ArrayAdapter<BieuDienModel> {
    Context context;
    int resource;
    ArrayList data;
    Database database;
    @Override
    public int getCount() {
        return data.size();
    }

    @NonNull
    @Override
    public View getView(int i, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(resource, null);


        TextView tvMaBD = convertView.findViewById(R.id.tv_mabieudien);
        TextView tvMaCaSi = convertView.findViewById(R.id.tv_macs);
        TextView tvTenCaSi = convertView.findViewById(R.id.tv_tencasi);
        TextView tvBaiHat = convertView.findViewById(R.id.tv_tenbaihat);
        TextView tvNgayBieuDien = convertView.findViewById(R.id.tv_ngaybieudien);
        TextView tvDiaDiem = convertView.findViewById(R.id.tv_diadiem);

        BieuDienModel bieuDienModel = (BieuDienModel) data.get(i);
        System.out.println("ma bieu dien thu i = " +i + " la " + bieuDienModel.getMaBieuDien());
        tvMaBD.setText(Integer.toString(bieuDienModel.getMaBieuDien()));


        tvTenCaSi.setText(selectNameCS(bieuDienModel.getMaCaSi()));
        if (bieuDienModel.getMaBaiHat() != null){
            tvBaiHat.setText(selectNameBH(bieuDienModel.getMaBaiHat()));
        }
        System.out.println("resource la " + resource);
        //2131427437
        if (resource == 2131427437){
            tvMaCaSi.setText(bieuDienModel.getMaCaSi());
        }



        tvNgayBieuDien.setText(bieuDienModel.getNgayBieuDien());
        tvDiaDiem.setText(bieuDienModel.getDiaDiem());
        return convertView;
    }
    public String selectNameCS(String id) {
        System.out.println("ma cs la = " + id);
        database = new Database(getContext(), "QuanLyAmNhac.sqlite", null, 1);
        Cursor datacs = database.GetData("SELECT * FROM CaSi where MaCaSi = '"+ id +"'");
        if (datacs.moveToNext()){
            return datacs.getString(1);
        }

        return "khong co";
    }
    public String selectNameBH(String id) {

        database = new Database(getContext(), "QuanLyAmNhac.sqlite", null, 1);
        Cursor databh = database.GetData("SELECT * FROM BaiHat where MaBaiHat = '"+ id +"'");
        while (databh.moveToNext()) {

            return databh.getString(1);
        }
        return "khong co dau";
    }

    public CustomAdapterBieuDien(Context context, int resource, ArrayList data) {
        super(context, resource);
        this.context = context;
        this.data = data;
        this.resource = resource;
    }
}

