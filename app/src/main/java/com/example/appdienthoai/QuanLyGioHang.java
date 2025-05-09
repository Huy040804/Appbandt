package com.example.appdienthoai;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuanLyGioHang implements Serializable {
    private List<GioHangItem> danhSachGioHang;

    public QuanLyGioHang() {
        danhSachGioHang = new ArrayList<>();
    }

    public List<GioHangItem> layDanhSachGioHang() {
        return danhSachGioHang;
    }

    public void themSanPham(SanPham sanPham, int soLuong) {
        for (GioHangItem item : danhSachGioHang) {
            if (item.getSanPham().layTen().equals(sanPham.layTen())) {
                int soLuongMoi = item.getSoLuong() + soLuong;
                if (soLuongMoi <= sanPham.getTonKho() && soLuongMoi <= 10) {
                    item.setSoLuong(soLuongMoi);
                }
                return;
            }
        }
        danhSachGioHang.add(new GioHangItem(sanPham, soLuong));
    }

    public void capNhatSoLuong(int viTri, int soLuongMoi) {
        if (viTri >= 0 && viTri < danhSachGioHang.size()) {
            danhSachGioHang.get(viTri).setSoLuong(soLuongMoi);
        }
    }

    public void xoaSanPham(int viTri) {
        if (viTri >= 0 && viTri < danhSachGioHang.size()) {
            danhSachGioHang.remove(viTri);
        }
    }

    public void xoaTatCa() {
        danhSachGioHang.clear();
    }

    public double tinhTongTien() {
        double tong = 0;
        for (GioHangItem item : danhSachGioHang) {
            tong += item.getSanPham().layGia() * item.getSoLuong();
        }
        return tong;
    }

    public void luuGioHang(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("GioHangPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("so_san_pham", danhSachGioHang.size());
        for (int i = 0; i < danhSachGioHang.size(); i++) {
            GioHangItem item = danhSachGioHang.get(i);
            editor.putString("ten_sp_" + i, item.getSanPham().layTen());
            editor.putInt("so_luong_" + i, item.getSoLuong());
        }
        editor.apply();
        Log.d("QuanLyGioHang", "Lưu " + danhSachGioHang.size() + " sản phẩm vào giỏ");
    }

    public void taiGioHang(Context context, List<SanPham> danhSachSanPham) {
        SharedPreferences prefs = context.getSharedPreferences("GioHangPrefs", Context.MODE_PRIVATE);
        int soSanPham = prefs.getInt("so_san_pham", 0);
        danhSachGioHang.clear();
        for (int i = 0; i < soSanPham; i++) {
            String ten = prefs.getString("ten_sp_" + i, "");
            int soLuong = prefs.getInt("so_luong_" + i, 1);
            for (SanPham sp : danhSachSanPham) {
                if (sp.layTen().equals(ten)) {
                    danhSachGioHang.add(new GioHangItem(sp, soLuong));
                    break;
                }
            }
        }
        Log.d("QuanLyGioHang", "Tải " + danhSachGioHang.size() + " sản phẩm từ giỏ");
    }
}
