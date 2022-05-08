package com.example.quanlyamnhac.bieudien;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.quanlyamnhac.Database;
import com.example.quanlyamnhac.R;
import com.example.quanlyamnhac.adapter.CustomAdapterBieuDien;
import com.example.quanlyamnhac.adapter.CustomAdapterNhacSi;
import com.example.quanlyamnhac.model.BieuDienModel;
import com.example.quanlyamnhac.model.NhacSiModel;

import java.util.ArrayList;

public class BieuDienFragment extends Fragment {

    Database database;
    ListView lvTableBieuDien;
    String mabh,macs;
    Spinner sp_edt_macasi,sp_edt_mabh;
    ArrayList sp_macasi,sp_mabaihat;
    ArrayList<BieuDienModel> bieuDienArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.bieudien_fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        lvTableBieuDien = root.findViewById(R.id.lv_tableBieuDien);

        Dialog dialogSelect = new Dialog(this.getContext());
        dialogSelect.setContentView(R.layout.bieudien_dialog_select);

        Dialog dialogEdit = new Dialog(this.getContext());
        dialogEdit.setContentView(R.layout.bieudien_dialog_edit);

        Dialog dialogDelete = new Dialog(this.getContext());
        dialogDelete.setContentView(R.layout.bieudien_dialog_confirm_delete);

        Button btnEdit = dialogEdit.findViewById(R.id.btn_edit);
        Button btnEditBack = dialogEdit.findViewById(R.id.btn_edit_back);
        Button btnSelectEdit = dialogSelect.findViewById(R.id.btn_select_edit);
        Button btnSelectDelete = dialogSelect.findViewById(R.id.btn_select_delete);
        Button btnNo = dialogDelete.findViewById(R.id.btn_delete_no);
        Button btnYes = dialogDelete.findViewById(R.id.btn_delete_yes);

        database = new Database(getContext(), "QuanLyAmNhac.sqlite", null, 1);

        getDataBieuDien();


        lvTableBieuDien.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {
                dialogSelect.show();
                btnSelectEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogEdit.show();
                        EditText edtTencasi = dialogEdit.findViewById(R.id.edt_tencasi);
                        EditText edtMabieudien = dialogEdit.findViewById(R.id.edt_mabieudien);
                        EditText edtTenbaihat = dialogEdit.findViewById(R.id.edt_tenbh);
                        EditText edtNgaybd = dialogEdit.findViewById(R.id.edt_ngaybd);
                        EditText edtDiadiem = dialogEdit.findViewById(R.id.edt_diadiem);

                        sp_edt_macasi = (Spinner) dialogEdit.findViewById(R.id.sp_edt_macasi);
                        sp_edt_mabh = (Spinner) dialogEdit.findViewById(R.id.sp_edt_mabh);
                        khoitao();

                        ArrayAdapter adaptercs = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, sp_macasi);
                        sp_edt_macasi.setAdapter(adaptercs);
                        sp_edt_macasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                 macs = sp_edt_macasi.getSelectedItem().toString();
                                edtTencasi.setText(selectNameCS(macs));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        ArrayAdapter adapterbh = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, sp_mabaihat);
                        sp_edt_mabh.setAdapter(adapterbh);
                        sp_edt_mabh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                 mabh = sp_edt_mabh.getSelectedItem().toString();
                                edtTenbaihat.setText(selectNameBH(mabh));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        BieuDienModel db = bieuDienArrayList.get(i);

                        int ma = db.getMaBieuDien();
                        edtMabieudien.setText(Integer.toString(ma));
                        edtDiadiem.setText(db.getDiaDiem().toString());
                        edtNgaybd.setText(db.getNgayBieuDien().toString());

                        btnEditBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogEdit.dismiss();
                            }
                        });

                        btnEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                String diadiem = edtDiadiem.getText().toString();
                                String ngay = edtNgaybd.getText().toString();
                                if ( diadiem.equals("") || ngay.equals("")) {
                                    Toast.makeText(getContext(), "Vui long dien thong tin day du!", Toast.LENGTH_SHORT).show();
                                } else {
                                    database.QueryData("UPDATE BieuDien SET MaBaiHat = '" + mabh +"',MaCaSi ='"+ macs + "' ,NgayBieuDien = '" + ngay + "' ,DiaDiem = '" + diadiem + "' WHERE MaBieuDien = '" + ma + "'");
                                    getDataBieuDien();
                                    Toast.makeText(getContext(), "Cập nhập thành công!", Toast.LENGTH_SHORT).show();
                                }
                                dialogEdit.dismiss();
                                dialogSelect.dismiss();
                            }
                        });
                    }

                });

                btnSelectDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogDelete.show();
                        btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogDelete.dismiss();
                            }
                        });
                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BieuDienModel db = bieuDienArrayList.get(i);

                                System.out.println("v la = " + db.getMaCaSi());
                                database.QueryData("DELETE FROM BieuDien WHERE MaBieuDien = '" +db.getMaBieuDien() +"'");
                                bieuDienArrayList.remove(i);
                                getDataBieuDien();
                                Toast.makeText(getContext(), "Xoa thanh cong!", Toast.LENGTH_SHORT).show();
                                dialogDelete.dismiss();
                                dialogSelect.dismiss();
                            }
                        });
                    }
                });
                return true;
            }
        });

        //Search
        SearchView search_bar = root.findViewById(R.id.search_bar);
        CustomAdapterBieuDien adapter = new CustomAdapterBieuDien(getContext(), R.layout.bieudien_item_list_view, bieuDienArrayList);
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CustomAdapterNhacSi new_adapter;
                getDataBieuDien();
                if(newText.equals("") || newText.equals(null)){

                    lvTableBieuDien.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    return false;
                }


                ArrayList<BieuDienModel> search_result = new ArrayList<BieuDienModel>();
                bieuDienArrayList.forEach(item -> {
                    if (
                            Integer.valueOf(item.getMaBieuDien()).toString().toLowerCase().contains(newText.toLowerCase()) ||
                                    item.getNgayBieuDien().toLowerCase().contains(newText.toLowerCase()) ||
                                    item.getDiaDiem().toLowerCase().contains(newText.toLowerCase()) ||
                                    item.getMaBaiHat().toLowerCase().contains(newText.toLowerCase()) ||
                                    item.getMaCaSi().toLowerCase().contains(newText.toLowerCase())
                    )
                        search_result.add(item);
                });
                bieuDienArrayList = search_result;
                new_adapter = new CustomAdapterNhacSi(getContext(), R.layout.nhacsi_item_list_view, search_result);
                lvTableBieuDien.setAdapter(new_adapter);

                return false;
            }
        });



        return root;
    }

    public void getDataBieuDien() {
        bieuDienArrayList = new ArrayList<>();
        CustomAdapterBieuDien adapter = new CustomAdapterBieuDien(getContext(), R.layout.bieudien_item_list_view, bieuDienArrayList);
        lvTableBieuDien.setAdapter(adapter);
        Cursor dataBieuDien = database.GetData("SELECT * FROM BieuDien");
        bieuDienArrayList.clear();
        while (dataBieuDien.moveToNext()) {
            Integer id = Integer.parseInt(dataBieuDien.getString(0));
            String mabaihat = dataBieuDien.getString(1);
            String macasi = dataBieuDien.getString(2);
            String ngaybieudien = dataBieuDien.getString(3);
            String diadiem = dataBieuDien.getString(4);
            bieuDienArrayList.add(new BieuDienModel(id, mabaihat, macasi, ngaybieudien, diadiem));
        }
        adapter.notifyDataSetChanged();
    }

    public String selectNameBH(String id) {
        Cursor databh = database.GetData("SELECT * FROM BaiHat where MaBaiHat = '"+ id +"'");
        while (databh.moveToNext()) {
            Log.d("abcdef", databh.getString(1));
            return databh.getString(1);
        }
        return "";
    }

    public String selectNameCS(String id) {
        Cursor datacs = database.GetData("SELECT * FROM CaSi where MaCaSi = '"+ id +"'");
        while (datacs.moveToNext()) {
            Log.d("abcdef", datacs.getString(1));
            return datacs.getString(1);
        }
        return "";
    }

    private void khoitao() {
        sp_macasi = new ArrayList();
        sp_mabaihat = new ArrayList();
        database = new Database(getContext(), "QuanLyAmNhac.sqlite", null, 1);
        Cursor dataBieuDiencs = database.GetData("SELECT * FROM CaSi");
        sp_macasi.clear();

        while (dataBieuDiencs.moveToNext()) {

            String macasi = dataBieuDiencs.getString(0);
            sp_macasi.add(macasi);

        }
        Cursor dataBieuDienbh = database.GetData("SELECT * FROM BaiHat");
        sp_mabaihat.clear();
        while (dataBieuDienbh.moveToNext()) {
            String mabh = dataBieuDienbh.getString(0);

            sp_mabaihat.add(mabh);
        }

    }
}