package com.fadecolor.esport;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fadecolor.esport.Adapter.GymAdapter;
import com.fadecolor.esport.Adapter.MyOrderAdapter;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.Util.HttpUtil;
import com.fadecolor.esport.domain.Order;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyOrderActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private ImageView imageView;
    private List<Order> orders;
    private LinearLayout Yes,No;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        RelativeLayout linearLayout = findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(), statusBarHeight, linearLayout.getPaddingEnd(), linearLayout.getPaddingBottom());
        recyclerView = findViewById(R.id.Order_recyclerview);
        imageView = findViewById(R.id.iv_back);
        Yes = findViewById(R.id.Yes);
        No = findViewById(R.id.none);
        imageView.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SharedPreferences prefs = getSharedPreferences("account", MODE_PRIVATE);
        String userTel = prefs.getString("userId", "null");
        String address = Constant.SEVER_ADDRESS + "/user/"+userTel+"/myOrder";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                try {
                orders = new ArrayList<>();
                    JSONArray  orderList ;
                    orderList =  new JSONArray(responseData);
                    for (int i = 0; i <orderList.length(); i++) {
                        JSONObject MyOrder= orderList.getJSONObject(i);
                        Order order = new Order();
                        order.setImageSrc(MyOrder.getString("imageSrc"));
                        order.setName(MyOrder.getString("name"));
                        order.setGymId(MyOrder.getInt("gymId"));
                        order.setPeriod(MyOrder.getInt("period"));
                        order.setAddress(MyOrder.getString("imageSrc"));
                        order.setDate(new Date(MyOrder.getLong("date")));
                        orders.add(order);
                    }
                    runOnUiThread(new Runnable() {//切换到主线程
                        @Override
                        public void run() {
                            if (orders.isEmpty()){
                                No.setVisibility(View.VISIBLE);
                                Yes.setVisibility(View.GONE);
                            } else {
                            MyOrderAdapter adapter = new MyOrderAdapter(orders);
                            recyclerView.setAdapter(adapter);}
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
        setResult(0);
        finish();
    }
}
