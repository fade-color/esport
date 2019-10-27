package com.fadecolor.esport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import com.fadecolor.esport.Adapter.GymAdapter;
import com.fadecolor.esport.Adapter.SubGymAdapter;
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

public class GymDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView GymImage,call,imageView;
    private TextView GymName, GymPosition,GymTel,GymDetail;
    private RecyclerView recyclerView;
    private List<Gym> gyms;
    private CheckBox collect;
    private int GymId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_detail);
        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        LinearLayout linearLayout = findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(), statusBarHeight, linearLayout.getPaddingEnd(), linearLayout.getPaddingBottom());
        SharedPreferences prefs = getSharedPreferences("account", MODE_PRIVATE);
        HashSet<String> collect1 = (HashSet<String>) prefs.getStringSet("collect_Gym_id", new HashSet<String>());

        GymImage = findViewById(R.id.Gym_image);
        GymName = findViewById(R.id.Gym_name);
        GymPosition = findViewById(R.id.Gym_position);
        GymTel = findViewById(R.id.Gym_tel);
        GymDetail = findViewById(R.id.Gym_detail);
        recyclerView = findViewById(R.id.Gym_recyclerview);
        imageView = findViewById(R.id.iv_back);
        imageView.setOnClickListener(this);
        call = findViewById(R.id.call);
        collect = findViewById(R.id.collect);
        call.setOnClickListener(this);
        init();
        Iterator<String> iterator = collect1.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.equals(GymId+"")) {
                collect.setChecked(true);
            }
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }
    public void init(){
        gyms = new ArrayList<>();
        Gym gym = new Gym();
        String ImagePath = getIntent().getStringExtra("ImagePath");
        String name = getIntent().getStringExtra("GymName");
        String position = getIntent().getStringExtra("GymPosition");
        final String tel = getIntent().getStringExtra("GymTel");
        String detail = getIntent().getStringExtra("GymDetail");
        GymId = getIntent().getIntExtra("GymId",0);
        GymName.setText(name);
        GymPosition.setText(position);
        GymTel.setText(tel);
        GymDetail.setText("场馆简介:"+"\n"+detail);
        Glide.with(this)
                .load(Constant.GYM_IMG_PATH + ImagePath)
                .into(GymImage);
        gym.setImageSrc(ImagePath);
        gym.setPosition(position);
        gym.setTel(tel);
        gym.setName(name);
        gyms.add(gym);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:"+tel);
                Intent  intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        //
        collect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = getSharedPreferences("account", MODE_PRIVATE);
                HashSet<String> collect1 = (HashSet<String>) prefs.getStringSet("collect_Gym_id", new HashSet<String>());
                SharedPreferences.Editor editor = getSharedPreferences("account", MODE_PRIVATE).edit();
                if (isChecked){
                    collect1.add(GymId+"");
                    editor.putStringSet("collect_Gym_id",collect1);
                } else {
                    collect1.remove(GymId+"");
                }
                editor.apply();
            }
        });//
        String address  = Constant.SEVER_ADDRESS + "/query/subGymByGymId?gymId="+GymId;
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
                    gymList = new JSONArray(responseData);
                    for (int i = 0; i < gymList.length(); i++) {
                        JSONObject subGym = gymList.getJSONObject(i);
                        Gym gym = new Gym();
                        gym.setKind(subGym.getInt("kind"));
                        gym.setId(subGym.getInt("id"));
                        gym.setType(subGym.getInt("type"));
                        gym.setMaxCount(subGym.getInt("maxCount"));
                        gym.setGymId(subGym.getInt("gymId"));
                        gyms.add(gym);
                    }
                    runOnUiThread(new Runnable() {//切换到主线程
                        @Override
                        public void run() {
                            SubGymAdapter adapter = new SubGymAdapter(gyms);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }




    @Override
    public void onClick(View v) {
        Intent intent ;
        switch (v.getId()){
            case R.id.call:
                intent = new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
                break;
            case R.id.iv_back:
                setResult(0);
                finish();
                break;
        }
    }
}
