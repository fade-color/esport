package com.fadecolor.esport;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.fadecolor.esport.Util.Constant;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        PhotoView photoView = findViewById(R.id.photo_view);
        Intent intent = getIntent();
        String imageSrc = intent.getStringExtra("imageSrc");
        Glide.with(this).load(Constant.ACTIVITY_IMG_PATH+imageSrc).into(photoView);
    }
}
