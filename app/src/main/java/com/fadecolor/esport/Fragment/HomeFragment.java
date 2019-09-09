package com.fadecolor.esport;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private Fruit[] fruits = {new Fruit("支付宝", R.drawable.ali_pay), new Fruit("淘宝", R.drawable.ali_pay), new Fruit("京东", R.drawable.ali_pay), new Fruit("QQ", R.drawable.ali_pay), new Fruit("百度", R.drawable.ali_pay), new Fruit("支付宝", R.drawable.ali_pay), new Fruit("淘宝", R.drawable.ali_pay), new Fruit("京东", R.drawable.ali_pay), new Fruit("QQ", R.drawable.ali_pay), new Fruit("百度", R.drawable.ali_pay)};

    private FruitAdapter adapter;

    private List<Fruit> fruitList = new ArrayList<>();

    private TextView mTvPosition;

    private TextView mTvSearch;

    private Banner mBanner;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private String locationText = "点击获取";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocation();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initFruits(view);
        Log.d(TAG, "onCreateView: ");
        mTvSearch = view.findViewById(R.id.tv_search);
        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "不许点我>_<", Toast.LENGTH_SHORT).show();
            }
        });
        mTvPosition = view.findViewById(R.id.tv_position);
        mTvPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
        mBanner = view.findViewById(R.id.banner);
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
        mTvPosition.setText(locationText);
        return view;
    }

    public void getLocation() {
        mLocationClient = new LocationClient(MainActivity.context);
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

    private void initFruits(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new FruitAdapter(fruitList);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 5);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        fruitList.clear();
        for (int i = 0; i < 10; i++) {
            //Random random = new Random();
            //int index = random.nextInt(fruits.length);
            //fruitList.add(fruits[index]);
            fruitList.add(fruits[i]);
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
            if (city != null) {
                mTvPosition.setText(city);
                locationText = city;
            }
        }
    }

}
