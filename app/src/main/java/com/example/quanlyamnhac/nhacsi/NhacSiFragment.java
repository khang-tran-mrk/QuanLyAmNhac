package com.example.quanlyamnhac.nhacsi;


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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.quanlyamnhac.Database;
import com.example.quanlyamnhac.MainActivity;
import com.example.quanlyamnhac.R;
import com.example.quanlyamnhac.adapter.CustomAdapterBaiHat;
import com.example.quanlyamnhac.adapter.CustomAdapterBieuDien;
import com.example.quanlyamnhac.adapter.CustomAdapterNhacSi;
import com.example.quanlyamnhac.model.BaiHatModel;
import com.example.quanlyamnhac.model.BieuDienModel;
import com.example.quanlyamnhac.model.NhacSiModel;

import java.util.ArrayList;

public class NhacSiFragment extends Fragment {

    ArrayList<NhacSiModel> MusicianArrayList;
    Database database;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.nhacsi_fragment, container, false);
        final ListView lv_tablemusician = root.findViewById(R.id.lv_tablemusician);


        MusicianArrayList = new ArrayList<>();
        CustomAdapterNhacSi adapter = new CustomAdapterNhacSi(getContext(), R.layout.nhacsi_item_list_view, MusicianArrayList);
        lv_tablemusician.setAdapter(adapter);


        database = new Database(getContext(), "QuanLyAmNhac.sqlite", null, 1);

        getList_NhacSi();
        adapter.notifyDataSetChanged();

        //tao table trong db neu chua co
        init_db();

        lv_tablemusician.setClickable(true);

        lv_tablemusician.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                final View baihatfornhacsi_layout = inflater.inflate(R.layout.nhacsi_fragment_baihat, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setPositiveButton("Xem bài hát của nhạc sĩ này", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        AlertDialog.Builder fix = new AlertDialog.Builder(getContext());
                        fix.setView(baihatfornhacsi_layout);

                        TextView ten_ns = baihatfornhacsi_layout.findViewById(R.id.ten_ns);
                        TextView ten_bh = baihatfornhacsi_layout.findViewById(R.id.ten_bh);
                        final ListView lv_baihatofnhacsi = baihatfornhacsi_layout.findViewById(R.id.lv_baihatofnhacsi);
                        // final ListView bh_for_ns_name = baihatfornhacsi_layout.findViewById(R.id.bh_tenbaihat);
                        final ListView lv_bieudienforbaihat = baihatfornhacsi_layout.findViewById(R.id.lv_bieudienforbaihat);
                        Button btn_back_bhforns = baihatfornhacsi_layout.findViewById(R.id.btn_back_bhforns);

                        ArrayList<BaiHatModel> BaiHatArrayList = new ArrayList<>();

                        CustomAdapterBaiHat adapterbh = new CustomAdapterBaiHat(getContext(), R.layout.nhacsi_item_listbaihat_view, BaiHatArrayList);
                        lv_baihatofnhacsi.setAdapter(adapterbh);

                        //System.out.println("ma nhac si la :" + MusicianArrayList.get(position).getId());
                        ten_ns.setText(MusicianArrayList.get(position).getName());
                        Cursor dataBaiHat = database.GetData("SELECT * FROM BaiHat  where MaNhacSi ='" + MusicianArrayList.get(position).getId() + "'");
                        BaiHatArrayList.clear();
                        while (dataBaiHat.moveToNext()) {
                            String mabaihat = dataBaiHat.getString(0);
                            String tenbaihat = dataBaiHat.getString(1);
                            String namsangtac = dataBaiHat.getString(2);

                            BaiHatArrayList.add(new BaiHatModel(mabaihat, tenbaihat, namsangtac));
                        }

                        adapterbh.notifyDataSetChanged();


                        lv_baihatofnhacsi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                //  Toast.makeText(getContext(), "vao duoc funtion", Toast.LENGTH_SHORT).show();
                                ArrayList<BieuDienModel> bieuDienArrayList;
                                bieuDienArrayList = new ArrayList<>();
                                CustomAdapterBieuDien adapterbd = new CustomAdapterBieuDien(getContext(), R.layout.nhacsi_item_baihat_listbieudien, bieuDienArrayList);
                                lv_bieudienforbaihat.setAdapter(adapterbd);
                                ten_bh.setText(BaiHatArrayList.get(i).getTenBaiHat());
                                // Toast.makeText(getContext(), "vao duoc funtion =" + BaiHatArrayList.get(i).getMaBaiHat(), Toast.LENGTH_SHORT).show();
                                Cursor dataBieuDien = database.GetData("SELECT * FROM BieuDien where  MaBaiHat = '" + BaiHatArrayList.get(i).getMaBaiHat() + "'");
                                bieuDienArrayList.clear();
                                while (dataBieuDien.moveToNext()) {
                                    Integer id = Integer.parseInt(dataBieuDien.getString(0));

                                    String macasi = dataBieuDien.getString(2);
                                    String ngaybieudien = dataBieuDien.getString(3);
                                    String diadiem = dataBieuDien.getString(4);
                                    bieuDienArrayList.add(new BieuDienModel(id, macasi, ngaybieudien, diadiem));
                                }
                                adapterbd.notifyDataSetChanged();

                            }
                        });


                        btn_back_bhforns.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent nhacsi = new Intent(getContext(), MainActivity.class);
                                nhacsi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(nhacsi);
                            }
                        });

                        AlertDialog alertDialog = fix.create();
                        alertDialog.setCancelable(false);
                        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        alertDialog.show();


                    }
                });
                alert.show();

            }
        });

        lv_tablemusician.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final View update_layout = inflater.inflate(R.layout.nhacsi_fragment_update, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext()); // modal poppup
                alert.setMessage("Thực hiện thao tác");


                alert.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder fix = new AlertDialog.Builder(getContext());
                        fix.setView(update_layout);

                        EditText up_MaNS = (EditText) update_layout.findViewById(R.id.editma);
                        EditText up_tennhacsi = (EditText) update_layout.findViewById(R.id.editname);

                        Button btn_up_ns = (Button) update_layout.findViewById(R.id.btnUpdateNS);
                        Button btnBacKNS = (Button) update_layout.findViewById(R.id.btnBacKNS);
                        up_MaNS.setText(MusicianArrayList.get(position).getId());
                        up_tennhacsi.setText(MusicianArrayList.get(position).getName());


                        btnBacKNS.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent nhacsi = new Intent(getContext(), MainActivity.class);
                                nhacsi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(nhacsi);
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
                                Log.d("abc", "" + up_tennhacsi.getText());
                                database.QueryData("UPDATE NhacSi SET TenNhacSi  = '" + up_tennhacsi.getText().toString() + "' where MaNhacSi='" + MusicianArrayList.get(position).getId() + "'");
                                String name = up_tennhacsi.getText().toString();
                                MusicianArrayList.set(position, new NhacSiModel(MusicianArrayList.get(position).getId(), name));
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

                        System.out.println("xoa ne !! = " + MusicianArrayList.get(position).getId());
                        database.QueryData("DELETE FROM NhacSi WHERE MaNhacSi   = '" + MusicianArrayList.get(position).getId() + "'");
                        MusicianArrayList.remove(position);
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
                CustomAdapterNhacSi new_adapter;
                getList_NhacSi();
                if(newText.equals("") || newText.equals(null)){

                    lv_tablemusician.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    return false;
                }


                ArrayList<NhacSiModel> search_result = new ArrayList<NhacSiModel>();
                MusicianArrayList.forEach(item -> {
                    if (
                            item.getName().toLowerCase().contains(newText.toLowerCase()) ||
                                    item.getId().toLowerCase().contains(newText.toLowerCase())
                    )
                        search_result.add(item);
                });
                MusicianArrayList = search_result;
                new_adapter = new CustomAdapterNhacSi(getContext(), R.layout.nhacsi_item_list_view, search_result);
                lv_tablemusician.setAdapter(new_adapter);

                return false;
            }
        });
        return root;
    }

    private void init_db() {
        // database.QueryData("DROP TABLE NhacSi");
        database.QueryData("CREATE TABLE IF NOT EXISTS NhacSi(MaNhacSi VARCHAR(200) PRIMARY KEY NOT NULL, TenNhacSi VARCHAR(200) NOT NULL, HinhAnh BLOB)");
        database.QueryData("CREATE TABLE IF NOT EXISTS BaiHat(MaBaiHat VARCHAR(200) PRIMARY KEY NOT NULL, TenBaiHat VARCHAR(200) NOT NULL, NamSangTac VARCHAR(200) NOT NULL, MaNhacSi VARCHAR(200) NOT NULL, FOREIGN KEY (MaNhacSi) REFERENCES NhacSi(MaNhacSi))");
        // database.QueryData("DROP TABLE BieuDien");
        database.QueryData("CREATE TABLE IF NOT EXISTS BieuDien(MaBieuDien INTEGER PRIMARY KEY autoincrement  ,MaBaiHat VARCHAR(200) NOT NULL, MaCaSi VARCHAR(200) NOT NULL ,  NgayBieuDien VARCHAR(200) , DiaDiem VARCHAR(200) , FOREIGN KEY (MaBaiHat) REFERENCES BaiHat(MaBaiHat))");
        database.QueryData("CREATE TABLE IF NOT EXISTS CaSi(MaCaSi VARCHAR(200) PRIMARY KEY NOT NULL, TenCaSi VARCHAR(200) )"); //
    }

    private void getList_NhacSi() {
        Cursor data = database.GetData("SELECT * FROM NhacSi");
        MusicianArrayList.clear();
        while (data.moveToNext()) {
            String id = data.getString(0);
            String name = data.getString(1);

            MusicianArrayList.add(new NhacSiModel(id, name));
        }
    }


}