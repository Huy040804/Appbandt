package com.example.appdienthoai;

import java.io.Serializable;

public class SanPham implements Serializable {
    private String ten;
    private double gia;
    private String imagePath; // String, không phải int
    private String hangSanXuat;
    private String moTa;
    private int tonKho;

    public SanPham(String ten, double gia, String imagePath, String hangSanXuat, String moTa, int tonKho) {
        this.ten = ten;
        this.gia = gia;
        this.imagePath = imagePath;
        this.hangSanXuat = hangSanXuat;
        this.moTa = moTa;
        this.tonKho = tonKho;
    }

    public String layTen() {
        return ten;
    }

    public double layGia() {
        return gia;
    }

    public String layImagePath() {
        return imagePath;
    }

    public String layHangSanXuat() {
        return hangSanXuat != null ? hangSanXuat : "Không xác định";
    }

    public String layMoTa() {
        return moTa != null ? moTa : "";
    }

    public int getTonKho() {
        return tonKho;
    }

    public void setTonKho(int tonKho) {
        this.tonKho = tonKho;
    }
}