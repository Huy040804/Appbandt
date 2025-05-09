package com.example.appdienthoai;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

// Interface định nghĩa các API endpoint
public interface ApiService {
    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> dangNhap(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("reset-password")
    Call<ResetPasswordResponse> quenMatKhau(
            @Field("email") String email
    );
}

// Model cho phản hồi đăng nhập
class LoginResponse {
    private String status;
    private String message;
    private String role; // Vai trò: admin hoặc user

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

// Model cho phản hồi quên mật khẩu
class ResetPasswordResponse {
    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}