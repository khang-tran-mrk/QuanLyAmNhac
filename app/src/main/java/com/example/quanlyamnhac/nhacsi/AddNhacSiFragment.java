package com.example.quanlyamnhac.nhacsi;


import android.app.ActivityManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quanlyamnhac.Database;
import com.example.quanlyamnhac.MainActivity;
import com.example.quanlyamnhac.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AddNhacSiFragment extends Fragment {

    Button btnReturn, btnInsert;
    EditText insertname, insertma;
    Database database;
    ImageButton imageUpload, insertImage;
    int REQUEST_CODE_FOLDER =456;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.nhacsi_fragment_insert, container, false);

        btnReturn = root.findViewById(R.id.btnReturn);
        btnInsert = root.findViewById(R.id.btnInsert);
        insertname = root.findViewById(R.id.insertname);
        insertma = root.findViewById(R.id.insertma);
        imageUpload= root.findViewById(R.id.imageUpload);
        insertImage= root.findViewById(R.id.insertImage);
        database = new Database(getContext(), "QuanLyAmNhac.sqlite", null, 1);


        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nhacsi = new Intent(getContext(), MainActivity.class);
                nhacsi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nhacsi);
// back
            }
        });
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_FOLDER);
            }
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = insertname.getText().toString();
                String ma = insertma.getText().toString();

                // chuyen data image -> byte[]
                BitmapDrawable bitmapDrawable = (BitmapDrawable) insertImage.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArr);
                byte[] hinhanh= byteArr.toByteArray();

                Cursor data = database.GetData("SELECT * FROM NhacSi where MaNhacSi = '" + ma + "'");
                if(data.moveToNext()) {
                    Toast.makeText(getContext(), "Mã Nhạc Sĩ đã tồn tại", Toast.LENGTH_LONG).show();
                    return;
                }
                if (name.equals("")) {
                    Toast.makeText(getContext(), "Vui long nhap ten", Toast.LENGTH_LONG).show();
                }
                if (ma.equals("")) {
                    Toast.makeText(getContext(), "Vui long nhap ma nhac si", Toast.LENGTH_LONG).show();
                } else {

                        SQLiteDatabase db = database.getWritableDatabase();
                        String sql=  "INSERT INTO NhacSi VALUES(?,?,?)";
                        SQLiteStatement statement = db.compileStatement(sql);
                        statement.clearBindings();
                        statement.bindString(1,ma);
                        statement.bindString(2,name);
                        statement.bindBlob(3,hinhanh);
                        statement.executeInsert();
                    // database.QueryData("INSERT INTO Musician values ( null, '"+ name +" )");
                    //database.QueryData("INSERT INTO NhacSi VALUES('"+ma+"','" + name + "','" + hinhanh+"')");


                    Toast.makeText(getContext(), "Đã Thêm", Toast.LENGTH_LONG).show();
                    Intent nhacsi = new Intent(getContext(), MainActivity.class);
                    nhacsi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(nhacsi);
// back

                }

            }
        });


        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_FOLDER && resultCode == MainActivity.RESULT_OK && data!=null){
            Uri uri = data.getData();

            try  {
                InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                Bitmap  bitmap = BitmapFactory.decodeStream(inputStream);
                insertImage.setImageBitmap(bitmap);
                System.out.println("AAAAAAAAAAAAAAAAAAAAAA ");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
