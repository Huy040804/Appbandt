package com.example.appdienthoai;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.appdienthoai.R;
import com.google.android.material.button.MaterialButton;

public class ChiTietSanPham extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_san_pham);

        SanPham sanPham = (SanPham) getIntent().getSerializableExtra("san_pham");
        if (sanPham == null) {
            Log.e("ChiTietSanPham", "SanPham null trong Intent");
            Toast.makeText(this, "Lỗi tải thông tin sản phẩm!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tenSanPham = findViewById(R.id.chi_tiet_ten_san_pham);
        TextView giaSanPham = findViewById(R.id.chi_tiet_gia_san_pham);
        TextView hangSanXuat = findViewById(R.id.chi_tiet_hang_san_xuat);
        TextView moTa = findViewById(R.id.chi_tiet_mo_ta);
        ImageView hinhSanPham = findViewById(R.id.chi_tiet_hinh_san_pham);
        MaterialButton nutQuayLai = findViewById(R.id.nut_quay_lai);

        try {
            tenSanPham.setText(sanPham.layTen() != null ? sanPham.layTen() : "Không có tên");
            giaSanPham.setText(String.format("%,.0f VNĐ", sanPham.layGia()));
            hangSanXuat.setText("Hãng: " + (sanPham.layHangSanXuat() != null ? sanPham.layHangSanXuat() : "Không rõ"));
            moTa.setText("Mô tả: " + (sanPham.layMoTa() != null ? sanPham.layMoTa() : "Không có mô tả"));
            Glide.with(this).load(sanPham.layImagePath()).placeholder(R.drawable.ic_placeholder).into(hinhSanPham);
        } catch (Exception e) {
            Log.e("ChiTietSanPham", "Lỗi hiển thị sản phẩm: " + e.getMessage());
            Toast.makeText(this, "Lỗi hiển thị sản phẩm!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        nutQuayLai.setOnClickListener(v -> finish());
    }
}