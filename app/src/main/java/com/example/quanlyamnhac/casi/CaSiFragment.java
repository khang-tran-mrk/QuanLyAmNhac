package com.example.quanlyamnhac.casi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.quanlyamnhac.Database;
import com.example.quanlyamnhac.R;
import com.example.quanlyamnhac.adapter.CustomAdapterCaSi;
import com.example.quanlyamnhac.model.CaSiModel;

import java.util.ArrayList;


public class CaSiFragment extends Fragment {
    Database database;
    ListView lv_tablecasi;


    ArrayList<CaSiModel> caSiArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.casi_fragment, container, false);

        lv_tablecasi = root.findViewById(R.id.lv_tablecasi);

        caSiArrayList = new ArrayList<>();
        CustomAdapterCaSi adapter = new CustomAdapterCaSi(getContext(), R.layout.casi_item_list_view, caSiArrayList);
        lv_tablecasi.setAdapter(adapter);
        database = new Database(getContext(), "QuanLyAmNhac.sqlite", null, 1);
        getListCasi();
        adapter.notifyDataSetChanged();

        lv_tablecasi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final View update_layout = inflater.inflate(R.layout.casi_fragment_update, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Thực hiện thao tác");
                alert.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder fix = new AlertDialog.Builder(getContext());
                        fix.setView(update_layout);

                        EditText up_MaCS = (EditText) update_layout.findViewById(R.id.editmacs);
                        EditText up_tencasi = (EditText) update_layout.findViewById(R.id.editnamecs);

                        Button btn_up_ns = (Button) update_layout.findViewById(R.id.btnUpdateCS);
                        Button btnBacKNS = (Button) update_layout.findViewById(R.id.btnBacKCS);
                        up_MaCS.setText(caSiArrayList.get(position).getMaCaSi());
                        up_tencasi.setText(caSiArrayList.get(position).getTenCaSi());


                        btnBacKNS.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent casi = new Intent(getContext(), CaSiActivity.class);
                                casi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(casi);
                            }
                        });

                        AlertDialog alertDialog = fix.create();
                        alertDialog.setCancelable(false);
                        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        alertDialog.show();


                        btn_up_ns.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("abc", "" + position);
                                Log.d("abc", "" + up_tencasi.getText());
                                database.QueryData("UPDATE CaSi SET TenCaSi  = '" + up_tencasi.getText().toString() + "' where MaCaSi='" + caSiArrayList.get(position).getMaCaSi() + "'");
                                String name = up_tencasi.getText().toString();
                                caSiArrayList.set(position, new CaSiModel(caSiArrayList.get(position).getMaCaSi(), name));
                                adapter.notifyDataSetChanged();

                                alertDialog.dismiss();


                            }
                        });

                        adapter.notifyDataSetChanged();
                    }


                });

                alert.setNegativeButton("Xoá", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Cursor data = database.GetData("SELECT * FROM BieuDien where MaCaSi = '" + caSiArrayList.get(position).getMaCaSi() + "'");
                        if(data.moveToNext()) {
                            Toast.makeText(getContext(), "Lỗi khoá ngoại! Không thể xoá!!!", Toast.LENGTH_LONG).show();
                            return;
                        }


                        System.out.println("xoa ne !! = " + caSiArrayList.get(position).getMaCaSi());
                        database.QueryData("DELETE FROM CaSi WHERE MaCaSi   = '" + caSiArrayList.get(position).getMaCaSi() + "'");
                        caSiArrayList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                alert.show();
                return true;
            }
        });

        //Search
        SearchView search_bar = root.findViewById(R.id.search_bar);
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CustomAdapterCaSi new_adapter;
                getListCasi();
                if (newText.equals("") || newText.equals(null)) {

                    lv_tablecasi.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    return false;
                }


                ArrayList<CaSiModel> search_result = new ArrayList<CaSiModel>();
                caSiArrayList.forEach(item -> {
                    if (
                            item.getMaCaSi().toLowerCase().contains(newText.toLowerCase()) ||
                                    item.getTenCaSi().toLowerCase().contains(newText.toLowerCase())
                    )
                        search_result.add(item);
                });
                caSiArrayList = search_result;
                new_adapter = new CustomAdapterCaSi(getContext(), R.layout.casi_item_list_view, search_result);
                lv_tablecasi.setAdapter(new_adapter);

                return false;
            }
        });

        return root;
    }

    private void getListCasi() {
        Cursor data = database.GetData("SELECT * FROM CaSi");
        caSiArrayList.clear();

        while (data.moveToNext()) {
            String id = data.getString(0);
            String name = data.getString(1);

            caSiArrayList.add(new CaSiModel(id, name));
        }
    }
/*
    public void getDataBieuDien() {
        bieuDienArrayList = new ArrayList<>();
        CustomAdapterBieuDien adapter = new CustomAdapterBieuDien(getContext(), R.layout.bieudien_item_list_view, bieuDienArrayList);
        lvTableBieuDien.setAdapter(adapter);
        Cursor dataBieuDien = database.GetData("SELECT * FROM BieuDien");
        bieuDienArrayList.clear();
        while (dataBieuDien.moveToNext()) {
            Integer id = Integer.parseInt(dataBieuDien.getString(0));
            String macasi = dataBieuDien.getString(1);
            String tencasi = dataBieuDien.getString(2);
            String ngaybieudien = dataBieuDien.getString(3);
            String diadiem = dataBieuDien.getString(4);
            bieuDienArrayList.add(new BieuDienModel(id, macasi, tencasi, ngaybieudien, diadiem));
        }
        adapter.notifyDataSetChanged();
    }

    public String selectName(String id) {
        Cursor dataBieuDien = database.GetData("SELECT * FROM BieuDien");
        while (dataBieuDien.moveToNext()) {
            if (dataBieuDien.getString(1).equals(id)) {
                return dataBieuDien.getString(2);
            }
        }
        return "";
    }

    private void khoitao() {
        sp_macasi = new ArrayList();
        database = new Database(getContext(), "QuanLyAmNhac.sqlite", null, 1);
        Cursor dataBieuDien = database.GetData("SELECT * FROM BieuDien");
        sp_macasi.clear();
        while (dataBieuDien.moveToNext()) {
            String macasi = dataBieuDien.getString(1);
            sp_macasi.add(macasi);
        }

    }*/
}