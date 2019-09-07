package com.fadecolor.esport;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.youth.banner.loader.ImageLoader;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class GlideImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(28, 0, RoundedCornersTransformation.CornerType.ALL)))
                .into(imageView);
    }


}
