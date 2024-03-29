package com.fadecolor.esport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.Util.HttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private Button mBtnLogin;

    private EditText mEtAccount, mEtPassword;

    private TextView mTvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestPermissions();

        mEtAccount = findViewById(R.id.et_account);
        mEtPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
        if (verifyLogin()) {
            SharedPreferences prefs = getSharedPreferences("account", MODE_PRIVATE);
            String userId = prefs.getString("userId","null");
            String userName = prefs.getString("userName", "昵称");
            String headPath = prefs.getString("headPath", "null");
            if (!userId.equals("null")) {
                loginSuccess(userId, userName, headPath);
            }
        }
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SVProgressHUD progressHUD = new SVProgressHUD(LoginActivity.this);
                final SVProgressHUD progressHUD1 = new SVProgressHUD(LoginActivity.this);
                progressHUD.showWithStatus("登录中", SVProgressHUD.SVProgressHUDMaskType.Black);
                String tel = mEtAccount.getText().toString();
                String password = mEtPassword.getText().toString();
                String address = Constant.SEVER_ADDRESS+"/user/login?tel="+tel+"&password="+password;
                HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressHUD.dismissImmediately();
                                progressHUD1.showErrorWithStatus("无法连接到服务器", SVProgressHUD.SVProgressHUDMaskType.Black);
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            int statusCode = jsonObject.getInt("code");
                            if (statusCode == 100) {
                                JSONObject extend = jsonObject.getJSONObject("extend");
                                JSONObject userJSON = extend.getJSONObject("user");
                                final String userId = userJSON.getString("tel");
                                final String userName = userJSON.getString("userName");
                                final String headPath = userJSON.getString("headPath");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressHUD.dismissImmediately();
                                        progressHUD1.showSuccessWithStatus("登录成功", SVProgressHUD.SVProgressHUDMaskType.Black);
                                        loginSuccess(userId, userName, headPath);
                                    }
                                });
                            } else if (statusCode == 200) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressHUD.dismissImmediately();
                                        progressHUD1.showErrorWithStatus("用户名或密码错误", SVProgressHUD.SVProgressHUDMaskType.Black);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        mTvRegister = findViewById(R.id.tv_register);
        mTvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginSuccess(String userId, String userName, String headPath) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        intent.putExtra("headPath",headPath); // 传递数据
        saveLoginInformation(userId, userName, headPath);
        startActivity(intent);
        finish();
    }

    private void saveLoginInformation(String userId, String userName, String headPath) {
        SharedPreferences.Editor editor = getSharedPreferences("account", MODE_PRIVATE).edit();
        editor.putString("account", mEtAccount.getText().toString());
        editor.putBoolean("status", true); // 表示已登录过
        editor.putString("userId", userId);
        editor.putString("userName", userName);
        editor.putString("headPath", headPath);
        editor.apply();
    }

    private boolean verifyLogin() {
        SharedPreferences prefs = getSharedPreferences("account", MODE_PRIVATE);
        return prefs.getBoolean("status", false); // 第二个参数为找不到该值时的默认返回值
    }

    public void requestPermissions() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LoginActivity.this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result :
                            grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(LoginActivity.this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

}
