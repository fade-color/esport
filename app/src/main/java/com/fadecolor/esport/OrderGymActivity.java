package com.fadecolor.esport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fadecolor.esport.Adapter.OrderAdapter;
import com.fadecolor.esport.domain.Order;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderGymActivity extends BaseActivity {

    CheckBox[][] orderMap = new CheckBox[7][14];

    List<Order> orderList = new LinkedList<>();

    Date[] dates = new Date[7];

    Map<String, Order> orderAddedMap = new HashMap<>();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM月dd日", Locale.CHINA);

    public static final Map<Integer, String> timeMap;

    private int gymId;

    private int id;

    private String userTel;

    static {
        timeMap = new HashMap<>();
        timeMap.put(0,"8:00-9:00");
        timeMap.put(1,"9:00-10:00");
        timeMap.put(2,"10:00-11:00");
        timeMap.put(3,"11:00-12:00");
        timeMap.put(4,"12:00-13:00");
        timeMap.put(5,"13:00-14:00");
        timeMap.put(6,"14:00-15:00");
        timeMap.put(7,"15:00-16:00");
        timeMap.put(8,"16:00-17:00");
        timeMap.put(9,"17:00-18:00");
        timeMap.put(10,"18:00-19:00");
        timeMap.put(11,"19:00-20:00");
        timeMap.put(12,"20:00-21:00");
        timeMap.put(13,"21:00-22:00");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransStatusBar();
        setContentView(R.layout.activity_order_gym);

        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        LinearLayout linearLayout = findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(), statusBarHeight, linearLayout.getPaddingEnd(), linearLayout.getPaddingBottom());

        init();
    }

    private void init() {
        initDates();
        initCheckBoxes();

        Intent intent = getIntent();
        gymId = intent.getIntExtra("GymId",-1);
        id = intent.getIntExtra("Id", -1);
        SharedPreferences prefs = getSharedPreferences("account", MODE_PRIVATE);
        userTel = prefs.getString("account", "null");
        if (gymId == -1 || id == -1 || userTel.equals("null")) {
            finish();
        }

        RecyclerView orderSelected = findViewById(R.id.order_selected);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        orderSelected.setLayoutManager(linearLayoutManager);
        final OrderAdapter orderAdapter = new OrderAdapter(orderList);
        orderSelected.setAdapter(orderAdapter);

        for (int i = 0; i < orderMap.length; i++) {
            for (int j = 0; j < orderMap[i].length; j++) {
                final Date date = dates[i];
                final int period = j;
                final int col = i;
                orderMap[i][j].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            Order order = new Order();
                            order.setDate(date);
                            order.setPeriod(period);
                            orderAddedMap.put(col+""+period, order);
                            orderList.add(order);
                        } else {
                            orderList.remove(orderAddedMap.get(col+""+period));
                        }
                        orderAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    private void initDates() {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(new Date());
        for (int i = 0; i < 7; i++) {
            dates[i] = rightNow.getTime();
            rightNow.add(Calendar.DAY_OF_YEAR, 1);
        }
        initTvDays();
    }

    private void initTvDays() {
        ((TextView)findViewById(R.id.tv_day1)).setText(dateFormat2.format(dates[1]));
        ((TextView)findViewById(R.id.tv_day2)).setText(dateFormat2.format(dates[2]));
        ((TextView)findViewById(R.id.tv_day3)).setText(dateFormat2.format(dates[3]));
        ((TextView)findViewById(R.id.tv_day4)).setText(dateFormat2.format(dates[4]));
        ((TextView)findViewById(R.id.tv_day5)).setText(dateFormat2.format(dates[5]));
        ((TextView)findViewById(R.id.tv_day6)).setText(dateFormat2.format(dates[6]));
    }

    private void initCheckBoxes() {
        orderMap[0][0] = findViewById(R.id.cb_0_0);
        orderMap[0][1] = findViewById(R.id.cb_0_1);
        orderMap[0][2] = findViewById(R.id.cb_0_2);
        orderMap[0][3] = findViewById(R.id.cb_0_3);
        orderMap[0][4] = findViewById(R.id.cb_0_4);
        orderMap[0][5] = findViewById(R.id.cb_0_5);
        orderMap[0][6] = findViewById(R.id.cb_0_6);
        orderMap[0][7] = findViewById(R.id.cb_0_7);
        orderMap[0][8] = findViewById(R.id.cb_0_8);
        orderMap[0][9] = findViewById(R.id.cb_0_9);
        orderMap[0][10] = findViewById(R.id.cb_0_10);
        orderMap[0][11] = findViewById(R.id.cb_0_11);
        orderMap[0][12] = findViewById(R.id.cb_0_12);
        orderMap[0][13] = findViewById(R.id.cb_0_13);
        orderMap[1][0] = findViewById(R.id.cb_1_0);
        orderMap[1][1] = findViewById(R.id.cb_1_1);
        orderMap[1][2] = findViewById(R.id.cb_1_2);
        orderMap[1][3] = findViewById(R.id.cb_1_3);
        orderMap[1][4] = findViewById(R.id.cb_1_4);
        orderMap[1][5] = findViewById(R.id.cb_1_5);
        orderMap[1][6] = findViewById(R.id.cb_1_6);
        orderMap[1][7] = findViewById(R.id.cb_1_7);
        orderMap[1][8] = findViewById(R.id.cb_1_8);
        orderMap[1][9] = findViewById(R.id.cb_1_9);
        orderMap[1][10] = findViewById(R.id.cb_1_10);
        orderMap[1][11] = findViewById(R.id.cb_1_11);
        orderMap[1][12] = findViewById(R.id.cb_1_12);
        orderMap[1][13] = findViewById(R.id.cb_1_13);
        orderMap[2][0] = findViewById(R.id.cb_2_0);
        orderMap[2][1] = findViewById(R.id.cb_2_1);
        orderMap[2][2] = findViewById(R.id.cb_2_2);
        orderMap[2][3] = findViewById(R.id.cb_2_3);
        orderMap[2][4] = findViewById(R.id.cb_2_4);
        orderMap[2][5] = findViewById(R.id.cb_2_5);
        orderMap[2][6] = findViewById(R.id.cb_2_6);
        orderMap[2][7] = findViewById(R.id.cb_2_7);
        orderMap[2][8] = findViewById(R.id.cb_2_8);
        orderMap[2][9] = findViewById(R.id.cb_2_9);
        orderMap[2][10] = findViewById(R.id.cb_2_10);
        orderMap[2][11] = findViewById(R.id.cb_2_11);
        orderMap[2][12] = findViewById(R.id.cb_2_12);
        orderMap[2][13] = findViewById(R.id.cb_2_13);
        orderMap[3][0] = findViewById(R.id.cb_3_0);
        orderMap[3][1] = findViewById(R.id.cb_3_1);
        orderMap[3][2] = findViewById(R.id.cb_3_2);
        orderMap[3][3] = findViewById(R.id.cb_3_3);
        orderMap[3][4] = findViewById(R.id.cb_3_4);
        orderMap[3][5] = findViewById(R.id.cb_3_5);
        orderMap[3][6] = findViewById(R.id.cb_3_6);
        orderMap[3][7] = findViewById(R.id.cb_3_7);
        orderMap[3][8] = findViewById(R.id.cb_3_8);
        orderMap[3][9] = findViewById(R.id.cb_3_9);
        orderMap[3][10] = findViewById(R.id.cb_3_10);
        orderMap[3][11] = findViewById(R.id.cb_3_11);
        orderMap[3][12] = findViewById(R.id.cb_3_12);
        orderMap[3][13] = findViewById(R.id.cb_3_13);
        orderMap[4][0] = findViewById(R.id.cb_4_0);
        orderMap[4][1] = findViewById(R.id.cb_4_1);
        orderMap[4][2] = findViewById(R.id.cb_4_2);
        orderMap[4][3] = findViewById(R.id.cb_4_3);
        orderMap[4][4] = findViewById(R.id.cb_4_4);
        orderMap[4][5] = findViewById(R.id.cb_4_5);
        orderMap[4][6] = findViewById(R.id.cb_4_6);
        orderMap[4][7] = findViewById(R.id.cb_4_7);
        orderMap[4][8] = findViewById(R.id.cb_4_8);
        orderMap[4][9] = findViewById(R.id.cb_4_9);
        orderMap[4][10] = findViewById(R.id.cb_4_10);
        orderMap[4][11] = findViewById(R.id.cb_4_11);
        orderMap[4][12] = findViewById(R.id.cb_4_12);
        orderMap[4][13] = findViewById(R.id.cb_4_13);
        orderMap[5][0] = findViewById(R.id.cb_5_0);
        orderMap[5][1] = findViewById(R.id.cb_5_1);
        orderMap[5][2] = findViewById(R.id.cb_5_2);
        orderMap[5][3] = findViewById(R.id.cb_5_3);
        orderMap[5][4] = findViewById(R.id.cb_5_4);
        orderMap[5][5] = findViewById(R.id.cb_5_5);
        orderMap[5][6] = findViewById(R.id.cb_5_6);
        orderMap[5][7] = findViewById(R.id.cb_5_7);
        orderMap[5][8] = findViewById(R.id.cb_5_8);
        orderMap[5][9] = findViewById(R.id.cb_5_9);
        orderMap[5][10] = findViewById(R.id.cb_5_10);
        orderMap[5][11] = findViewById(R.id.cb_5_11);
        orderMap[5][12] = findViewById(R.id.cb_5_12);
        orderMap[5][13] = findViewById(R.id.cb_5_13);
        orderMap[6][0] = findViewById(R.id.cb_6_0);
        orderMap[6][1] = findViewById(R.id.cb_6_1);
        orderMap[6][2] = findViewById(R.id.cb_6_2);
        orderMap[6][3] = findViewById(R.id.cb_6_3);
        orderMap[6][4] = findViewById(R.id.cb_6_4);
        orderMap[6][5] = findViewById(R.id.cb_6_5);
        orderMap[6][6] = findViewById(R.id.cb_6_6);
        orderMap[6][7] = findViewById(R.id.cb_6_7);
        orderMap[6][8] = findViewById(R.id.cb_6_8);
        orderMap[6][9] = findViewById(R.id.cb_6_9);
        orderMap[6][10] = findViewById(R.id.cb_6_10);
        orderMap[6][11] = findViewById(R.id.cb_6_11);
        orderMap[6][12] = findViewById(R.id.cb_6_12);
        orderMap[6][13] = findViewById(R.id.cb_6_13);
    }
}
