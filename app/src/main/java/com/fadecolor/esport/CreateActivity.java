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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.Util.HttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateActivity extends BaseActivity implements View.OnClickListener {

    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;

    private TextView mTvSubmit, mTvPosition, mTvMask, mTvChooseFromGallery, mTvTakePhoto, mTvCancelPic, mTvDeletePic;

    private ImageView mIvBack, mIvAddPic;

    private EditText mEtContent;

    private LinearLayout mPicSelect;

    private float yOffset;

    private Uri imageUri;

    private boolean isHavePic = false;

    private String imagePath = null;
    private Intent intent;
private int t = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        LinearLayout linearLayout = findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(), statusBarHeight, linearLayout.getPaddingEnd(), linearLayout.getPaddingBottom());
        yOffset = getYOffset();

        mIvBack = findViewById(R.id.iv_back);
        mIvAddPic = findViewById(R.id.iv_add_picture);
        mEtContent = findViewById(R.id.et_comment);
        mTvSubmit = findViewById(R.id.tv_submit);
        mTvPosition = findViewById(R.id.tv_position);
        mTvMask = findViewById(R.id.tv_mask);
        mTvChooseFromGallery = findViewById(R.id.tv_choose_from_gallery);
        mTvTakePhoto = findViewById(R.id.tv_take_photo);
        mTvCancelPic = findViewById(R.id.tv_cancel_pic);
        mTvDeletePic = findViewById(R.id.tv_delete_pic);
        mPicSelect = findViewById(R.id.pic_select);

        mIvBack.setOnClickListener(this);
        mTvSubmit.setOnClickListener(this);
        mIvAddPic.setOnClickListener(this);
        mTvPosition.setOnClickListener(this);
        mTvMask.setOnClickListener(this);
        mTvChooseFromGallery.setOnClickListener(this);
        mTvTakePhoto.setOnClickListener(this);
        mTvDeletePic.setOnClickListener(this);
        mTvCancelPic.setOnClickListener(this);
        intent = getIntent();
        String i = intent.getStringExtra("data");
        if(null != i) mTvPosition.setText(i);
        else  mTvPosition.setText("获取位置");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back:
                setResult(0);
                finish();
                break;
            case R.id.iv_add_picture:
                showSelectPic(view);
                break;
            case R.id.tv_submit:
                if (isHavePic) {
                    final SVProgressHUD progressHUD = new SVProgressHUD(CreateActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressHUD.showWithStatus("正在上传图片", SVProgressHUD.SVProgressHUDMaskType.Black);
                        }
                    });
                    HttpUtil.uploadImage(Constant.UPLOAD_ACTIVITY_PATH, imagePath, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressHUD.dismissImmediately();
                                    new SVProgressHUD(CreateActivity.this).showInfoWithStatus("发生错误");
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
                                    final SVProgressHUD progressHUD1 = new SVProgressHUD(CreateActivity.this);
                                    progressHUD1.showWithStatus("正在发表动态", SVProgressHUD.SVProgressHUDMaskType.Black);
                                    SharedPreferences prefs = getSharedPreferences("account", MODE_PRIVATE);
                                    String tel = prefs.getString("userId", null);
                                    HttpUtil.sendOkHttpRequest(Constant.SEVER_ADDRESS + "/activity/submit?tel=" + tel + "&content=" +
                                                    mEtContent.getText().toString() + "&image_src=" + imageSrc + "&location=" +
                                                    (mTvPosition.getText().toString().equals("获取位置") ? "null" : mTvPosition.getText().toString()),
                                            new Callback() {
                                                @Override
                                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressHUD1.dismissImmediately();
                                                            new SVProgressHUD(CreateActivity.this).showInfoWithStatus("发表失败");
                                                        }
                                                    });
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
                                                                    progressHUD1.dismissImmediately();
                                                                    new SVProgressHUD(CreateActivity.this).showSuccessWithStatus("发表成功", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                                    setResult(1);
                                                                    finish();
                                                                }
                                                            });
                                                        } else {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    progressHUD1.dismissImmediately();
                                                                    new SVProgressHUD(CreateActivity.this).showErrorWithStatus("发表失败", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                                }
                                                            });
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressHUD1.dismissImmediately();
                                                                new SVProgressHUD(CreateActivity.this).showErrorWithStatus("请求失败", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    });
                } else {
                    final SVProgressHUD progressHUD1 = new SVProgressHUD(CreateActivity.this);
                    progressHUD1.showWithStatus("正在发表动态", SVProgressHUD.SVProgressHUDMaskType.Black);
                    SharedPreferences prefs = getSharedPreferences("account", MODE_PRIVATE);
                    String tel = prefs.getString("userId", null);
                    HttpUtil.sendOkHttpRequest(Constant.SEVER_ADDRESS + "/activity/submit?tel=" + tel + "&content=" +
                                    mEtContent.getText().toString() + "&image_src=null&location=" +
                                    (mTvPosition.getText().toString().equals("获取位置") ? "null" : mTvPosition.getText().toString()),
                            new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressHUD1.dismissImmediately();
                                            new SVProgressHUD(CreateActivity.this).showInfoWithStatus("发表失败");
                                        }
                                    });
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
                                                    progressHUD1.dismissImmediately();
                                                    new SVProgressHUD(CreateActivity.this).showSuccessWithStatus("发表成功", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                    setResult(1);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressHUD1.dismissImmediately();
                                                    new SVProgressHUD(CreateActivity.this).showErrorWithStatus("发表失败", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                }
                                            });
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressHUD1.dismissImmediately();
                                                new SVProgressHUD(CreateActivity.this).showErrorWithStatus("请求失败", SVProgressHUD.SVProgressHUDMaskType.Black);
                                            }
                                        });
                                    }
                                }
                            });
                }

                break;
            case R.id.tv_position:
                    intent = new Intent(CreateActivity.this, LocationActivity.class);
                    startActivity(intent);
                break;
            case R.id.tv_cancel_pic:
            case R.id.tv_mask:
                hideSelectPic();
                break;
            case R.id.tv_choose_from_gallery:
                if (ContextCompat.checkSelfPermission(CreateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                    imageUri = FileProvider.getUriForFile(CreateActivity.this, "com.fadecolor.esport.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                try {
                    imagePath = outputImage.getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;
            case R.id.tv_delete_pic:
                mIvAddPic.setImageResource(R.drawable.ic_add_picture);
                isHavePic = false;
                mIvAddPic.setScaleType(ImageView.ScaleType.CENTER);
                hideSelectPic();
                break;
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
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
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        mIvAddPic.setImageBitmap(bitmap);
                        mIvAddPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        isHavePic = true;
                        hideSelectPic();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
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
                            String selection = MediaStore.Images.Media._ID + "=" + id;
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
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        mIvAddPic.setImageBitmap(bitmap);
                        mIvAddPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        isHavePic = true;
                        hideSelectPic();
                    } else {
                        Toast.makeText(this, "读取图片失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private void showSelectPic(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (isHavePic) {
            mTvDeletePic.setVisibility(View.VISIBLE);
            mTvTakePhoto.setBackgroundResource(R.drawable.item_board_buttom);
        } else {
            mTvDeletePic.setVisibility(View.GONE);
            mTvTakePhoto.setBackgroundResource(R.drawable.card_backgroud);
        }
        TranslateAnimation animation = new TranslateAnimation(0, 0, yOffset, 0);
        animation.setDuration(200);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(200);
        mPicSelect.setAnimation(animation);
        mTvMask.setAnimation(alphaAnimation);
        mPicSelect.setVisibility(View.VISIBLE);
        mTvMask.setVisibility(View.VISIBLE);
    }

    private void hideSelectPic() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, yOffset);
        animation.setDuration(200);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
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
        return getResources().getDimension(R.dimen.one_dip) + actionBarHeight * 3;
    }
}
