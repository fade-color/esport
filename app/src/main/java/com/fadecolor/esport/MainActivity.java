package com.fadecolor.esport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RadioGroup mTabRadioGroup;
    private SparseArray<Fragment> mFragmentSparseArray;
    private Fruit[] fruits = {new Fruit("支付宝", R.drawable.ali_pay), new Fruit("淘宝", R.drawable.ali_pay), new Fruit("京东", R.drawable.ali_pay), new Fruit("QQ", R.drawable.ali_pay), new Fruit("百度", R.drawable.ali_pay), new Fruit("支付宝", R.drawable.ali_pay), new Fruit("淘宝", R.drawable.ali_pay), new Fruit("京东", R.drawable.ali_pay), new Fruit("QQ", R.drawable.ali_pay), new Fruit("百度", R.drawable.ali_pay)};
    private List<Fruit> fruitList = new ArrayList<>();
    private FruitAdapter adapter;

    private TextView mTvPosition;

    private TextView mTvSearch;

    private Banner mBanner;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransStatusBar();
        setContentView(R.layout.activity_main);

        requestPermissions();
        initFruits();

        mTvSearch = findViewById(R.id.tv_search);
        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "请欸我却觉得", Toast.LENGTH_SHORT).show();
            }
        });
        mTvPosition = findViewById(R.id.tv_position);
        mTvPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
        mBanner = findViewById(R.id.banner);
        // 速度
        mBanner.setDelayTime(3000);
        // 设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        // 设置图片集合
        String[] images = new String[] {
                "https://cn.bing.com/th?id=OHR.MountFanjing_ZH-CN1999613800_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp",
                "https://cn.bing.com/th?id=OHR.ElMorro_ZH-CN1911346184_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp",
                "https://cn.bing.com/th?id=OHR.Tegallalang_ZH-CN1855493751_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp"
        };
        List<String> imageList = Arrays.asList(images);
        mBanner.setImages(imageList);
        mBanner.start();

        getLocation();
    }

    private void initFruits() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new FruitAdapter(fruitList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        fruitList.clear();
        for (int i = 0; i < 10; i++) {
            //Random random = new Random();
            //int index = random.nextInt(fruits.length);
            //fruitList.add(fruits[index]);
            fruitList.add(fruits[i]);
        }
        initView();
    }

    private void initView() {
        mTabRadioGroup = findViewById(R.id.tabs_rg);
        mFragmentSparseArray = new SparseArray<>();
        mFragmentSparseArray.append(R.id.today_tab, BlankFragment.newInstance("首页"));
        mFragmentSparseArray.append(R.id.record_tab, BlankFragment.newInstance("发现"));
        mFragmentSparseArray.append(R.id.contact_tab, BlankFragment.newInstance("动态"));
        mFragmentSparseArray.append(R.id.settings_tab, BlankFragment.newInstance("我的"));
        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                // 具体的fragment切换逻辑可以根据应用调整，例如使用show()/hide()
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        mFragmentSparseArray.get(checkedId)).commit();
//            }


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != R.id.today_tab)
                    //Toast.makeText(MainActivity.this, String.valueOf(R.id.today_tab), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        // 默认显示第一个
        //getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
        //mFragmentSparseArray.get(R.id.today_tab)).commit();

//        findViewById(R.id.sign_iv).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            }
//        });
    }

    public void requestPermissions() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
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
                            Toast.makeText(MainActivity.this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        } else if (result == PackageManager.PERMISSION_GRANTED) {
                            getLocation();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    public void getLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.start();
    }

    private void setTransStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            mTvPosition.setText(city==null? "未知":city);
        }
    }
}
