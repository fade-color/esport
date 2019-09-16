package com.fadecolor.esport;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.RadioGroup;

import com.fadecolor.esport.Fragment.DynamicFragment;
import com.fadecolor.esport.Fragment.HomeFragment;
import com.fadecolor.esport.Fragment.IndividualFragment;
import com.fadecolor.esport.Fragment.InventionFragment;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SCAN = 1;

    public static Context context;

    private RadioGroup mTabRadioGroup;
    private SparseArray<Fragment> mFragmentSparseArray;

    private int currentFragmentId;
    private SparseArray<Integer> fragmentIndex;

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
        fragmentIndex = new SparseArray<>();
        mFragmentSparseArray.append(R.id.today_tab, new HomeFragment());
        mFragmentSparseArray.append(R.id.record_tab, new InventionFragment());
        mFragmentSparseArray.append(R.id.contact_tab, new DynamicFragment());
        mFragmentSparseArray.append(R.id.settings_tab, new IndividualFragment());
        fragmentIndex.append(R.id.today_tab, 0);
        fragmentIndex.append(R.id.record_tab, 1);
        fragmentIndex.append(R.id.contact_tab, 2);
        fragmentIndex.append(R.id.settings_tab, 3);
        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 具体的fragment切换逻辑可以根据应用调整，例如使用show()/hide()
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (currentFragmentId < fragmentIndex.get(checkedId)) {
                    transaction.setCustomAnimations(R.anim.fragment_right_in, R.anim.fragment_left_out, R.anim.fragment_left_in, R.anim.fragment_right_out);
                } else if (currentFragmentId > fragmentIndex.get(checkedId)) {
                    transaction.setCustomAnimations(R.anim.fragment_left_in, R.anim.fragment_right_out, R.anim.fragment_right_in, R.anim.fragment_left_out);
                }
                transaction.replace(R.id.fragment_container, mFragmentSparseArray.get(checkedId)).commit();
                currentFragmentId = fragmentIndex.get(checkedId);
            }

        });
//         默认显示第一个
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mFragmentSparseArray.get(R.id.today_tab))
                .commit();
        currentFragmentId = fragmentIndex.get(R.id.today_tab);
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
