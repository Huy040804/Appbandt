package com.example.appdienthoai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdienthoai.R;
import com.google.android.material.button.MaterialButton;

public class DangNhap extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        if (isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        EditText nhapTenDangNhap = findViewById(R.id.nhap_ten_dang_nhap);
        EditText nhapMatKhau = findViewById(R.id.nhap_mat_khau);
        MaterialButton nutDangNhap = findViewById(R.id.nut_dang_nhap);
        TextView quenMatKhau = findViewById(R.id.quen_mat_khau);

        nhapTenDangNhap.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                nhapMatKhau.requestFocus();
                nhapMatKhau.setText("");
                return true;
            }
            return false;
        });

        nhapMatKhau.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                nutDangNhap.performClick();
                return true;
            }
            return false;
        });

        nutDangNhap.setOnClickListener(v -> {
            String tenDangNhap = nhapTenDangNhap.getText().toString().trim();
            String matKhau = nhapMatKhau.getText().toString().trim();

            if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (tenDangNhap.equals("admin") && matKhau.equals("1234")) {
                saveLoginState("admin");
                Toast.makeText(this, "Đăng nhập thành công với vai trò Admin!", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            } else if (tenDangNhap.equals("huy") && matKhau.equals("1234")) {
                saveLoginState("khachHang");
                Toast.makeText(this, "Đăng nhập thành công với vai trò Khách hàng!", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            } else {
                Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        quenMatKhau.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuenMatKhau.class);
            startActivity(intent);
        });
    }

    private boolean isLoggedIn() {
        String role = sharedPreferences.getString(KEY_ROLE, null);
        return role != null && (role.equals("admin") || role.equals("khachHang"));
    }

    private void saveLoginState(String role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    private void navigateToMainActivity() {
        String role = sharedPreferences.getString(KEY_ROLE, "");
        Intent intent;
        if ("admin".equals(role)) {
            intent = new Intent(this, AdminMain.class);
        } else {
            intent = new Intent(this, CustomerMain.class);
        }
        startActivity(intent);
        finish();
    }
}