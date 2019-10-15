package com.fadecolor.esport;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.fadecolor.esport.Adapter.CommentAdapter;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.Util.HttpUtil;
import com.fadecolor.esport.customize.AdaptiveImageView;
import com.fadecolor.esport.domain.Comment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommentActivity extends BaseActivity implements View.OnClickListener {

    private int activityId;

    private String userName;

    private String headPath;

    private LinearLayout commentLayout;

    private EditText mEtComment;

    private Button mBtnSubmit;

    private TextView mTvUserName, mTvNow, mTvText, mTvLocation, mTvCommentNum;

    private ImageView mIvBack;

    private AdaptiveImageView mIvImage;

    private CircleImageView mCivUserHead;

    private RecyclerView mRvComments;

    private boolean isTextEmpty = true;

    ArrayList<Comment> comments;

    CommentAdapter adapter;

    private void init() {
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        headPath = intent.getStringExtra("headPath");
        activityId = intent.getIntExtra("activityId", -1);
        commentLayout = findViewById(R.id.comment_layout);
        mEtComment = findViewById(R.id.et_comment);
        mBtnSubmit = findViewById(R.id.btn_comment);
        mTvUserName = findViewById(R.id.user_name);
        mTvNow = findViewById(R.id.tv_now);
        mTvText = findViewById(R.id.tv_text);
        mTvLocation = findViewById(R.id.tv_location);
        mTvCommentNum = findViewById(R.id.tv_comment_num);
        mIvImage = findViewById(R.id.iv_image);
        mIvBack = findViewById(R.id.iv_back);
        mCivUserHead = findViewById(R.id.user_head);
        mRvComments = findViewById(R.id.comments_view);

        mBtnSubmit.setOnClickListener(this);
        mIvBack.setOnClickListener(this);

        mEtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (text.equals("")) {
                    mBtnSubmit.setTextColor(getResources().getColor(R.color.colorDeepGray));
                    mBtnSubmit.setBackgroundResource(R.drawable.btn_send_disable);
                    isTextEmpty = true;
                } else {
                    mBtnSubmit.setTextColor(getResources().getColor(R.color.colorPurple));
                    mBtnSubmit.setBackgroundResource(R.drawable.btn_send_enable);
                    isTextEmpty = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        comments = new ArrayList<>();
        loadComments(1);
    }

    private void loadComments(final int i) {
        HttpUtil.sendOkHttpRequest(Constant.SEVER_ADDRESS + "/activity/" + activityId + "/comment", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(CommentActivity.this,"请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            comments.clear();
                            JSONArray commentsJSONArray = new JSONArray(responseData);
                            for (int i = 0; i < commentsJSONArray.length(); i++) {
                                JSONObject commentJSONObject = commentsJSONArray.getJSONObject(i);
                                Comment comment = new Comment();
                                comment.setUserTel(commentJSONObject.getString("userTel"));
                                comment.setContent(commentJSONObject.getString("content"));
                                comment.setHeadPath(commentJSONObject.getString("headPath"));
                                comment.setTime(new Date(commentJSONObject.getLong("time")));
                                comment.setUserName(commentJSONObject.getString("userName"));
                                comments.add(comment);
                            }
                            if (i == 1) {
                                LinearLayoutManager manager = new LinearLayoutManager(CommentActivity.this);
                                mRvComments.setLayoutManager(manager);
                                adapter = new CommentAdapter(comments);
                                mRvComments.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        RelativeLayout linearLayout = findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(),statusBarHeight,linearLayout.getPaddingEnd(),linearLayout.getPaddingBottom());

        init();
        linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int mScreenHeight = 0;
                Rect rect = new Rect();
                // 测量当前窗口的显示区域
                getWindow().getDecorView()
                        .getWindowVisibleDisplayFrame(rect);
                if(mScreenHeight <= 0){
                    mScreenHeight = ((WindowManager) getApplicationContext()
                            .getSystemService(Context.WINDOW_SERVICE))
                            .getDefaultDisplay().getHeight();
                }
                //计算出软键盘的高度
                int keyboardHeight = mScreenHeight - rect.bottom;

                //如果keyboardHeight大于屏幕的五分之一，
                // 此时keyboardHeight有效，反之就是软键盘已经关闭了。
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) commentLayout.getLayoutParams();
                if (Math.abs(keyboardHeight) > mScreenHeight / 5) {
                    layoutParams.bottomMargin = keyboardHeight;
                } else {
                    layoutParams.bottomMargin = 0;
                }
                commentLayout.setLayoutParams(layoutParams);
            }
        });

        loadData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comment:
                if (!isTextEmpty) {
                    SharedPreferences prefs = getSharedPreferences("account", MODE_PRIVATE);
                    String userId = prefs.getString("userId","null");
                    if (!userId.equals("null")) {
                        HttpUtil.sendOkHttpRequest(Constant.SEVER_ADDRESS + "/activity/" + activityId
                                        + "/comment/reply?userTel=" + userId + "&content="
                                        + mEtComment.getText().toString(),
                                new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(CommentActivity.this, "发生异常", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        String responseData = response.body().string();
                                        Log.d("---------------------", "onResponse: "+responseData);
                                        try {
                                            JSONObject msg = new JSONObject(responseData);
                                            int code = msg.getInt("code");
                                            if (code == 100) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mEtComment.setText("");
                                                        new SVProgressHUD(CommentActivity.this).showSuccessWithStatus("回复成功");
                                                        loadComments(2);
                                                        InputMethodManager m=(InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
                                                        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                                                    }
                                                });
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(CommentActivity.this, "回复失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                });
                    }
                }
                break;
            case R.id.iv_back:
                this.finish();
                break;
        }
    }

    private void loadData() {
        HttpUtil.sendOkHttpRequest(Constant.SEVER_ADDRESS + "/activity?id=" + activityId, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CommentActivity.this,"请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseString = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
                            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm", Locale.CHINA);
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH) + 1;
                            int day = calendar.get(Calendar.DATE);
                            JSONObject activityJSON = new JSONObject(responseString);
                            Date date = new Date(activityJSON.getLong("time"));
                            String imageSrc = activityJSON.getString("imageSrc");
                            String location = activityJSON.getString("location");
                            int commentNum = activityJSON.getInt("commentNum");
                            String content = activityJSON.getString("content");
                            if (headPath!=null && !"null".equals(headPath)) {
                                Glide.with(CommentActivity.this).load(Constant.HEAD_PATH + headPath).into(mCivUserHead);
                            } else {
                                mCivUserHead.setImageResource(R.drawable.ic_user_default);
                            }
                            mTvUserName.setText(userName.equals("null")? activityJSON.getString("userTel"):userName);
                            if (year == (date.getYear() + 1900) && month == (date.getMonth() + 1) && day == date.getDate()) {
                                mTvNow.setText(format2.format(date));
                            } else {
                                mTvNow.setText(format1.format(date));
                            }
                            mTvText.setText(content);
                            if (!imageSrc.equals("null")) {
                                mIvImage.setVisibility(View.VISIBLE);
                                Glide.with(CommentActivity.this).load(Constant.ACTIVITY_IMG_PATH + imageSrc).into(mIvImage);
                            } else {
                                mIvImage.setVisibility(View.GONE);
                            }
                            mTvLocation.setText(location);
                            mTvCommentNum.setText(commentNum>99? "99+":commentNum+"");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}
