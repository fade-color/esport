package com.fadecolor.esport;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.fadecolor.esport.Fragment.DynamicFragment;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, AMapLocationListener, LocationSource, PoiSearch.OnPoiSearchListener, Inputtips.InputtipsListener, SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationClientOption = null;    //定位参数类
    private LocationSource.OnLocationChangedListener listener;
    private MapView mapView;
    private AMap aMap;
    private RecyclerView recyclerView;
    private Marker locationMarker;
    private String cityName;
    private SearchView searchView;
    private ListView listView;
    private String city = "北京";
    private ArrayAdapter<String> adapter;
    private LatLonPoint point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化定位
        locationClient = new AMapLocationClient(getApplicationContext());
        locationClient.setLocationListener(this);
        setContentView(R.layout.activity_location);
        listView = findViewById(R.id.list_view1);
        searchView = findViewById(R.id.input);
        searchView.setOnQueryTextListener(this);
        requestPermission();
    }
    //权限设置
    private void requestPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permission = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LocationActivity.this, permission, 1);
        } else {
            requestLocation();
        }

    }

    //
    private void requestLocation() {
        initLocation();
        locationClient.startLocation();
    }

    //定位参数设置
    private void initLocation() {
        locationClientOption = new AMapLocationClientOption();
        locationClientOption.setInterval(0);
        locationClientOption.setNeedAddress(true);
        locationClientOption.setMockEnable(true);
        locationClient.setLocationOption(locationClientOption);
        //locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    }

    //权限设置
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    //改变位置
    @Override
    public void onLocationChanged(final AMapLocation amapLocation) {
        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
        double latitude = amapLocation.getLatitude();//获取纬度
        double longitude = amapLocation.getLongitude();//获取经度
        city = amapLocation.getCity();//获取当前城市
        point = new LatLonPoint(latitude, longitude);
        amapLocation.getAccuracy();//获取精度信息
        onQueryTextChange("");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }


    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        PoiSearch.Query query = poiResult.getQuery();
        ArrayList<PoiItem> pois = poiResult.getPois();
        ArrayList<String> d = new ArrayList<String>();
        d.add("不显示");
        for (PoiItem poi : pois) {
            String title = poi.getTitle();
            d.add(title);
        }
        //listView.setAdapter(new MapListAdapter(MainActivity.this,d));
        adapter = new ArrayAdapter<String>(LocationActivity.this, android.R.layout.simple_list_item_1, d);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }



    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        ArrayList<String> d = new ArrayList<String>();
        for (Tip info : list) {
            d.add(info.getName());
        }
        adapter = new ArrayAdapter<String>(LocationActivity.this, android.R.layout.simple_list_item_1, d);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(this);
    }

    //点击地址
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String i = (String) listView.getAdapter().getItem(position);
        Intent intent = new Intent(LocationActivity.this,CreateActivity.class);
        intent.putExtra("data", i);
        startActivity(intent);
          //Toast.makeText(LocationActivity.this,"点击 pos:"+i,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            InputtipsQuery inputtipsQuery = new InputtipsQuery(newText, city);
            inputtipsQuery.setCityLimit(true);
            Inputtips inputTips = new Inputtips(LocationActivity.this, inputtipsQuery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        } else {
            PoiSearch.Query query = new PoiSearch.Query("", "体育休闲服务", "");
            query.setPageSize(10);
            PoiSearch search = new PoiSearch(this, query);
            search.setBound(new PoiSearch.SearchBound(point, 1000));//设置周边搜索的中心点以及半径//设置周边搜索的中心点以及半径
            search.setOnPoiSearchListener(this);
            search.searchPOIAsyn();

        }

        return false;
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        listener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁定位客户端
        if (locationClient != null) {
            locationClient.onDestroy();
            locationClient = null;
            locationClientOption = null;
        }
    }


}