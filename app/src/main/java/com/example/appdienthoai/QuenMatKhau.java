package com.example.appdienthoai;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdienthoai.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuenMatKhau extends AppCompatActivity {
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quen_mat_khau); // Tạo layout mới cho Quên mật khẩu

        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your-api-url.com/") // Thay bằng URL API thật
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Liên kết với giao diện
        EditText nhapEmail = findViewById(R.id.nhap_email); // ID của EditText nhập email
        Button nutGuiYeuCau = findViewById(R.id.nut_gui_yeu_cau); // ID của nút gửi yêu cầu

        // Sự kiện nút Gửi yêu cầu
        nutGuiYeuCau.setOnClickListener(v -> {
            String email = nhapEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(QuenMatKhau.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }

            Call<ResetPasswordResponse> call = apiService.quenMatKhau(email);
            call.enqueue(new Callback<ResetPasswordResponse>() {
                @Override
                public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ResetPasswordResponse resetResponse = response.body();
                        if ("success".equals(resetResponse.getStatus())) {
                            Toast.makeText(QuenMatKhau.this, "Yêu cầu đặt lại mật khẩu đã được gửi!", Toast.LENGTH_SHORT).show();
                            finish(); // Quay lại màn hình trước (có thể là DangNhap)
                        } else {
                            Toast.makeText(QuenMatKhau.this, resetResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(QuenMatKhau.this, "Lỗi phản hồi từ server!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                    Toast.makeText(QuenMatKhau.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}