package com.fadecolor.esport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.Util.HttpUtil;
import com.fadecolor.esport.domain.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PersonalDetailsActivity extends BaseActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        user = queryUser();
    }

    public User queryUser() {
        final User returnUser = new User();
        String address = Constant.SEVER_ADDRESS + "/user/query";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject user = new JSONObject(responseData).getJSONObject("extend").getJSONObject("user");
                    String userTel = user.getString("tel");
                    String userName = user.getString("userName");
                    String userSex = user.getString("sex");
                    String path = user.getString("headPath");
                    returnUser.setTel(userTel);
                    returnUser.setUsername(userName);
                    returnUser.setSex(userSex);
                    returnUser.setHeadPath(path);
                    Intent intent = new Intent();
                    intent.putExtra("headPath",path);
                    intent.putExtra("userName",userName);
                    PersonalDetailsActivity.this.setResult(1,intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return returnUser;
    }
}
