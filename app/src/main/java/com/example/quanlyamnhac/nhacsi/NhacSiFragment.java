package com.example.quanlyamnhac.nhacsi;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class NhacSiFragment extends Fragment {

    ArrayList<NhacSiModel> MusicianArrayList;
    Database database;
    ImageView editImage;
    int REQUEST_CODE_FOLDER = 456;

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

                        ArrayList<BaiHatModel> BaiHatArrayList;
                        BaiHatArrayList = new ArrayList<>();

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
                        editImage = (ImageView) update_layout.findViewById(R.id.editImage);
                        Button btn_up_ns = (Button) update_layout.findViewById(R.id.btnUpdateNS);
                        Button btnBacKNS = (Button) update_layout.findViewById(R.id.btnBacKNS);
                        ImageButton imageButton = update_layout.findViewById(R.id.imageBtn);

                        up_MaNS.setText(MusicianArrayList.get(position).getId());
                        up_tennhacsi.setText(MusicianArrayList.get(position).getName());


                        byte[] hinhAnh = MusicianArrayList.get(position).getHinhAnh();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnh, 0, hinhAnh.length);
                        editImage.setImageBitmap(bitmap);


                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, REQUEST_CODE_FOLDER);
                            }
                        });

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

                                // chuyen data image -> byte[]
                                BitmapDrawable bitmapDrawable = (BitmapDrawable) editImage.getDrawable();
                                Bitmap bitmap = bitmapDrawable.getBitmap();
                                ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArr);
                                byte[] hinhanh = byteArr.toByteArray();

                                System.out.println("hinhanh[] chinh sua " + hinhanh);
                                System.out.println("hinhanh to string chinh sua " + hinhanh.toString());
                                SQLiteDatabase db = database.getWritableDatabase();
                                //db.execSQL(" UPDATE NhacSi SET TenNhacSi=?,HinhAnh =? WHERE  MaNhacSi = ?",new String[]{up_tennhacsi.getText().toString(), hinhanh.toString(), MusicianArrayList.get(position).getId()});
                                String sql = " UPDATE NhacSi SET TenNhacSi=?,HinhAnh =? WHERE  MaNhacSi = ?";
                                SQLiteStatement statement = db.compileStatement(sql);
                                statement.clearBindings();
                                statement.bindString(1, up_tennhacsi.getText().toString());
                                statement.bindBlob(2, hinhanh);
                                statement.bindString(3, MusicianArrayList.get(position).getId());
                                statement.executeUpdateDelete();

                                //  database.QueryData("UPDATE NhacSi SET TenNhacSi  = '" + up_tennhacsi.getText().toString() + "',HinhAnh='"+hinhanh + "' where MaNhacSi='" + MusicianArrayList.get(position).getId() + "'");
                                String name = up_tennhacsi.getText().toString();
                                MusicianArrayList.set(position, new NhacSiModel(MusicianArrayList.get(position).getId(), name, hinhanh));
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

                        Cursor data2 = database.GetData("SELECT * FROM BaiHat where MaNhacSi = '" + MusicianArrayList.get(position).getId() + "'");
                        if(data2.moveToNext()) {
                            Toast.makeText(getContext(), "Lỗi khoá ngoại! Không thể xoá!!!", Toast.LENGTH_LONG).show();
                            return;
                        }

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
                if (newText.equals("") || newText.equals(null)) {

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

    //quanlyamnhac1
    private void getList_NhacSi() {
        Cursor data = database.GetData("SELECT * FROM NhacSi");
        MusicianArrayList.clear();
        MusicianArrayList.clear();
        while (data.moveToNext()) {
            String id = data.getString(0);
            String name = data.getString(1);
            byte[] hinh = data.getBlob(2);
            System.out.println("hinh la " + hinh);
            MusicianArrayList.add(new NhacSiModel(id, name, hinh));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_FOLDER && resultCode == MainActivity.RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                editImage.setImageBitmap(bitmap);
                System.out.println("BBBBBBBBBBBBBBBB ");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}