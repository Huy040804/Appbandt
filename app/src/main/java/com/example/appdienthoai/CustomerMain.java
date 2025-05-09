package com.example.appdienthoai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class CustomerMain extends AppCompatActivity {
    private QuanLyGiaoDien quanLyGiaoDien;
    private QuanLySanPham quanLySanPham;
    private QuanLyGioHang quanLyGioHang;
    private QuanLyDonHang quanLyDonHang; // Sửa từ QuanLyGiohang
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_ROLE = "role";
    private static final int REQUEST_CODE_GIO_HANG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (!isLoggedIn()) {
            navigateToLogin();
            return;
        }

        String vaiTro = sharedPreferences.getString(KEY_ROLE, "khachHang");
        quanLyGiaoDien = new QuanLyGiaoDien(this);
        quanLySanPham = new QuanLySanPham();
        quanLyGioHang = new QuanLyGioHang();
        quanLyDonHang = new QuanLyDonHang(); // Sửa từ QuanLyGiohang

        quanLySanPham.taiSanPham(this);
        quanLyGioHang.taiGioHang(this, quanLySanPham.layDanhSachSanPham());
        quanLyDonHang.taiDonHang(this, quanLySanPham.layDanhSachSanPham());

        // Kiểm tra null trước khi hiển thị danh sách sản phẩm
        if (quanLySanPham.layDanhSachSanPham() == null) {
            Log.e("CustomerMain", "Danh sách sản phẩm null");
            Toast.makeText(this, "Lỗi tải danh sách sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }
        quanLyGiaoDien.hienDanhSachSanPham(quanLySanPham.layDanhSachSanPham(), vaiTro);

        MaterialButton nutDangXuat = findViewById(R.id.nut_dang_xuat);
        nutDangXuat.setOnClickListener(v -> logout());

        MaterialButton nutGioHang = findViewById(R.id.nut_xem_gio_hang);
        nutGioHang.setOnClickListener(v -> {
            Intent intent = new Intent(this, Giohang.class);
            intent.putExtra("quan_ly_gio_hang", quanLyGioHang);
            startActivityForResult(intent, REQUEST_CODE_GIO_HANG);
        });

        MaterialButton nutDonHang = findViewById(R.id.nut_xem_don_hang);
        nutDonHang.setOnClickListener(v -> {
            Intent intent = new Intent(this, DonHangActivity.class);
            startActivity(intent);
        });
    }

    private boolean isLoggedIn() {
        String role = sharedPreferences.getString(KEY_ROLE, null);
        return role != null && role.equals("khachHang");
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, DangNhap.class);
        startActivity(intent);
        finish();
    }

    public void themVaoGioHang(SanPham sanPham) {
        if (sanPham == null) {
            Log.e("CustomerMain", "SanPham null khi thêm vào giỏ");
            Toast.makeText(this, "Lỗi sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }
        quanLyGioHang.themSanPham(sanPham, 1);
        quanLyGioHang.luuGioHang(this);
        Toast.makeText(this, "Đã thêm " + sanPham.layTen() + " vào giỏ!", Toast.LENGTH_SHORT).show();
    }

    public void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(this, DangNhap.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode == REQUEST_CODE_GIO_HANG && resultCode == RESULT_OK && data != null) {
            QuanLyGioHang updatedGioHang = (QuanLyGioHang) data.getSerializableExtra("quan_ly_gio_hang");
            if (updatedGioHang != null) {
                quanLyGioHang = updatedGioHang;
                quanLyGioHang.luuGioHang(this);
                quanLySanPham.luuSanPham(this);
                Log.d("CustomerMain", "Cập nhật giỏ hàng từ Giohang");
            } else {
                Log.e("CustomerMain", "QuanLyGioHang trả về null");
            }
        }
    }
}
