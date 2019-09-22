package com.fadecolor.esport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.Util.HttpUtil;
import com.fadecolor.esport.Util.StringUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    private EditText mEtTel, mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransStatusBar();
        setContentView(R.layout.activity_register);

        mEtTel = findViewById(R.id.et_account);
        mEtPassword = findViewById(R.id.et_password);
        Button register = findViewById(R.id.btn_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tel = mEtTel.getText().toString();
                String password = mEtPassword.getText().toString();
                if (tel.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
                } else if (!StringUtil.isMobileNo(tel)) {
                    Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    final SVProgressHUD progressHUD = new SVProgressHUD(RegisterActivity.this);
                    final SVProgressHUD progressHUD1 = new SVProgressHUD(RegisterActivity.this);
                    progressHUD.showWithStatus("正在注册...", SVProgressHUD.SVProgressHUDMaskType.Black);
                    String address = Constant.SEVER_ADDRESS+"/user/register?tel="+tel+"&password="+password;
                    HttpUtil.sendOkHttpRequest(address, new Callback() {
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressHUD.dismissImmediately();
                                            progressHUD1.showSuccessWithStatus("注册成功，请返回登录页面登录", SVProgressHUD.SVProgressHUDMaskType.Black);
                                        }
                                    });
                                } else if (statusCode == 200) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressHUD.dismissImmediately();
                                            progressHUD1.showErrorWithStatus("该用户已经注册，请直接使用密码登录", SVProgressHUD.SVProgressHUDMaskType.Black);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}
