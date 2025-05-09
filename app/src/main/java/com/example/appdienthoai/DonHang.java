package com.example.appdienthoai;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DonHang implements Serializable {
    private String maDonHang;
    private List<GioHangItem> danhSachSanPham;
    private double tongTien;
    private final String ngayDat; // Đặt là final để không thể thay đổi sau khi gán
    private String trangThai;

    public DonHang(String maDonHang, List<GioHangItem> danhSachSanPham, double tongTien, String ngayDat) {
        this.maDonHang = maDonHang;
        this.danhSachSanPham = new ArrayList<>(danhSachSanPham);
        this.tongTien = tongTien;
        this.ngayDat = ngayDat != null && !ngayDat.isEmpty()
                ? ngayDat
                : new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        this.trangThai = "Đã đặt";
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public List<GioHangItem> getDanhSachSanPham() {
        return danhSachSanPham;
    }

    public double getTongTien() {
        return tongTien;
    }

    public String getNgayDat() {
        return ngayDat;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}