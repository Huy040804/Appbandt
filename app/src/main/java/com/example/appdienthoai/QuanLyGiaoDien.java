package com.example.appdienthoai;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdienthoai.R;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.util.List;

public class QuanLyGiaoDien {
    private Context context;

    public QuanLyGiaoDien(Context context) {
        this.context = context;
    }

    public void hienDanhSachSanPham(List<SanPham> danhSachSanPham, String vaiTro) {
        RecyclerView recyclerView;
        if (context instanceof CustomerMain) {
            recyclerView = ((CustomerMain) context).findViewById(R.id.danh_sach_san_pham);
        } else if (context instanceof AdminMain) {
            recyclerView = ((AdminMain) context).findViewById(R.id.danh_sach_san_pham);
        } else {
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        SanPhamAdapter adapter = new SanPhamAdapter(danhSachSanPham, vaiTro);
        recyclerView.setAdapter(adapter);
    }

    private class SanPhamAdapter extends RecyclerView.Adapter<SanPhamViewHolder> {
        private List<SanPham> danhSachSanPham;
        private String vaiTro;

        public SanPhamAdapter(List<SanPham> danhSachSanPham, String vaiTro) {
            this.danhSachSanPham = danhSachSanPham;
            this.vaiTro = vaiTro;
        }

        @Override
        public SanPhamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_san_pham, parent, false);
            return new SanPhamViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SanPhamViewHolder holder, int position) {
            SanPham sanPham = danhSachSanPham.get(position);
            holder.tenSanPham.setText(sanPham.layTen());
            holder.giaSanPham.setText(formatVND(sanPham.layGia()));
            holder.hangSanXuat.setText(sanPham.layHangSanXuat());
            Glide.with(context).load(sanPham.layImagePath()).placeholder(R.drawable.ic_placeholder).into(holder.hinhSanPham);

            if (vaiTro.equals("khachHang")) {
                holder.nutXemThongTin.setVisibility(View.VISIBLE);
                holder.nutThemVaoGio.setVisibility(View.VISIBLE);
                holder.nutSua.setVisibility(View.GONE);
                holder.nutXoa.setVisibility(View.GONE);
                holder.nutXoaTrongGio.setVisibility(View.GONE);
                holder.soLuongContainer.setVisibility(View.GONE);

                holder.nutXemThongTin.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ChiTietSanPham.class);
                    intent.putExtra("san_pham", sanPham);
                    context.startActivity(intent);
                });

                holder.nutThemVaoGio.setOnClickListener(v -> {
                    if (context instanceof CustomerMain) {
                        ((CustomerMain) context).themVaoGioHang(sanPham);
                    }
                });
            } else if (vaiTro.equals("admin")) {
                holder.nutXemThongTin.setVisibility(View.GONE);
                holder.nutThemVaoGio.setVisibility(View.GONE);
                holder.nutSua.setVisibility(View.VISIBLE);
                holder.nutXoa.setVisibility(View.VISIBLE);
                holder.nutXoaTrongGio.setVisibility(View.GONE);
                holder.soLuongContainer.setVisibility(View.GONE);

                holder.nutSua.setOnClickListener(v -> {
                    if (context instanceof AdminMain) {
                        ((AdminMain) context).suaSanPham(position, sanPham);
                    }
                });

                holder.nutXoa.setOnClickListener(v -> {
                    if (context instanceof AdminMain) {
                        ((AdminMain) context).xoaSanPham(position, sanPham);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return danhSachSanPham.size();
        }
    }

    private class SanPhamViewHolder extends RecyclerView.ViewHolder {
        TextView tenSanPham, giaSanPham, hangSanXuat, soLuong;
        ImageView hinhSanPham;
        LinearLayout soLuongContainer;
        MaterialButton nutXemThongTin, nutThemVaoGio, nutSua, nutXoa, nutXoaTrongGio, nutTang, nutGiam;

        public SanPhamViewHolder(View itemView) {
            super(itemView);
            tenSanPham = itemView.findViewById(R.id.ten_san_pham);
            giaSanPham = itemView.findViewById(R.id.gia_san_pham);
            hangSanXuat = itemView.findViewById(R.id.hangsan_xuat);
            soLuong = itemView.findViewById(R.id.so_luong);
            hinhSanPham = itemView.findViewById(R.id.hinh_san_pham);
            soLuongContainer = itemView.findViewById(R.id.so_luong_container);
            nutXemThongTin = itemView.findViewById(R.id.nut_xem_thong_tin);
            nutThemVaoGio = itemView.findViewById(R.id.nut_them_vao_gio);
            nutSua = itemView.findViewById(R.id.nut_sua);
            nutXoa = itemView.findViewById(R.id.nut_xoa);
            nutXoaTrongGio = itemView.findViewById(R.id.nut_xoa_trong_gio);
            nutTang = itemView.findViewById(R.id.nut_tang);
            nutGiam = itemView.findViewById(R.id.nut_giam);
        }
    }

    private String formatVND(double gia) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(gia) + " VNĐ";
    }
}