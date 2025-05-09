package com.example.appdienthoai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdienthoai.R;
import com.google.android.material.button.MaterialButton;

public class SuaSanPham extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_san_pham);

        EditText nhapTen = findViewById(R.id.nhap_ten_san_pham);
        EditText nhapGia = findViewById(R.id.nhap_gia_san_pham);
        EditText nhapHangSanXuat = findViewById(R.id.nhap_hang_san_xuat);
        EditText nhapMoTa = findViewById(R.id.nhap_mo_ta);
        EditText nhapTonKho = findViewById(R.id.nhap_ton_kho);
        MaterialButton nutLuu = findViewById(R.id.nutluu);
        MaterialButton nutHuy = findViewById(R.id.nuthuy);

        Intent intent = getIntent();
        String ten = intent.getStringExtra("ten_san_pham");
        double gia = intent.getDoubleExtra("gia_san_pham", 0);
        String hangSanXuat = intent.getStringExtra("hang_san_xuat");
        String moTa = intent.getStringExtra("mo_ta");
        int tonKho = intent.getIntExtra("ton_kho", 0);
        int hinh = intent.getIntExtra("hinh_san_pham", R.drawable.default_image);
        int position = intent.getIntExtra("position", -1);

        nhapTen.setText(ten);
        nhapGia.setText(String.valueOf(gia));
        nhapHangSanXuat.setText(hangSanXuat);
        nhapMoTa.setText(moTa);
        nhapTonKho.setText(String.valueOf(tonKho));

        nutLuu.setOnClickListener(v -> {
            String newTen = nhapTen.getText().toString().trim();
            String giaStr = nhapGia.getText().toString().trim();
            String newHangSanXuat = nhapHangSanXuat.getText().toString().trim();
            String newMoTa = nhapMoTa.getText().toString().trim();
            String tonKhoStr = nhapTonKho.getText().toString().trim();

            if (!newTen.isEmpty() && !giaStr.isEmpty() && !tonKhoStr.isEmpty()) {
                try {
                    double newGia = Double.parseDouble(giaStr);
                    int newTonKho = Integer.parseInt(tonKhoStr);
                    if (newGia < 0 || newTonKho < 0) {
                        Toast.makeText(this, "Giá và tồn kho phải không âm!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent result = new Intent();
                    result.putExtra("ten_san_pham", newTen);
                    result.putExtra("gia_san_pham", newGia);
                    result.putExtra("hinh_san_pham", hinh);
                    result.putExtra("hang_san_xuat", newHangSanXuat);
                    result.putExtra("mo_ta", newMoTa);
                    result.putExtra("ton_kho", newTonKho);
                    result.putExtra("position", position);
                    setResult(RESULT_OK, result);
                    finish();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Giá hoặc tồn kho không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập tên, giá và tồn kho!", Toast.LENGTH_SHORT).show();
            }
        });

        nutHuy.setOnClickListener(v -> finish());
    }
}