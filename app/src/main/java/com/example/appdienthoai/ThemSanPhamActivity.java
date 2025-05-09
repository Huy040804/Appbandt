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

public class ThemSanPhamActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private EditText tenSanPham, giaSanPham, hangSanXuat, moTa, tonKho;
    private ImageView hinhSanPham;
    private String imagePath = "default_image";
    private QuanLySanPham quanLySanPham;
    private boolean isRequestingCameraPermission = false;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        imagePath = saveImageToInternalStorage(bitmap);
                        if (imagePath != null) {
                            Glide.with(this).load(imagePath).error(R.drawable.default_image).into(hinhSanPham);
                            Log.d("ThemSanPham", "Hình ảnh được chọn: " + imagePath);
                        } else {
                            imagePath = "default_image";
                            Toast.makeText(this, "Lỗi lưu hình ảnh, dùng hình mặc định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e("ThemSanPham", "Lỗi tải hình: " + e.getMessage());
                        imagePath = "default_image";
                        Toast.makeText(this, "Lỗi tải hình ảnh, dùng hình mặc định!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("ThemSanPham", "Chọn hình bị hủy");
                }
            });

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::onActivityResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_them_san_pham);
        } catch (Exception e) {
            Log.e("ThemSanPham", "Lỗi tải layout: " + e.getMessage());
            Toast.makeText(this, "Lỗi giao diện, vui lòng kiểm tra!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tenSanPham = findViewById(R.id.ten_san_pham);
        giaSanPham = findViewById(R.id.gia_san_pham);
        hangSanXuat = findViewById(R.id.hang_san_xuat);
        moTa = findViewById(R.id.mo_ta);
        tonKho = findViewById(R.id.ton_kho);
        hinhSanPham = findViewById(R.id.hinh_san_pham);
        MaterialButton nutChonHinh = findViewById(R.id.nut_chon_hinh);
        MaterialButton nutThem = findViewById(R.id.nut_them);
        MaterialButton nutQuayLai = findViewById(R.id.nut_quay_lai);

        quanLySanPham = new QuanLySanPham();
        quanLySanPham.taiSanPham(this);

        Glide.with(this).load(imagePath).error(R.drawable.default_image).into(hinhSanPham);

        nutChonHinh.setOnClickListener(v -> showImagePickerDialog());
        nutThem.setOnClickListener(v -> themSanPham());
        nutQuayLai.setOnClickListener(v -> finish());
    }

    private void showImagePickerDialog() {
        boolean hasCamera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        String[] options = hasCamera ? new String[]{"Chụp ảnh", "Chọn từ thư viện"} : new String[]{"Chọn từ thư viện"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn hình ảnh")
                .setItems(options, (dialog, which) -> {
                    if (hasCamera && which == 0) {
                        isRequestingCameraPermission = true;
                        checkCameraPermission();
                    } else {
                        isRequestingCameraPermission = false;
                        checkStoragePermission();
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this)
                        .setTitle("Cần quyền camera")
                        .setMessage("Ứng dụng cần quyền camera để chụp ảnh sản phẩm. Vui lòng cấp quyền trong cài đặt.")
                        .setPositiveButton("Cài đặt", (dialog, which) -> openAppSettings())
                        .setNegativeButton("Hủy", null)
                        .show();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
            }
        } else {
            openCamera();
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Cần quyền truy cập ảnh")
                            .setMessage("Ứng dụng cần quyền truy cập thư viện ảnh để chọn hình sản phẩm. Vui lòng cấp quyền trong cài đặt.")
                            .setPositiveButton("Cài đặt", (dialog, which) -> openAppSettings())
                            .setNegativeButton("Hủy", null)
                            .show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSIONS);
                }
            } else {
                openGallery();
            }
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            boolean needRequest = false;
            for (String perm : permissions) {
                if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                    needRequest = true;
                    break;
                }
            }
            if (needRequest) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Cần quyền truy cập bộ nhớ")
                            .setMessage("Ứng dụng cần quyền truy cập bộ nhớ để chọn và lưu hình sản phẩm. Vui lòng cấp quyền trong cài đặt.")
                            .setPositiveButton("Cài đặt", (dialog, which) -> openAppSettings())
                            .setNegativeButton("Hủy", null)
                            .show();
                } else {
                    requestPermissions(permissions, REQUEST_CODE_PERMISSIONS);
                }
            } else {
                openGallery();
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e("ThemSanPham", "Lỗi mở cài đặt: " + e.getMessage());
            Toast.makeText(this, "Không thể mở cài đặt!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                if (isRequestingCameraPermission) {
                    openCamera();
                } else {
                    openGallery();
                }
            } else {
                Toast.makeText(this, "Quyền bị từ chối, không thể chọn hình!", Toast.LENGTH_SHORT).show();
                Log.d("ThemSanPham", "Quyền bị từ chối: " + permissions[0]);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            pickImageLauncher.launch(intent);
        } catch (Exception e) {
            Log.e("ThemSanPham", "Lỗi mở thư viện ảnh: " + e.getMessage());
            Toast.makeText(this, "Không thể mở thư viện ảnh!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            takePictureLauncher.launch(intent);
        } catch (Exception e) {
            Log.e("ThemSanPham", "Lỗi mở camera: " + e.getMessage());
            Toast.makeText(this, "Không thể mở camera!", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        File directory = new File(getFilesDir(), "images");
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("ThemSanPham", "Không thể tạo thư mục images");
            return null;
        }
        String fileName = "IMG_" + UUID.randomUUID().toString() + ".jpg";
        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.d("ThemSanPham", "Hình ảnh lưu tại: " + file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("ThemSanPham", "Lỗi lưu hình: " + e.getMessage());
            return null;
        }
    }

    private void themSanPham() {
        String ten = tenSanPham.getText().toString().trim();
        String giaStr = giaSanPham.getText().toString().trim();
        String hang = hangSanXuat.getText().toString().trim();
        String moTaText = moTa.getText().toString().trim();
        String tonKhoStr = tonKho.getText().toString().trim();

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
            SanPham sanPham = new SanPham(ten, gia, imagePath, hang, moTaText, tonKhoVal);
            quanLySanPham.themSanPham(sanPham);
            quanLySanPham.luuSanPham(this);
            Log.d("ThemSanPham", "Thêm sản phẩm: " + ten + ", imagePath: " + imagePath);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("ten_san_pham", ten);
            resultIntent.putExtra("gia_san_pham", gia);
            resultIntent.putExtra("hinh_san_pham", imagePath);
            resultIntent.putExtra("hang_san_xuat", hang);
            resultIntent.putExtra("mo_ta", moTaText);
            resultIntent.putExtra("ton_kho", tonKhoVal);
            setResult(RESULT_OK, resultIntent);

            Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Log.e("ThemSanPham", "Lỗi định dạng: " + e.getMessage());
            Toast.makeText(this, "Giá hoặc tồn kho không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
            if (bitmap != null) {
                imagePath = saveImageToInternalStorage(bitmap);
                if (imagePath != null) {
                    Glide.with(this).load(imagePath).error(R.drawable.default_image).into(hinhSanPham);
                    Log.d("ThemSanPham", "Hình ảnh chụp: " + imagePath);
                } else {
                    imagePath = "default_image";
                    Toast.makeText(this, "Lỗi lưu hình ảnh, dùng hình mặc định!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("ThemSanPham", "Bitmap null từ camera");
                imagePath = "default_image";
                Toast.makeText(this, "Lỗi chụp ảnh, dùng hình mặc định!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("ThemSanPham", "Chụp ảnh bị hủy");
        }
    }
}
