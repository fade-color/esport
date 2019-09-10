package com.fadecolor.esport;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fadecolor.esport.Fragment.DynamicFragment;
import com.fadecolor.esport.Fragment.HomeFragment;
import com.fadecolor.esport.Fragment.IndividualFragment;
import com.fadecolor.esport.Fragment.InventionFragment;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SCAN = 1;

    public static Context context;

    private RadioGroup mTabRadioGroup;
    private SparseArray<Fragment> mFragmentSparseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransStatusBar();
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        initView();
    }

    private void initView() {
        mTabRadioGroup = findViewById(R.id.tabs_rg);
        mFragmentSparseArray = new SparseArray<>();
        mFragmentSparseArray.append(R.id.today_tab, new HomeFragment());
        mFragmentSparseArray.append(R.id.record_tab, new InventionFragment());
        mFragmentSparseArray.append(R.id.contact_tab, new DynamicFragment());
        mFragmentSparseArray.append(R.id.settings_tab, new IndividualFragment());
        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 具体的fragment切换逻辑可以根据应用调整，例如使用show()/hide()
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mFragmentSparseArray.get(checkedId))
                        .commit();
            }

        });
//         默认显示第一个
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mFragmentSparseArray.get(R.id.today_tab))
                .commit();
    }

    private void setTransStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (keyCode == KEYCODE_BACK && current instanceof InventionFragment) {
            WebView webView = null;
            InventionFragment inventionFragment = (InventionFragment) mFragmentSparseArray.get(R.id.record_tab);
            if (inventionFragment != null) {
                webView = inventionFragment.getView().findViewById(R.id.web_view);
            }
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
