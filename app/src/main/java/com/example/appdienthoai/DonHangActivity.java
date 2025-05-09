package com.example.appdienthoai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DonHangActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_STORAGE_PERMISSIONS = 101;
    private static final int REQUEST_CODE_WRITE_STORAGE_PERMISSIONS = 102;
    private QuanLyDonHang quanLyDonHang;
    private QuanLySanPham quanLySanPham;
    private DonHangAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_don_hang);
        } catch (Exception e) {
            Log.e("DonHangActivity", "Lỗi tải layout activity_don_hang: " + e.getMessage());
            Toast.makeText(this, "Lỗi giao diện, vui lòng kiểm tra!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo QuanLySanPham và QuanLyDonHang
        quanLySanPham = new QuanLySanPham();
        try {
            quanLySanPham.taiSanPham(this);
        } catch (Exception e) {
            Log.e("DonHangActivity", "Lỗi tải danh sách sản phẩm: " + e.getMessage());
            Toast.makeText(this, "Lỗi tải danh sách sản phẩm!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        quanLyDonHang = new QuanLyDonHang();
        try {
            quanLyDonHang.taiDonHang(this, quanLySanPham.layDanhSachSanPham());
        } catch (Exception e) {
            Log.e("DonHangActivity", "Lỗi tải danh sách đơn hàng: " + e.getMessage());
            Toast.makeText(this, "Lỗi tải danh sách đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.danh_sach_don_hang);
        if (recyclerView == null) {
            Log.e("DonHangActivity", "Không tìm thấy danh_sach_don_hang trong layout");
            Toast.makeText(this, "Lỗi giao diện danh sách đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<DonHang> danhSachDonHang = quanLyDonHang.layDanhSachDonHang();
        if (danhSachDonHang == null || danhSachDonHang.isEmpty()) {
            danhSachDonHang = new ArrayList<>();
            Log.w("DonHangActivity", "Danh sách đơn hàng rỗng hoặc null");
            Toast.makeText(this, "Không có đơn hàng nào!", Toast.LENGTH_SHORT).show();
        }
        adapter = new DonHangAdapter(danhSachDonHang);
        recyclerView.setAdapter(adapter);

        // Nút quay lại
        MaterialButton nutQuayLai = findViewById(R.id.nut_quay_lai);
        if (nutQuayLai != null) {
            nutQuayLai.setOnClickListener(v -> finish());
        } else {
            Log.e("DonHangActivity", "Không tìm thấy nut_quay_lai trong layout");
        }

        // Kiểm tra quyền bộ nhớ
        checkStoragePermission();
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_STORAGE_PERMISSIONS);
            }
        } else {
            List<String> permissions = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[0]), REQUEST_CODE_STORAGE_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSIONS || requestCode == REQUEST_CODE_WRITE_STORAGE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                Log.w("DonHangActivity", "Quyền bộ nhớ bị từ chối, có thể ảnh hưởng đến tải hình ảnh hoặc lưu trữ");
                Toast.makeText(this, "Quyền truy cập bộ nhớ bị từ chối!", Toast.LENGTH_SHORT).show();
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class DonHangAdapter extends RecyclerView.Adapter<DonHangViewHolder> {
        private List<DonHang> danhSachDonHang;

        public DonHangAdapter(List<DonHang> danhSachDonHang) {
            this.danhSachDonHang = danhSachDonHang != null ? danhSachDonHang : new ArrayList<>();
        }

        @Override
        public DonHangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                View view = LayoutInflater.from(DonHangActivity.this).inflate(R.layout.item_don_hang, parent, false);
                return new DonHangViewHolder(view);
            } catch (Exception e) {
                Log.e("DonHangAdapter", "Lỗi inflate item_don_hang: " + e.getMessage());
                return null;
            }
        }

        @Override
        public void onBindViewHolder(DonHangViewHolder holder, int position) {
            if (holder == null) {
                Log.e("DonHangAdapter", "ViewHolder null tại vị trí: " + position);
                return;
            }
            DonHang donHang = danhSachDonHang.get(position);
            if (donHang == null) {
                Log.e("DonHangAdapter", "Đơn hàng null tại vị trí: " + position);
                return;
            }

            try {
                // Hiển thị thông tin đơn hàng
                holder.maDonHang.setText("Mã đơn: " + (donHang.getMaDonHang() != null ? donHang.getMaDonHang().substring(0, Math.min(8, donHang.getMaDonHang().length())) : "N/A"));
                holder.ngayDat.setText("Ngày đặt: " + (donHang.getNgayDat() != null ? donHang.getNgayDat() : "N/A"));
                holder.tongTien.setText("Tổng tiền: " + formatVND(donHang.getTongTien()));
                holder.trangThai.setText("Trạng thái: " + (donHang.getTrangThai() != null ? donHang.getTrangThai() : "N/A"));
                Log.d("DonHangAdapter", "Trạng thái đơn hàng " + donHang.getMaDonHang() + ": " + donHang.getTrangThai());

                // Hiển thị danh sách sản phẩm
                List<GioHangItem> danhSachSanPham = donHang.getDanhSachSanPham();
                if (danhSachSanPham == null || danhSachSanPham.isEmpty()) {
                    danhSachSanPham = new ArrayList<>();
                    Log.w("DonHangAdapter", "Danh sách sản phẩm rỗng cho đơn hàng: " + donHang.getMaDonHang());
                }
                SanPhamDonHangAdapter sanPhamAdapter = new SanPhamDonHangAdapter(danhSachSanPham);
                holder.danhSachSanPham.setLayoutManager(new LinearLayoutManager(DonHangActivity.this));
                holder.danhSachSanPham.setAdapter(sanPhamAdapter);

                // Xử lý nút hủy đơn
                if (donHang.getTrangThai() != null && donHang.getTrangThai().equals("Đã đặt")) {
                    holder.nutHuyDon.setVisibility(View.VISIBLE);
                    holder.nutHuyDon.setEnabled(true);
                    holder.nutHuyDon.setOnClickListener(v -> {
                        Log.d("DonHangAdapter", "Nhấn hủy đơn tại vị trí: " + position + ", mã đơn: " + donHang.getMaDonHang());
                        xacNhanHuyDon(position, donHang.getMaDonHang());
                    });
                    Log.d("DonHangAdapter", "Nút hủy hiển thị cho đơn hàng: " + donHang.getMaDonHang());
                } else {
                    holder.nutHuyDon.setVisibility(View.GONE);
                    holder.nutHuyDon.setEnabled(false);
                    Log.d("DonHangAdapter", "Nút hủy ẩn, trạng thái: " + donHang.getTrangThai());
                }
            } catch (Exception e) {
                Log.e("DonHangAdapter", "Lỗi bind ViewHolder tại vị trí " + position + ": " + e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return danhSachDonHang.size();
        }
    }

    private class DonHangViewHolder extends RecyclerView.ViewHolder {
        TextView maDonHang, ngayDat, tongTien, trangThai;
        RecyclerView danhSachSanPham;
        MaterialButton nutHuyDon;

        public DonHangViewHolder(View itemView) {
            super(itemView);
            try {
                maDonHang = itemView.findViewById(R.id.ma_don_hang);
                ngayDat = itemView.findViewById(R.id.ngay_dat);
                tongTien = itemView.findViewById(R.id.tong_tien);
                trangThai = itemView.findViewById(R.id.trang_thai);
                danhSachSanPham = itemView.findViewById(R.id.danh_sach_san_pham);
                nutHuyDon = itemView.findViewById(R.id.nut_huy_don);
                if (nutHuyDon == null) {
                    Log.e("DonHangViewHolder", "Không tìm thấy nut_huy_don trong layout item_don_hang");
                }
            } catch (Exception e) {
                Log.e("DonHangViewHolder", "Lỗi khởi tạo ViewHolder: " + e.getMessage());
            }
        }
    }

    private class SanPhamDonHangAdapter extends RecyclerView.Adapter<SanPhamDonHangViewHolder> {
        private List<GioHangItem> danhSachSanPham;

        public SanPhamDonHangAdapter(List<GioHangItem> danhSachSanPham) {
            this.danhSachSanPham = danhSachSanPham != null ? danhSachSanPham : new ArrayList<>();
        }

        @Override
        public SanPhamDonHangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                View view = LayoutInflater.from(DonHangActivity.this).inflate(R.layout.item_san_pham_don_hang, parent, false);
                return new SanPhamDonHangViewHolder(view);
            } catch (Exception e) {
                Log.e("SanPhamDonHangAdapter", "Lỗi inflate item_san_pham_don_hang: " + e.getMessage());
                return null;
            }
        }

        @Override
        public void onBindViewHolder(SanPhamDonHangViewHolder holder, int position) {
            if (holder == null) {
                Log.e("SanPhamDonHangAdapter", "ViewHolder null tại vị trí: " + position);
                return;
            }
            GioHangItem item = danhSachSanPham.get(position);
            if (item == null || item.getSanPham() == null) {
                Log.e("SanPhamDonHangAdapter", "GioHangItem hoặc SanPham null tại vị trí: " + position);
                return;
            }

            try {
                SanPham sanPham = item.getSanPham();
                holder.tenSanPham.setText(sanPham.layTen() != null ? sanPham.layTen() : "N/A");
                holder.soLuong.setText("Số lượng: " + item.getSoLuong());
                holder.giaSanPham.setText(formatVND(sanPham.layGia() * item.getSoLuong()));
                Glide.with(DonHangActivity.this)
                        .load(sanPham.layImagePath() != null ? sanPham.layImagePath() : R.drawable.ic_placeholder)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(holder.hinhSanPham);
            } catch (Exception e) {
                Log.e("SanPhamDonHangAdapter", "Lỗi bind ViewHolder tại vị trí " + position + ": " + e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return danhSachSanPham.size();
        }
    }

    private class SanPhamDonHangViewHolder extends RecyclerView.ViewHolder {
        TextView tenSanPham, soLuong, giaSanPham;
        ImageView hinhSanPham;

        public SanPhamDonHangViewHolder(View itemView) {
            super(itemView);
            try {
                tenSanPham = itemView.findViewById(R.id.ten_san_pham);
                soLuong = itemView.findViewById(R.id.so_luong);
                giaSanPham = itemView.findViewById(R.id.gia_san_pham);
                hinhSanPham = itemView.findViewById(R.id.hinh_san_pham);
            } catch (Exception e) {
                Log.e("SanPhamDonHangViewHolder", "Lỗi khởi tạo ViewHolder: " + e.getMessage());
            }
        }
    }

    private void xacNhanHuyDon(int position, String maDonHang) {
        if (maDonHang == null) {
            Log.e("DonHangActivity", "Mã đơn hàng null tại vị trí: " + position);
            Toast.makeText(this, "Lỗi hủy đơn hàng!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (position < 0 || position >= quanLyDonHang.layDanhSachDonHang().size()) {
            Log.e("DonHangActivity", "Vị trí không hợp lệ: " + position);
            Toast.makeText(this, "Lỗi hủy đơn hàng!", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy đơn")
                .setMessage("Bạn có chắc muốn hủy đơn hàng " + maDonHang.substring(0, Math.min(8, maDonHang.length())) + "?")
                .setPositiveButton("Hủy đơn", (dialog, which) -> {
                    try {
                        // Hủy đơn hàng
                        quanLyDonHang.huyDonHang(this, position);
                        Log.d("DonHangActivity", "Đã hủy đơn hàng tại vị trí: " + position);

                        // Hoàn tồn kho
                        DonHang donHang = quanLyDonHang.layDanhSachDonHang().get(position);
                        if (donHang != null && donHang.getDanhSachSanPham() != null) {
                            for (GioHangItem item : donHang.getDanhSachSanPham()) {
                                if (item != null && item.getSanPham() != null) {
                                    SanPham sanPham = item.getSanPham();
                                    int newTonKho = sanPham.getTonKho() + item.getSoLuong();
                                    sanPham.setTonKho(newTonKho);
                                    Log.d("DonHangActivity", "Hoàn tồn kho: " + sanPham.layTen() + ", số lượng: " + item.getSoLuong());
                                }
                            }
                            quanLySanPham.luuSanPham(this);
                            Log.d("DonHangActivity", "Đã lưu danh sách sản phẩm sau khi hoàn tồn kho");
                        }

                        // Cập nhật giao diện
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Đã hủy đơn hàng!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("DonHangActivity", "Lỗi hủy đơn hàng: " + e.getMessage());
                        Toast.makeText(this, "Lỗi hủy đơn hàng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private String formatVND(double gia) {
        try {
            DecimalFormat formatter = new DecimalFormat("###,###,###");
            return formatter.format(gia) + " VNĐ";
        } catch (Exception e) {
            Log.e("DonHangActivity", "Lỗi định dạng tiền: " + e.getMessage());
            return gia + " VNĐ";
        }
    }
}