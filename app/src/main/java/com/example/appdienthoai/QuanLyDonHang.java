package com.example.appdienthoai;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuanLyDonHang implements Serializable {
    private List<DonHang> danhSachDonHang;

    public QuanLyDonHang() {
        danhSachDonHang = new ArrayList<>();
    }

    public void themDonHang(Context context, List<GioHangItem> danhSachSanPham, double tongTien) {
        String maDonHang = UUID.randomUUID().toString();
        DonHang donHang = new DonHang(maDonHang, danhSachSanPham, tongTien, null);
        danhSachDonHang.add(donHang);
        luuDonHang(context);
        Log.d("QuanLyDonHang", "Thêm đơn hàng: " + maDonHang);
    }

    public void huyDonHang(Context context, int viTri) {
        if (viTri >= 0 && viTri < danhSachDonHang.size()) {
            DonHang donHang = danhSachDonHang.get(viTri);
            donHang.setTrangThai("Đã hủy");
            luuDonHang(context); // Đảm bảo lưu sau khi thay đổi
            Log.d("QuanLyDonHang", "Hủy đơn hàng: " + donHang.getMaDonHang());
        } else {
            Log.e("QuanLyDonHang", "Vị trí không hợp lệ: " + viTri);
        }
    }

    public List<DonHang> layDanhSachDonHang() {
        return danhSachDonHang;
    }

    public void luuDonHang(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("DonHangPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("so_don_hang", danhSachDonHang.size());
        for (int i = 0; i < danhSachDonHang.size(); i++) {
            DonHang donHang = danhSachDonHang.get(i);
            editor.putString("ma_don_hang_" + i, donHang.getMaDonHang());
            editor.putFloat("tong_tien_" + i, (float) donHang.getTongTien());
            editor.putString("ngay_dat_" + i, donHang.getNgayDat());
            editor.putString("trang_thai_" + i, donHang.getTrangThai());
            editor.putInt("so_san_pham_" + i, donHang.getDanhSachSanPham().size());
            for (int j = 0; j < donHang.getDanhSachSanPham().size(); j++) {
                GioHangItem item = donHang.getDanhSachSanPham().get(j);
                editor.putString("ten_sp_" + i + "_" + j, item.getSanPham().layTen());
                editor.putFloat("gia_sp_" + i + "_" + j, (float) item.getSanPham().layGia());
                editor.putString("hinh_sp_" + i + "_" + j, item.getSanPham().layImagePath());
                editor.putString("hang_sp_" + i + "_" + j, item.getSanPham().layHangSanXuat());
                editor.putString("mo_ta_sp_" + i + "_" + j, item.getSanPham().layMoTa());
                editor.putInt("ton_kho_sp_" + i + "_" + j, item.getSanPham().getTonKho());
                editor.putInt("so_luong_sp_" + i + "_" + j, item.getSoLuong());
            }
        }
        editor.apply();
        Log.d("QuanLyDonHang", "Lưu " + danhSachDonHang.size() + " đơn hàng");
    }

    public void taiDonHang(Context context, List<SanPham> danhSachSanPham) {
        SharedPreferences prefs = context.getSharedPreferences("DonHangPrefs", Context.MODE_PRIVATE);
        int soDonHang = prefs.getInt("so_don_hang", 0);
        danhSachDonHang.clear();
        Log.d("QuanLyDonHang", "Tải " + soDonHang + " đơn hàng");
        for (int i = 0; i < soDonHang; i++) {
            String maDonHang = prefs.getString("ma_don_hang_" + i, "");
            float tongTien = prefs.getFloat("tong_tien_" + i, 0);
            String ngayDat = prefs.getString("ngay_dat_" + i, "");
            String trangThai = prefs.getString("trang_thai_" + i, "Đã đặt");
            int soSanPham = prefs.getInt("so_san_pham_" + i, 0);
            List<GioHangItem> danhSachSp = new ArrayList<>();
            for (int j = 0; j < soSanPham; j++) {
                String ten = prefs.getString("ten_sp_" + i + "_" + j, "");
                float gia = prefs.getFloat("gia_sp_" + i + "_" + j, 0);
                String hinh = prefs.getString("hinh_sp_" + i + "_" + j, "");
                String hang = prefs.getString("hang_sp_" + i + "_" + j, "");
                String moTa = prefs.getString("mo_ta_sp_" + i + "_" + j, "");
                int tonKho = prefs.getInt("ton_kho_sp_" + i + "_" + j, 0);
                int soLuong = prefs.getInt("so_luong_sp_" + i + "_" + j, 1);
                for (SanPham sp : danhSachSanPham) {
                    if (sp.layTen().equals(ten)) {
                        danhSachSp.add(new GioHangItem(sp, soLuong));
                        break;
                    }
                }
            }
            // Sử dụng constructor mới với ngayDat từ SharedPreferences
            DonHang donHang = new DonHang(maDonHang, danhSachSp, tongTien, ngayDat);
            donHang.setTrangThai(trangThai);
            danhSachDonHang.add(donHang);
        }
    }
}