package com.example.appdienthoai;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class QuanLySanPham {
    private List<SanPham> danhSachSanPham;
    private static final String PREF_NAME = "SanPhamPrefs";

    public QuanLySanPham() {
        danhSachSanPham = new ArrayList<>();
    }

    public void themSanPham(SanPham sanPham) {
        if (sanPham != null && sanPham.getTonKho() >= 0) {
            danhSachSanPham.add(sanPham);
        }
    }

    public void themSanPham(String ten, double gia, String imagePath, String hangSanXuat, String moTa, int tonKho) {
        if (ten != null && !ten.isEmpty() && gia >= 0 && tonKho >= 0) {
            themSanPham(new SanPham(ten, gia, imagePath != null ? imagePath : "default_image", hangSanXuat, moTa, tonKho));
        }
    }

    public void themSanPham(String ten, double gia) {
        themSanPham(ten, gia, "default_image", "", "", 10);
    }

    public void suaSanPham(int viTri, SanPham sanPhamMoi) {
        if (viTri >= 0 && viTri < danhSachSanPham.size() && sanPhamMoi != null && sanPhamMoi.getTonKho() >= 0) {
            danhSachSanPham.set(viTri, sanPhamMoi);
        }
    }

    public void xoaSanPham(int viTri) {
        if (viTri >= 0 && viTri < danhSachSanPham.size()) {
            danhSachSanPham.remove(viTri);
        }
    }

    public void taiSanPham(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int soLuongSanPham = prefs.getInt("so_luong_san_pham", 0);
        danhSachSanPham.clear();

        if (soLuongSanPham > 0) {
            for (int i = 0; i < soLuongSanPham; i++) {
                String ten = prefs.getString("ten_" + i, "");
                float gia = prefs.getFloat("gia_" + i, 0);
                String imagePath = prefs.getString("hinh_" + i, "default_image"); // Sửa: String thay vì int
                String hang = prefs.getString("hang_" + i, "");
                String moTa = prefs.getString("mo_ta_" + i, "");
                int tonKho = prefs.getInt("ton_kho_" + i, 0);
                if (!ten.isEmpty() && tonKho >= 0) {
                    danhSachSanPham.add(new SanPham(ten, gia, imagePath, hang, moTa, tonKho));
                }
            }
        }

        if (danhSachSanPham.isEmpty()) {
            danhSachSanPham.add(new SanPham("iPhone 13", 23990000, "iphone13", "Apple", "Chip A15, 128GB", 20));
            danhSachSanPham.add(new SanPham("Samsung Galaxy S23", 19990000, "samsung_s23", "Samsung", "Snapdragon 8 Gen 2, 256GB", 15));
            danhSachSanPham.add(new SanPham("Xiaomi 13 Pro", 17990000, "default_image", "Xiaomi", "Snapdragon 8 Gen 2, 256GB", 10));
            danhSachSanPham.add(new SanPham("Oppo Reno 8", 12990000, "default_image", "Oppo", "Dimensity 1300, 128GB", 25));
        }
        Log.d("QuanLySanPham", "Tải " + danhSachSanPham.size() + " sản phẩm");
    }

    public void luuSanPham(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("so_luong_san_pham", danhSachSanPham.size());
        for (int i = 0; i < danhSachSanPham.size(); i++) {
            SanPham sp = danhSachSanPham.get(i);
            editor.putString("ten_" + i, sp.layTen());
            editor.putFloat("gia_" + i, (float) sp.layGia());
            editor.putString("hinh_" + i, sp.layImagePath()); // Sửa: String thay vì int
            editor.putString("hang_" + i, sp.layHangSanXuat());
            editor.putString("mo_ta_" + i, sp.layMoTa());
            editor.putInt("ton_kho_" + i, sp.getTonKho());
        }
        editor.apply();
        Log.d("QuanLySanPham", "Lưu " + danhSachSanPham.size() + " sản phẩm");
    }

    public List<SanPham> layDanhSachSanPham() {
        return danhSachSanPham;
    }
}
