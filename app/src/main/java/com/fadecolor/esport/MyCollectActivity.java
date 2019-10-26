package com.fadecolor.esport;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fadecolor.esport.Adapter.GymAdapter;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.Util.HttpUtil;
import com.fadecolor.esport.domain.Gym;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MyCollectActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private List<Gym> gyms;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        imageView = findViewById(R.id.iv_back);
        recyclerView = findViewById(R.id.Gym_recyclerview);
        imageView.setOnClickListener(this);
        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        LinearLayout linearLayout = findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(), statusBarHeight, linearLayout.getPaddingEnd(), linearLayout.getPaddingBottom());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final int type = getIntent().getIntExtra("type", 0);
        SharedPreferences prefs = getSharedPreferences("account", MODE_PRIVATE);
        HashSet<String> collect1 = (HashSet<String>) prefs.getStringSet("collect_Gym_id", new HashSet<String>());
        Iterator<String> iterator = collect1.iterator();
        while (iterator.hasNext()) {
            String gymId = iterator.next();
//            String address = Constant.SEVER_ADDRESS +
//            HttpUtil.sendOkHttpRequest(address, new Callback() {
//                @Override
//                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    String responseData = response.body().string();
//                    try {
//                        gyms = new ArrayList<>();
//                        JSONArray gymList;
//                        if (type == 0) {
//                            gymList = new JSONObject(responseData).getJSONObject("extend").getJSONArray("gyms");
//                        } else {
//                            gymList = new JSONObject(responseData).getJSONObject("extend").getJSONArray("sub_gyms");
//                        }
//                        for (int i = 0; i < gymList.length(); i++) {
//                            JSONObject subGym = gymList.getJSONObject(i);
//                            Gym gym = new Gym();
//                            gym.setImageSrc(subGym.getString("imageSrc"));
//                            gym.setName(subGym.getString("name"));
//                            gym.setPosition(subGym.getString("address"));
//                            gym.setTel(subGym.getString("userTel"));
//                            gym.setDetail(subGym.getString("introduce"));
//                            gym.setGymId(subGym.getInt("gymId"));
//                            gyms.add(gym);
//                        }
//                        runOnUiThread(new Runnable() {//切换到主线程
//                            @Override
//                            public void run() {
//                                GymAdapter adapter = new GymAdapter(gyms);
//                                recyclerView.setAdapter(adapter);
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        }


    }

    @Override
    public void onClick(View v) {
        setResult(0);
        finish();
    }
}
