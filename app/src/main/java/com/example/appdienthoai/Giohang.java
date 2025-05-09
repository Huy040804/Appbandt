package com.example.appdienthoai;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.util.List;

public class Giohang extends AppCompatActivity {
    private QuanLyGioHang quanLyGioHang;
    private QuanLyDonHang quanLyDonHang; // Sửa từ QuanLyGiohang
    private QuanLySanPham quanLySanPham;
    private SanPhamAdapter adapter;
    private TextView tongTien;
    private TextView gioHangTrong; // Thêm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);

        quanLyGioHang = (QuanLyGioHang) getIntent().getSerializableExtra("quan_ly_gio_hang");
        quanLySanPham = new QuanLySanPham();
        quanLySanPham.taiSanPham(this);
        quanLyDonHang = new QuanLyDonHang(); // Sửa từ QuanLyGiohang
        quanLyDonHang.taiDonHang(this, quanLySanPham.layDanhSachSanPham());

        if (quanLyGioHang == null || quanLyGioHang.layDanhSachGioHang() == null) {
            Log.e("Giohang", "QuanLyGioHang null hoặc danh sách null");
            Toast.makeText(this, "Lỗi tải giỏ hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.danh_sach_gio_hang);
        gioHangTrong = findViewById(R.id.gio_hang_trong); // Thêm
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SanPhamAdapter(quanLyGioHang.layDanhSachGioHang());
        recyclerView.setAdapter(adapter);

        // Xử lý giỏ hàng trống
        if (quanLyGioHang.layDanhSachGioHang().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            gioHangTrong.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            gioHangTrong.setVisibility(View.GONE);
        }

        tongTien = findViewById(R.id.tong_tien);
        capNhatTongTien();

        MaterialButton nutQuayLai = findViewById(R.id.nut_quay_lai);
        nutQuayLai.setOnClickListener(v -> {
            quanLyGioHang.luuGioHang(this);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("quan_ly_gio_hang", quanLyGioHang);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        MaterialButton nutMuaHang = findViewById(R.id.nut_mua_hang);
        nutMuaHang.setOnClickListener(v -> {
            if (quanLyGioHang.layDanhSachGioHang().isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống, không thể mua!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (GioHangItem item : quanLyGioHang.layDanhSachGioHang()) {
                SanPham sanPham = item.getSanPham();
                if (sanPham == null) {
                    Toast.makeText(this, "Dữ liệu sản phẩm lỗi!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (item.getSoLuong() > sanPham.getTonKho()) {
                    Toast.makeText(this, "Sản phẩm " + sanPham.layTen() + " chỉ còn " + sanPham.getTonKho() + "!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (item.getSoLuong() > 10) {
                    Toast.makeText(this, "Sản phẩm " + sanPham.layTen() + " vượt quá giới hạn 10!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            // Lưu đơn hàng
            quanLyDonHang.themDonHang(this, quanLyGioHang.layDanhSachGioHang(), quanLyGioHang.tinhTongTien());
            // Cập nhật tồn kho
            for (GioHangItem item : quanLyGioHang.layDanhSachGioHang()) {
                SanPham sanPham = item.getSanPham();
                int tonKhoMoi = sanPham.getTonKho() - item.getSoLuong();
                sanPham.setTonKho(tonKhoMoi);
            }
            quanLySanPham.luuSanPham(this);
            Toast.makeText(this, "Đặt hàng thành công! Tổng tiền: " + formatVND(quanLyGioHang.tinhTongTien()), Toast.LENGTH_LONG).show();
            quanLyGioHang.xoaTatCa();
            quanLyGioHang.luuGioHang(this);
            adapter.notifyDataSetChanged();
            capNhatTongTien();
            recyclerView.setVisibility(View.GONE);
            gioHangTrong.setVisibility(View.VISIBLE); // Cập nhật giao diện sau khi mua
            Intent resultIntent = new Intent();
            resultIntent.putExtra("quan_ly_gio_hang", quanLyGioHang);
            setResult(RESULT_OK, resultIntent);
            // Không finish() để người dùng có thể quay lại
        });
    }

    private void capNhatTongTien() {
        double tong = quanLyGioHang != null ? quanLyGioHang.tinhTongTien() : 0;
        tongTien.setText("Tổng tiền: " + formatVND(tong));
    }

    private class SanPhamAdapter extends RecyclerView.Adapter<SanPhamViewHolder> {
        private List<GioHangItem> danhSachGioHang;

        public SanPhamAdapter(List<GioHangItem> danhSachGioHang) {
            this.danhSachGioHang = danhSachGioHang;
            Log.d("Giohang", "Khởi tạo adapter với " + danhSachGioHang.size() + " mục");
        }

        @Override
        public SanPhamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(Giohang.this).inflate(R.layout.item_san_pham, parent, false);
            return new SanPhamViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SanPhamViewHolder holder, int position) {
            GioHangItem item = danhSachGioHang.get(position);
            SanPham sanPham = item.getSanPham();
            if (sanPham == null) {
                Log.e("Giohang", "SanPham null tại vị trí " + position);
                Toast.makeText(Giohang.this, "Dữ liệu sản phẩm lỗi!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("Giohang", "Hiển thị sản phẩm: " + sanPham.layTen() + ", số lượng: " + item.getSoLuong());
            holder.tenSanPham.setText(sanPham.layTen());
            holder.giaSanPham.setText(formatVND(sanPham.layGia()));
            if (holder.hangSanXuat != null) {
                holder.hangSanXuat.setText("Hãng: " + sanPham.layHangSanXuat()); // Thêm "Hãng: "
            }
            // Tải hình ảnh
            Glide.with(Giohang.this)
                    .load(sanPham.layImagePath())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.hinhSanPham);

            holder.soLuongContainer.setVisibility(View.VISIBLE);
            holder.nutXemThongTin.setVisibility(View.GONE);
            holder.nutThemVaoGio.setVisibility(View.GONE);
            holder.nutSua.setVisibility(View.GONE);
            holder.nutXoa.setVisibility(View.GONE);
            holder.nutXoaTrongGio.setVisibility(View.VISIBLE);
            holder.soLuong.setText(String.valueOf(item.getSoLuong())); // Cập nhật số lượng

            holder.nutTang.setOnClickListener(v -> {
                int soLuongMoi = item.getSoLuong() + 1;
                if (soLuongMoi > sanPham.getTonKho()) {
                    Toast.makeText(Giohang.this, "Sản phẩm " + sanPham.layTen() + " chỉ còn " + sanPham.getTonKho() + "!", Toast.LENGTH_SHORT).show();
                } else if (soLuongMoi > 10) {
                    Toast.makeText(Giohang.this, "Số lượng tối đa là 10!", Toast.LENGTH_SHORT).show();
                } else {
                    quanLyGioHang.capNhatSoLuong(position, soLuongMoi);
                    holder.soLuong.setText(String.valueOf(soLuongMoi));
                    capNhatTongTien();
                    quanLyGioHang.luuGioHang(Giohang.this);
                }
            });

            holder.nutGiam.setOnClickListener(v -> {
                int soLuongMoi = item.getSoLuong() - 1;
                if (soLuongMoi <= 0) {
                    xacNhanXoa(position, sanPham.layTen());
                } else {
                    quanLyGioHang.capNhatSoLuong(position, soLuongMoi);
                    holder.soLuong.setText(String.valueOf(soLuongMoi));
                    capNhatTongTien();
                    quanLyGioHang.luuGioHang(Giohang.this);
                }
            });

            holder.nutXoaTrongGio.setOnClickListener(v -> xacNhanXoa(position, sanPham.layTen()));
        }

        @Override
        public int getItemCount() {
            int count = danhSachGioHang != null ? danhSachGioHang.size() : 0;
            Log.d("Giohang", "Số mục trong adapter: " + count);
            return count;
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
            hangSanXuat = itemView.findViewById(R.id.hang_san_xuat); // Sửa ID
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

    private void xacNhanXoa(int position, String tenSanPham) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa " + tenSanPham + " khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    quanLyGioHang.xoaSanPham(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, quanLyGioHang.layDanhSachGioHang().size());
                    capNhatTongTien();
                    quanLyGioHang.luuGioHang(this);
                    Toast.makeText(this, "Đã xóa " + tenSanPham, Toast.LENGTH_SHORT).show();
                    if (quanLyGioHang.layDanhSachGioHang().isEmpty()) {
                        RecyclerView recyclerView = findViewById(R.id.danh_sach_gio_hang);
                        recyclerView.setVisibility(View.GONE);
                        gioHangTrong.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String formatVND(double gia) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(gia) + " VNĐ";
    }

    @Override
    protected void onPause() {
        super.onPause();
        quanLyGioHang.luuGioHang(this);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("quan_ly_gio_hang", quanLyGioHang);
        setResult(RESULT_OK, resultIntent);
    }
}
