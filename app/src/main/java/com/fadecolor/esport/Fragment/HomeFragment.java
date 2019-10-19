package com.fadecolor.esport.Fragment;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.fadecolor.esport.GymActivity;
import com.fadecolor.esport.MainActivity;
import com.fadecolor.esport.R;
import com.fadecolor.esport.Util.GlideImageLoader;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.enums.PopupAnimation;
import com.youth.banner.Banner;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements AMapLocationListener , View.OnClickListener {
    private static final String TAG = "HomeFragment";

    private RadioButton mTvPosition;

    private TextView mTvSearch, mTvTime, position;

    private Banner mBanner;

    private ImageView qrCodeScanner;

    private CardView gym,yugym,pinggym,paigym,langym;

    private LinearLayout linearLayout;
    private String locationText = "点击获取";

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationClientOption = null;    //定位参数类
    private String city = "点击获取";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestLocation();//高德定位
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mTvSearch = view.findViewById(R.id.tv_search);
        position = view.findViewById(R.id.position);
        gym = view.findViewById(R.id.Gym);
        langym = view.findViewById(R.id.LanGym);
        yugym = view.findViewById(R.id.YuGym);
        pinggym = view.findViewById(R.id.PingGym);
        paigym = view.findViewById(R.id.PaiGym);

        langym.setOnClickListener(this);
        yugym.setOnClickListener(this);
        pinggym.setOnClickListener(this);
        paigym.setOnClickListener(this);
        gym.setOnClickListener(this);

        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "不许点我>_<", Toast.LENGTH_SHORT).show();
            }
        });
        mTvTime = view.findViewById(R.id.tv_time);
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日", Locale.CHINA);
        mTvTime.setText(format.format(new Date()));
        mTvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomPopup customPopup = new CustomPopup(getContext());
                new XPopup.Builder(getContext())
                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                        .autoOpenSoftInput(true)
                        .asCustom(customPopup)
                        .show();
            }
        });
        qrCodeScanner = view.findViewById(R.id.qr_code_scanner);
        qrCodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CaptureActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_CODE_SCAN);
            }
        });
        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        linearLayout = view.findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(), statusBarHeight, linearLayout.getPaddingEnd(), linearLayout.getPaddingBottom());
        mTvPosition = view.findViewById(R.id.tv_position);
        mTvPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvPosition.setText("定位中");
                // getLocation();
                requestLocation();
            }
        });
        mBanner = view.findViewById(R.id.banner);
        // 速度
        mBanner.setDelayTime(3000);
        // 设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        // 设置图片集合
        List<Uri> images = new ArrayList<>();
        images.add(getUriFromDrawableRes(R.drawable.post_image1));
        images.add(getUriFromDrawableRes(R.drawable.post_image3));
        mBanner.setImages(images);
        mBanner.start();
        mTvPosition.setText(locationText);
        return view;
    }

    public Uri getUriFromDrawableRes(int id) {
        Resources resources = getResources();
        String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(id) + "/"
                + resources.getResourceTypeName(id) + "/"
                + resources.getResourceEntryName(id);
        return Uri.parse(path);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        Log.d("aaa", "requestCode" + requestCode);
        if (requestCode == MainActivity.REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                if (content.matches("wechat")) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                    intent.setData(Uri.parse(content));
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(cmp);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //
    private void requestLocation() {
        locationClient = new AMapLocationClient(MainActivity.context);
        locationClient.setLocationListener(this);
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

    @Override
    public void onLocationChanged(final AMapLocation amapLocation) {
        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
        city = amapLocation.getCity();//获取当前城市

        amapLocation.getAccuracy();//获取精度信息
        if (null != city){
            mTvPosition.setText(city);
         position.setText(city);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), GymActivity.class);
        int type = 0;
        switch (v.getId()) {
            case R.id.YuGym:
                type = 1;
               break;
            case R.id.PingGym:
                type = 4;
               break;
            case R.id.PaiGym:
                type = 5;
                break;
            case R.id.LanGym:
                type = 2;
              break;
            default:
               break;
        }
        intent.putExtra("type",type);
        startActivity(intent);

}

    class CustomPopup extends CenterPopupView {
        public CustomPopup(@NonNull Context context) {
            super(context);
        }

        @Override
        protected int getImplLayoutId() {
            return R.layout.layout_select_date;
        }
        @Override
        protected void onCreate() {
            super.onCreate();
            CalendarView calendarView = findViewById(R.id.cv_date);
            Date date = new Date();
            calendarView.setMinDate(date.getTime());
            calendarView.setMaxDate(date.getTime()+1000*60*60*24*6);
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    mTvTime.setText(month+1+"月"+dayOfMonth+"日");
                    dismiss();
                }
            });
        }

        protected void onShow() {
            super.onShow();
        }

//        @Override
//        protected int getMaxHeight() {
//            return 200;
//        }
//
        //返回0表示让宽度撑满window，或者你可以返回一个任意宽度
//        @Override
//        protected int getMaxWidth() {
//            return 1200;
//        }
    }

}
