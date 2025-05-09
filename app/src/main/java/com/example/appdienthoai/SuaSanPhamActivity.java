package com.example.appdienthoai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SuaSanPhamActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private EditText nhapTenSanPham, nhapGiaSanPham, nhapHangSanXuat, nhapMoTa, nhapTonKho;
    private ImageView hinhSanPham;
    private String imagePath;
    private SanPham sanPham;
    private int viTri;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        imagePath = saveImageToInternalStorage(bitmap);
                        if (imagePath != null) {
                            Glide.with(this).load(imagePath).into(hinhSanPham);
                            Log.d("SuaSanPham", "Hình ảnh được chọn: " + imagePath);
                        } else {
                            imagePath = sanPham.layImagePath();
                            Toast.makeText(this, "Lỗi lưu hình ảnh, giữ hình hiện tại!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e("SuaSanPham", "Lỗi tải hình: " + e.getMessage());
                        imagePath = sanPham.layImagePath();
                        Toast.makeText(this, "Lỗi tải hình ảnh, giữ hình hiện tại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("SuaSanPham", "Chọn hình bị hủy");
                }
            });

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::onActivityResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_san_pham);

        nhapTenSanPham = findViewById(R.id.nhap_ten_san_pham);
        nhapGiaSanPham = findViewById(R.id.nhap_gia_san_pham);
        nhapHangSanXuat = findViewById(R.id.nhap_hang_san_xuat);
        nhapMoTa = findViewById(R.id.nhap_mo_ta);
        nhapTonKho = findViewById(R.id.nhap_ton_kho);
        hinhSanPham = findViewById(R.id.hinh_san_pham);
        MaterialButton nutChonHinh = findViewById(R.id.nutchon_hinh);
        MaterialButton nutLuu = findViewById(R.id.nutluu);
        MaterialButton nutHuy = findViewById(R.id.nuthuy);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        sanPham = (SanPham) intent.getSerializableExtra("san_pham");
        viTri = intent.getIntExtra("vi_tri", -1);

        if (sanPham == null || viTri < 0) {
            Toast.makeText(this, "Dữ liệu sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị thông tin sản phẩm hiện tại
        nhapTenSanPham.setText(sanPham.layTen());
        nhapGiaSanPham.setText(String.valueOf(sanPham.layGia()));
        nhapHangSanXuat.setText(sanPham.layHangSanXuat());
        nhapMoTa.setText(sanPham.layMoTa());
        nhapTonKho.setText(String.valueOf(sanPham.getTonKho()));
        imagePath = sanPham.layImagePath();
        Glide.with(this).load(imagePath).into(hinhSanPham);

        nutChonHinh.setOnClickListener(v -> showImagePickerDialog());
        nutLuu.setOnClickListener(v -> luuSanPham());
        nutHuy.setOnClickListener(v -> finish());
    }

    private void showImagePickerDialog() {
        String[] options = {"Chụp ảnh", "Chọn từ thư viện"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn hình ảnh")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermission();
                    } else {
                        checkStoragePermission();
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
        } else {
            openCamera();
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSIONS);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
            } else {
                openGallery();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permissions[0].equals(Manifest.permission.CAMERA)) {
                    openCamera();
                } else {
                    openGallery();
                }
            } else {
                Toast.makeText(this, "Quyền bị từ chối, không thể chọn hình!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(intent);
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        File directory = new File(getFilesDir(), "images");
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("SuaSanPham", "Không thể tạo thư mục images");
            return null;
        }
        String fileName = "IMG_" + UUID.randomUUID().toString() + ".jpg";
        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.d("SuaSanPham", "Hình ảnh lưu tại: " + file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("SuaSanPham", "Lỗi lưu hình: " + e.getMessage());
            return null;
        }
    }

    private void luuSanPham() {
        String ten = nhapTenSanPham.getText().toString().trim();
        String giaStr = nhapGiaSanPham.getText().toString().trim();
        String hang = nhapHangSanXuat.getText().toString().trim();
        String moTaText = nhapMoTa.getText().toString().trim();
        String tonKhoStr = nhapTonKho.getText().toString().trim();

        if (ten.isEmpty() || giaStr.isEmpty() || tonKhoStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên, giá và tồn kho!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double gia = Double.parseDouble(giaStr);
            int tonKhoVal = Integer.parseInt(tonKhoStr);
            if (gia <= 0 || tonKhoVal <= 0) {
                Toast.makeText(this, "Giá và tồn kho phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                return;
            }
            SanPham sanPhamMoi = new SanPham(ten, gia, imagePath, hang, moTaText, tonKhoVal);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("san_pham", sanPhamMoi);
            resultIntent.putExtra("vi_tri", viTri);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Log.e("SuaSanPham", "Lỗi định dạng: " + e.getMessage());
            Toast.makeText(this, "Giá hoặc tồn kho không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
            if (bitmap != null) {
                imagePath = saveImageToInternalStorage(bitmap);
                if (imagePath != null) {
                    Glide.with(this).load(imagePath).into(hinhSanPham);
                    Log.d("SuaSanPham", "Hình ảnh chụp: " + imagePath);
                } else {
                    imagePath = sanPham.layImagePath();
                    Toast.makeText(this, "Lỗi lưu hình ảnh, giữ hình hiện tại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("SuaSanPham", "Bitmap null từ camera");
                imagePath = sanPham.layImagePath();
                Toast.makeText(this, "Lỗi chụp ảnh, giữ hình hiện tại!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("SuaSanPham", "Chụp ảnh bị hủy");
        }
    }
}
