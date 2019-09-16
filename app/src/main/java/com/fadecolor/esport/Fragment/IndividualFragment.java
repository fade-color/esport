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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fadecolor.esport.LoginActivity;
import com.fadecolor.esport.MainActivity;
import com.fadecolor.esport.R;
import com.fadecolor.esport.Util.HttpUtil;
import com.fadecolor.esport.tabactivity.AboutActivity;

public class IndividualFragment extends Fragment {

    private Button mBtnLogout;

    private ImageView mIvHead;

    private TextView mTvName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual, container, false);
        mBtnLogout = view.findViewById(R.id.btn_logout);
        mIvHead = view.findViewById(R.id.iv_head);
        mTvName = view.findViewById(R.id.tv_name);
        SharedPreferences prefs = view.getContext().getSharedPreferences("account", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if (userId == -1) {
            mTvName.setText("未登录");
        } else {
            mTvName.setText(prefs.getString("userName", "昵称"));
        }
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = view.getContext().getSharedPreferences("account", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(),statusBarHeight,linearLayout.getPaddingEnd(),linearLayout.getPaddingBottom());
        LinearLayout about = view.findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AboutActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
