package com.fadecolor.esport.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fadecolor.esport.Adapter.PopularAdapter;
import com.fadecolor.esport.CreateActivity;
import com.fadecolor.esport.R;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.Util.HttpUtil;
import com.fadecolor.esport.domain.Activity;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DynamicFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;

    private List<Activity> activities = new ArrayList<>();

    private RefreshLayout refreshLayout;

    private AddFloatingActionButton actionButtonCreate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        actionButtonCreate = view.findViewById(R.id.create_activity);
        actionButtonCreate.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.popular_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        final PopularAdapter adapter = new PopularAdapter(activities);
        recyclerView.setAdapter(adapter);
        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(),statusBarHeight,linearLayout.getPaddingEnd(),linearLayout.getPaddingBottom());
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                String address = Constant.SEVER_ADDRESS + "/activities/1";
                HttpUtil.sendOkHttpRequest(address, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        try {
                            activities.clear();
                            JSONArray activityList = new JSONObject(responseData).getJSONObject("extend").getJSONObject("activities").getJSONArray("list");
                            for (int i = 0; i < activityList.length(); i++) {
                                JSONObject act = activityList.getJSONObject(i);
                                int activityId = act.getInt("activityId");
                                Date time = new Date(act.getLong("time"));
                                String image = act.getString("imageSrc");
                                String location = act.getString("location");
                                int commentNum = act.getInt("commentNum");
                                String userTel = act.getJSONObject("user").getString("tel");
                                String userName = act.getJSONObject("user").getString("userName");
                                String content = act.getString("content");
                                String headPath = act.getJSONObject("user").getString("headPath");
                                Activity activity = new Activity(activityId,time,image,location,commentNum,userTel,content,userName,headPath);
                                activities.add(activity);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                    recyclerView.scrollToPosition(0);
                                    refreshlayout.finishRefresh(true);//传入false表示刷新失败
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activities.size() == 0) {
            refreshLayout.autoRefresh();
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.create_activity:
                intent = new Intent(getContext(), CreateActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.autoRefresh();
                        }
                    });
                }
        }
    }
}
