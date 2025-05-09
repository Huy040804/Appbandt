package com.example.appdienthoai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

public class AdminMain extends AppCompatActivity {
    private QuanLyGiaoDien quanLyGiaoDien;
    private QuanLySanPham quanLySanPham;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_ROLE = "role";
    private static final int REQUEST_CODE_THEM_SAN_PHAM = 1;
    private static final int REQUEST_CODE_SUA_SAN_PHAM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (!isLoggedIn()) {
            navigateToLogin();
            return;
        }

        quanLyGiaoDien = new QuanLyGiaoDien(this);
        quanLySanPham = new QuanLySanPham();
        quanLySanPham.taiSanPham(this);
        quanLyGiaoDien.hienDanhSachSanPham(quanLySanPham.layDanhSachSanPham(), "admin");

        MaterialButton nutThemSanPham = findViewById(R.id.nut_them_san_pham);
        nutThemSanPham.setOnClickListener(v -> {
            Intent intent = new Intent(this, ThemSanPhamActivity.class);
            startActivityForResult(intent, REQUEST_CODE_THEM_SAN_PHAM);
        });

        MaterialButton nutDangXuat = findViewById(R.id.nut_dang_xuat);
        nutDangXuat.setOnClickListener(v -> logout());
    }

    private boolean isLoggedIn() {
        String role = sharedPreferences.getString(KEY_ROLE, null);
        return role != null && role.equals("admin");
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, DangNhap.class);
        startActivity(intent);
        finish();
    }

    public void suaSanPham(int viTri, SanPham sanPham) {
        Intent intent = new Intent(this, SuaSanPhamActivity.class);
        intent.putExtra("san_pham", sanPham);
        intent.putExtra("vi_tri", viTri);
        startActivityForResult(intent, REQUEST_CODE_SUA_SAN_PHAM);
    }

    public void xoaSanPham(int viTri, SanPham sanPham) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa " + sanPham.layTen() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    quanLySanPham.xoaSanPham(viTri);
                    quanLySanPham.luuSanPham(this);
                    RecyclerView recyclerView = findViewById(R.id.danh_sach_san_pham);
                    recyclerView.getAdapter().notifyItemRemoved(viTri);
                    Toast.makeText(this, "Đã xóa " + sanPham.layTen(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_THEM_SAN_PHAM && resultCode == RESULT_OK && data != null) {
            String ten = data.getStringExtra("ten_san_pham");
            double gia = data.getDoubleExtra("gia_san_pham", 0);
            String imagePath = data.getStringExtra("hinh_san_pham");
            String hangSanXuat = data.getStringExtra("hang_san_xuat");
            String moTa = data.getStringExtra("mo_ta");
            int tonKho = data.getIntExtra("ton_kho", 0);
            if (ten != null && !ten.isEmpty() && gia >= 0 && tonKho >= 0) {
                quanLySanPham.themSanPham(ten, gia, imagePath != null ? imagePath : "default_image", hangSanXuat, moTa, tonKho);
                quanLySanPham.luuSanPham(this);
                quanLyGiaoDien.hienDanhSachSanPham(quanLySanPham.layDanhSachSanPham(), "admin");
                Toast.makeText(this, "Đã thêm sản phẩm: " + ten, Toast.LENGTH_SHORT).show();
                Log.d("AdminMain", "Thêm sản phẩm từ ThemSanPhamActivity: " + ten);
            }
        } else if (requestCode == REQUEST_CODE_SUA_SAN_PHAM && resultCode == RESULT_OK && data != null) {
            SanPham sanPhamMoi = (SanPham) data.getSerializableExtra("san_pham");
            int viTri = data.getIntExtra("vi_tri", -1);
            if (sanPhamMoi != null && viTri >= 0) {
                quanLySanPham.suaSanPham(viTri, sanPhamMoi);
                quanLySanPham.luuSanPham(this);
                quanLyGiaoDien.hienDanhSachSanPham(quanLySanPham.layDanhSachSanPham(), "admin");
                RecyclerView recyclerView = findViewById(R.id.danh_sach_san_pham);
                recyclerView.getAdapter().notifyItemChanged(viTri);
                Toast.makeText(this, "Đã cập nhật " + sanPhamMoi.layTen(), Toast.LENGTH_SHORT).show();
                Log.d("AdminMain", "Cập nhật sản phẩm tại vị trí: " + viTri);
            } else {
                Log.e("AdminMain", "Dữ liệu sửa sản phẩm không hợp lệ");
            }
        }
    }
}
