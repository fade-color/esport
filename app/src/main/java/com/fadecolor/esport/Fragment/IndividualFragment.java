package com.fadecolor.esport.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.fadecolor.esport.*;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.Util.HttpUtil;
import com.fadecolor.esport.domain.User;
import com.fadecolor.esport.tabactivity.AboutActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class IndividualFragment extends Fragment implements View.OnClickListener {

    private Button mBtnLogout;

    private ImageView mIvHead;

    private TextView mTvName;

    private RelativeLayout headerRelativeLayout;

    private LinearLayout btn,collect;


    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user = new User();
        View view = inflater.inflate(R.layout.fragment_individual, container, false);
        mBtnLogout = view.findViewById(R.id.btn_logout);
        mIvHead = view.findViewById(R.id.iv_head);
        mTvName = view.findViewById(R.id.tv_name);
        btn = view.findViewById(R.id.btn);
        collect = view.findViewById(R.id.collect);
        collect.setOnClickListener(this);
        btn.setOnClickListener(this);
        headerRelativeLayout = view.findViewById(R.id.header_relative_layout);
        headerRelativeLayout.setOnClickListener(this);
        SharedPreferences prefs = view.getContext().getSharedPreferences("account", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", "null");
        if (userId.equals("null")) {
            mTvName.setText("未登录");
        } else {
            String userName = prefs.getString("userName", "昵称");
            String headPath = prefs.getString("headPath", "null");
            user.setTel(userId);
            user.setUsername(userName);
            user.setHeadPath(headPath);
            if (!"null".equals(headPath)) {
                Glide.with(getContext()).load(Constant.HEAD_PATH + headPath).into(mIvHead);
            }
            mTvName.setText("null".equals(userName)? userId+"":userName);
        }
        mBtnLogout.setOnClickListener(this);
        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(),statusBarHeight,linearLayout.getPaddingEnd(),linearLayout.getPaddingBottom());
        LinearLayout about = view.findViewById(R.id.about);
        about.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.header_relative_layout:
                intent = new Intent(view.getContext(), PersonalDetailsActivity.class);
                intent.putExtra("tel",user.getTel());
                startActivityForResult(intent, 1);
                break;
            case R.id.about:
                intent = new Intent(view.getContext(), AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_logout:
                SharedPreferences.Editor editor = view.getContext().getSharedPreferences("account", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.btn:
                intent = new Intent(view.getContext(), MyOrderActivity.class);
                startActivity(intent);
                break;
            case R.id.collect:
                intent = new Intent(view.getContext(), MyCollectActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences.Editor editor = getContext().getSharedPreferences("account", Context.MODE_PRIVATE).edit();
                            String userName = data.getStringExtra("userName");
                            String headPath = data.getStringExtra("headPath");
                            if (!userName.equals(user.getUsername())) {
                                mTvName.setText(userName);
                                user.setUsername(userName);
                                editor.putString("userName",userName);
                            }
                            if (!headPath.equals(user.getHeadPath())) {
                                Glide.with(getContext()).load(Constant.HEAD_PATH+headPath).into(mIvHead);
                                user.setHeadPath(headPath);
                                editor.putString("headPath",headPath);
                            }
                            editor.apply();
                        }
                    });
                }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getContext().getSharedPreferences("account", Context.MODE_PRIVATE).edit();
        editor.putString("userName", user.getUsername());
        editor.putString("headPath", user.getHeadPath());
        editor.apply();
    }
}
