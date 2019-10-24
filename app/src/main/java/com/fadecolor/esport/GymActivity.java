package com.fadecolor.esport;

import android.util.Log;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fadecolor.esport.Adapter.GymAdapter;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.Util.HttpUtil;
import com.fadecolor.esport.domain.Gym;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.amap.api.maps.model.BitmapDescriptorFactory.getContext;

public class GymActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<Gym> gyms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);

        recyclerView = findViewById(R.id.Gym_recyclerview);
        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        LinearLayout linearLayout = findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(), statusBarHeight, linearLayout.getPaddingEnd(), linearLayout.getPaddingBottom());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final int type = getIntent().getIntExtra("type",0);
        String address = Constant.SEVER_ADDRESS + "/query/allGyms";
        if (type != 0)
            address = Constant.SEVER_ADDRESS + "/query/subGymByType?type="+type;
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    gyms = new ArrayList<>();
                    JSONArray gymList;
                    if (type == 0) {
                        gymList = new JSONObject(responseData).getJSONObject("extend").getJSONArray("gyms");
                    } else {
                        gymList = new JSONObject(responseData).getJSONObject("extend").getJSONArray("sub_gyms");
                    }
                    for (int i = 0; i < gymList.length(); i++) {
                        JSONObject subGym = gymList.getJSONObject(i);
                        Gym gym = new Gym();
                        gym.setImageSrc(subGym.getString("imageSrc"));
                        gym.setName(subGym.getString("name"));
                        gym.setPosition(subGym.getString("address"));
                        gym.setTel(subGym.getString("userTel"));
                        gym.setDetail(subGym.getString("introduce"));
                        gym.setGymId(subGym.getInt("gymId"));
                        gyms.add(gym);
                    }
                    runOnUiThread(new Runnable() {//切换到主线程
                        @Override
                        public void run() {
                            GymAdapter adapter = new GymAdapter(gyms);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

}
