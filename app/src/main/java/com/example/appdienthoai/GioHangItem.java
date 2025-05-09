package com.example.appdienthoai;

import java.io.Serializable;

public class GioHangItem implements Serializable {
    private SanPham sanPham;
    private int soLuong;

    public GioHangItem(SanPham sanPham, int soLuong) {
        this.sanPham = sanPham;
        this.soLuong = soLuong;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double tinhTongTien() {
        return sanPham.layGia() * soLuong;
    }
}