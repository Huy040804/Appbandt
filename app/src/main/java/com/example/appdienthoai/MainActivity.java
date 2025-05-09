package com.example.appdienthoai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
    private QuanLyGiaoDien quanLyGiaoDien;
    private QuanLySanPham quanLySanPham;
    private QuanLyGioHang quanLyGioHang;
    private QuanLyDonHang quanLyDonHang; // Thêm
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_ROLE = "role";
    private static final int REQUEST_CODE_GIO_HANG = 2;
    private static final int REQUEST_CODE_SUA_SAN_PHAM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (!isLoggedIn()) {
            navigateToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        String vaiTro = sharedPreferences.getString(KEY_ROLE, "");
        MaterialCardView themSanPhamContainer = findViewById(R.id.them_san_pham_container);
        MaterialButton nutXemGioHang = findViewById(R.id.nutxem_gio_hang);
        MaterialButton nutDangXuat = findViewById(R.id.nut_dang_xuat); // Thêm

        // Hiển thị/ẩn phần thêm sản phẩm và nút giỏ hàng dựa trên vai trò
        if ("admin".equals(vaiTro)) {
            themSanPhamContainer.setVisibility(View.VISIBLE);
            nutXemGioHang.setVisibility(View.GONE);
        } else {
            themSanPhamContainer.setVisibility(View.GONE);
            nutXemGioHang.setVisibility(View.VISIBLE);
        }

        quanLyGiaoDien = new QuanLyGiaoDien(this);
        quanLySanPham = new QuanLySanPham();
        quanLyGioHang = new QuanLyGioHang();
        quanLyDonHang = new QuanLyDonHang(); // Thêm

        khoiDongApp(vaiTro);

        // Xử lý thêm sản phẩm cho admin
        if ("admin".equals(vaiTro)) {
            MaterialButton nutThem = findViewById(R.id.nut_them_san_pham);
            EditText nhapTen = findViewById(R.id.nhap_ten_san_pham);
            EditText nhapGia = findViewById(R.id.nhap_gia_san_pham);
            EditText nhapHangSanXuat = findViewById(R.id.nhap_hang_san_xuat);
            EditText nhapMoTa = findViewById(R.id.nhap_mo_ta);
            EditText nhapTonKho = findViewById(R.id.nhap_ton_kho);

            nutThem.setOnClickListener(v -> {
                String ten = nhapTen.getText().toString().trim();
                String giaStr = nhapGia.getText().toString().trim();
                String hangSanXuat = nhapHangSanXuat.getText().toString().trim();
                String moTa = nhapMoTa.getText().toString().trim();
                String tonKhoStr = nhapTonKho.getText().toString().trim();

                if (!ten.isEmpty() && !giaStr.isEmpty() && !tonKhoStr.isEmpty()) {
                    try {
                        double gia = Double.parseDouble(giaStr);
                        int tonKho = Integer.parseInt(tonKhoStr);
                        if (gia < 0 || tonKho < 0) {
                            Toast.makeText(this, "Giá và tồn kho phải không âm!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Sửa: dùng String cho imagePath
                        quanLySanPham.themSanPham(ten, gia, "default_image", hangSanXuat, moTa, tonKho);
                        quanLySanPham.luuSanPham(this);
                        quanLyGiaoDien.hienDanhSachSanPham(quanLySanPham.layDanhSachSanPham(), vaiTro);
                        nhapTen.setText("");
                        nhapGia.setText("");
                        nhapHangSanXuat.setText("");
                        nhapMoTa.setText("");
                        nhapTonKho.setText("");
                        Toast.makeText(this, "Đã thêm sản phẩm: " + ten, Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Giá hoặc tồn kho không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Vui lòng nhập tên, giá và tồn kho!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Xử lý nút xem giỏ hàng
        nutXemGioHang.setOnClickListener(v -> {
            Intent intent = new Intent(this, Giohang.class);
            intent.putExtra("quan_ly_gio_hang", quanLyGioHang);
            startActivityForResult(intent, REQUEST_CODE_GIO_HANG);
        });

        // Xử lý nút đăng xuất
        nutDangXuat.setOnClickListener(v -> logout());
    }

    private void khoiDongApp(String vaiTro) {
        quanLySanPham.taiSanPham(this);
        quanLyGioHang.taiGioHang(this, quanLySanPham.layDanhSachSanPham());
        quanLyDonHang.taiDonHang(this, quanLySanPham.layDanhSachSanPham()); // Thêm
        if (quanLySanPham.layDanhSachSanPham() == null) {
            Log.e("MainActivity", "Danh sách sản phẩm null");
            Toast.makeText(this, "Lỗi tải danh sách sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }
        quanLyGiaoDien.hienDanhSachSanPham(quanLySanPham.layDanhSachSanPham(), vaiTro);
    }

    public void themVaoGioHang(SanPham sanPham) {
        if (sanPham == null) {
            Log.e("MainActivity", "SanPham null khi thêm vào giỏ");
            Toast.makeText(this, "Lỗi sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }
        quanLyGioHang.themSanPham(sanPham, 1);
        quanLyGioHang.luuGioHang(this);
        Toast.makeText(this, sanPham.layTen() + " đã được thêm vào giỏ", Toast.LENGTH_SHORT).show();
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getString(KEY_ROLE, null) != null;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, DangNhap.class);
        startActivity(intent);
        finish();
    }

    public void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    navigateToLogin();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SUA_SAN_PHAM && resultCode == RESULT_OK && data != null) {
            String ten = data.getStringExtra("ten_san_pham");
            double gia = data.getDoubleExtra("gia_san_pham", 0);
            String imagePath = data.getStringExtra("hinh_san_pham"); // Sửa: String thay vì int
            String hangSanXuat = data.getStringExtra("hang_san_xuat");
            String moTa = data.getStringExtra("mo_ta");
            int tonKho = data.getIntExtra("ton_kho", 0);
            int position = data.getIntExtra("position", -1);
            String vaiTro = sharedPreferences.getString(KEY_ROLE, "");
            if (position != -1 && ten != null && !ten.isEmpty() && gia >= 0 && tonKho >= 0) {
                // Sửa: dùng String cho imagePath
                quanLySanPham.suaSanPham(position, new SanPham(ten, gia, imagePath != null ? imagePath : "default_image", hangSanXuat, moTa, tonKho));
                quanLySanPham.luuSanPham(this);
                quanLyGiaoDien.hienDanhSachSanPham(quanLySanPham.layDanhSachSanPham(), vaiTro);
                Toast.makeText(this, "Đã cập nhật " + ten, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_GIO_HANG && resultCode == RESULT_OK && data != null) {
            QuanLyGioHang updatedGioHang = (QuanLyGioHang) data.getSerializableExtra("quan_ly_gio_hang");
            if (updatedGioHang != null) {
                quanLyGioHang = updatedGioHang;
                quanLyGioHang.luuGioHang(this);
                quanLySanPham.luuSanPham(this);
                String vaiTro = sharedPreferences.getString(KEY_ROLE, "");
                quanLyGiaoDien.hienDanhSachSanPham(quanLySanPham.layDanhSachSanPham(), vaiTro);
                Log.d("MainActivity", "Cập nhật giỏ hàng từ Giohang");
            } else {
                Log.e("MainActivity", "QuanLyGioHang trả về null");
            }
        }
    }
}
