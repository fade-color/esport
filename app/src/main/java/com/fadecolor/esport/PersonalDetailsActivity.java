package com.fadecolor.esport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.Util.HttpUtil;
import com.fadecolor.esport.domain.User;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static java.security.AccessController.getContext;

public class PersonalDetailsActivity extends BaseActivity implements View.OnClickListener {

    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;

    private User user;

    private ImageView mIvHead, mIvBack;

    private TextView mTvTel, mTvName, mTvSex, mTvMask, mTvChooseFromGallery, mTvTakePhoto, mTvCancelPic;

    private LinearLayout mPicSelect, mTel, mName, mSex;

    private RelativeLayout mHeader;

    private Uri imageUri;

    private String imagePath = null;

    private float yOffset;

    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        RelativeLayout linearLayout = findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(),statusBarHeight,linearLayout.getPaddingEnd(),linearLayout.getPaddingBottom());
        yOffset = getYOffset();

        mIvHead = findViewById(R.id.iv_head);
        mTvTel = findViewById(R.id.tv_tel);
        mTvName = findViewById(R.id.tv_name);
        mTvSex = findViewById(R.id.tv_sex);
        mTvMask = findViewById(R.id.tv_mask);
        mTvChooseFromGallery = findViewById(R.id.tv_choose_from_gallery);
        mTvTakePhoto = findViewById(R.id.tv_take_photo);
        mTvCancelPic = findViewById(R.id.tv_cancel_pic);
        mPicSelect = findViewById(R.id.pic_select);
        mHeader = findViewById(R.id.header_relative_layout);
        mIvBack = findViewById(R.id.iv_back);
        mTel = findViewById(R.id.linear_layout_tel);
        mName = findViewById(R.id.linear_layout_name);
        mSex = findViewById(R.id.linear_layout_sex);


        mTvMask.setOnClickListener(this);
        mTvChooseFromGallery.setOnClickListener(this);
        mTvTakePhoto.setOnClickListener(this);
        mTvCancelPic.setOnClickListener(this);
        mHeader.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mTel.setOnClickListener(this);
        mName.setOnClickListener(this);
        mSex.setOnClickListener(this);

        queryUser();
    }

    private void loadData(User user) {
        this.user = user;
        if (user.getHeadPath() != null && !user.getHeadPath().equals("null")) {
            Glide.with(PersonalDetailsActivity.this).load(Constant.HEAD_PATH + user.getHeadPath()).into(mIvHead);
        } else {
            mIvHead.setImageResource(R.drawable.ic_user_default);
        }
        mTvName.setText(user.getUsername().equals("null")? "未设置":user.getUsername());
        mTvTel.setText(user.getTel());
        mTvSex.setText(user.getSex().equals("null")? "未设置":user.getSex());
    }

    public void queryUser() {
        final User returnUser = new User();
        String address = Constant.SEVER_ADDRESS + "/user/query?tel=" + getIntent().getStringExtra("tel");
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                try {
                    JSONObject user = new JSONObject(responseData).getJSONObject("extend").getJSONObject("user");
                    String userTel = user.getString("tel");
                    String userName = user.getString("userName");
                    String userSex = user.getString("sex");
                    String path = user.getString("headPath");
                    returnUser.setTel(userTel);
                    returnUser.setUsername(userName);
                    returnUser.setSex(userSex);
                    returnUser.setHeadPath(path);
                    intent.putExtra("headPath",path);
                    intent.putExtra("userName",userName);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadData(returnUser);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        final Intent in;
        switch (v.getId()) {
            case R.id.linear_layout_name:
                new XPopup.Builder(PersonalDetailsActivity.this).asInputConfirm("请输入昵称","",user.getUsername(),
                        new OnInputConfirmListener() {
                            @Override
                            public void onConfirm(final String text) {
                                HttpUtil.sendOkHttpRequest(Constant.SEVER_ADDRESS + "/user/update?tel=" +
                                        user.getTel() + "&user_name=" + text, new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        Toast.makeText(PersonalDetailsActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        String responseData = response.body().string();
                                        try {
                                            JSONObject jsonObject = new JSONObject(responseData);
                                            int statusCode = jsonObject.getInt("code");
                                            if (statusCode == 100) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mTvName.setText(text);
                                                        user.setUsername(text);
                                                        intent.putExtra("userName",text);
                                                        PersonalDetailsActivity.this.setResult(1,intent);
                                                    }
                                                });
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(PersonalDetailsActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(PersonalDetailsActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }).show();
                break;
            case R.id.linear_layout_sex:
                new XPopup.Builder(PersonalDetailsActivity.this)
                        //.maxWidth(600)
                        .asCenterList("", new String[]{"男", "女"},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, final String text) {
                                        HttpUtil.sendOkHttpRequest(Constant.SEVER_ADDRESS + "/user/update?tel=" +
                                                user.getTel() + "&sex=" + text, new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                Toast.makeText(PersonalDetailsActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                String responseData = response.body().string();
                                                try {
                                                    JSONObject jsonObject = new JSONObject(responseData);
                                                    int statusCode = jsonObject.getInt("code");
                                                    if (statusCode == 100) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                mTvSex.setText(text);
                                                                user.setSex(text);
                                                            }
                                                        });
                                                    } else {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(PersonalDetailsActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(PersonalDetailsActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                })
                        .show();
                break;
            case R.id.linear_layout_tel:
                new SVProgressHUD(PersonalDetailsActivity.this).showInfoWithStatus("暂不支持修改手机号！");
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.header_relative_layout:
                showSelectPic();
                break;
            case R.id.tv_cancel_pic:
            case R.id.tv_mask:
                hideSelectPic();
                break;
            case R.id.tv_choose_from_gallery:
                if (ContextCompat.checkSelfPermission(PersonalDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PersonalDetailsActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
                break;
            case R.id.tv_take_photo:
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(PersonalDetailsActivity.this, "com.fadecolor.esport.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                try {
                    imagePath = outputImage.getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = new Intent("android.media.action.IMAGE_CAPTURE");
                in.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(in, TAKE_PHOTO);
                break;
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private String getImagePath(Uri externalContentUri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(externalContentUri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void showSelectPic() {
        TranslateAnimation animation = new TranslateAnimation(0,0,yOffset,0);
        animation.setDuration(200);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(200);
        mPicSelect.setAnimation(animation);
        mTvMask.setAnimation(alphaAnimation);
        mPicSelect.setVisibility(View.VISIBLE);
        mTvMask.setVisibility(View.VISIBLE);
    }

    private void hideSelectPic() {
        TranslateAnimation animation = new TranslateAnimation(0,0,0,yOffset);
        animation.setDuration(200);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1,0);
        alphaAnimation.setDuration(200);
        mPicSelect.setAnimation(animation);
        mTvMask.setAnimation(alphaAnimation);
        mPicSelect.setVisibility(View.GONE);
        mTvMask.setVisibility(View.GONE);
    }

    private float getYOffset() {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getApplicationContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getApplicationContext().getResources().getDisplayMetrics());
        }
        return getResources().getDimension(R.dimen.one_dip) + actionBarHeight*3;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "您拒绝了权限!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    uploadHeadImg();
                    hideSelectPic();
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if (DocumentsContract.isDocumentUri(this, uri)) {
                        // 如果是document类型的uri，则通过document id处理
                        String docId = DocumentsContract.getDocumentId(uri);
                        if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                            String id = docId.split(":")[1]; // 解析出数字格式的id
                            String selection = MediaStore.Images.Media._ID + "=" +id;
                            imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                        } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                            imagePath = getImagePath(contentUri, null);
                        }
                    } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                        // 如果是content类型的uri，则使用普通方式处理
                        imagePath = getImagePath(uri, null);
                    } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                        // 如果是file类型的uri，直接获取图片路径即可
                        imagePath = uri.getPath();
                    }
                    if (imagePath != null) {
                        uploadHeadImg();
                        hideSelectPic();
                    } else {
                        Toast.makeText(this, "读取图片失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void uploadHeadImg() {
        final SVProgressHUD progressHUD = new SVProgressHUD(PersonalDetailsActivity.this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressHUD.showWithStatus("正在上传头像",SVProgressHUD.SVProgressHUDMaskType.Black);
            }
        });
        HttpUtil.uploadImage(Constant.UPLOAD_HEAD_PATH, imagePath, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressHUD.dismissImmediately();
                        new SVProgressHUD(PersonalDetailsActivity.this).showInfoWithStatus("发生错误");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String imageSrc = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressHUD.dismissImmediately();
                    }
                });
                HttpUtil.sendOkHttpRequest(Constant.SEVER_ADDRESS + "/user/update?tel="+
                                user.getTel()+"&head_path="+imageSrc,
                        new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        new SVProgressHUD(PersonalDetailsActivity.this).showSuccessWithStatus("头像更新失败", SVProgressHUD.SVProgressHUDMaskType.Black);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            int statusCode = jsonObject.getInt("code");
                            if (statusCode == 100) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        intent.putExtra("headPath",imageSrc);
                                        PersonalDetailsActivity.this.setResult(1,intent);
                                        Glide.with(PersonalDetailsActivity.this).load(Constant.HEAD_PATH + imageSrc).into(mIvHead);
                                        new SVProgressHUD(PersonalDetailsActivity.this).showSuccessWithStatus("头像更新成功", SVProgressHUD.SVProgressHUDMaskType.Black);
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new SVProgressHUD(PersonalDetailsActivity.this).showErrorWithStatus("头像更新失败", SVProgressHUD.SVProgressHUDMaskType.Black);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new SVProgressHUD(PersonalDetailsActivity.this).showErrorWithStatus("请求失败", SVProgressHUD.SVProgressHUDMaskType.Black);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

}
