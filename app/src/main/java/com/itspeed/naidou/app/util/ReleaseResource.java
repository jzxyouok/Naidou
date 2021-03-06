package com.itspeed.naidou.app.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by jafir on 15/7/8.
 * 释放资源类
 *
 */
public class ReleaseResource {
    public static void releaseImageViewResouce(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }


    public static void recyclerImg(ImageView ...views){
        for(ImageView imageView:views){
            if(imageView == null){
                continue;
            }
            Drawable drawable = imageView.getDrawable();
            if(drawable!=null) {
                drawable.setCallback(null);
            }
            imageView.setImageBitmap(null);
            imageView.setBackground(null);
        }
    }
}
