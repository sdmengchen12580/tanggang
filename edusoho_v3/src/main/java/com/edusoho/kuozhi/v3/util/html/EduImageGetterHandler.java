package com.edusoho.kuozhi.v3.util.html;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.stat.StatService;

/**
 * Created by howzhi on 14-10-29.
 */
public class EduImageGetterHandler implements Html.ImageGetter {

    private DisplayImageOptions mOptions;
    private Context             mContext;
    private TextView            mContainer;

    public EduImageGetterHandler(Context context, TextView view) {
        this.mContainer = view;
        this.mContext = context;
        mOptions = new DisplayImageOptions.Builder()
                .delayBeforeLoading(100)
                .showImageOnFail(R.drawable.default_error)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public Drawable getDrawable(String s) {
        CacheDrawable drawable = new CacheDrawable();
        try {
            ImageSize imageSize = new ImageSize(0, 0);
            NonViewAware nonViewAware = new NonViewAware(imageSize, ViewScaleType.FIT_INSIDE);
            ImageLoader.getInstance().displayImage(s, nonViewAware, mOptions, new CustomImageLoadingListener(drawable));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    private class CustomImageLoadingListener implements ImageLoadingListener {
        private CacheDrawable mDrawable;

        private CustomImageLoadingListener(CacheDrawable drawable) {
            this.mDrawable = drawable;
        }

        private void setBitmap(Bitmap loadedImage) {
            float showMaxWidth, showMaxHeight;
            showMaxWidth = EdusohoApp.screenW * 0.9f;
            showMaxHeight = EdusohoApp.screenH * 0.3f;

            int w = loadedImage.getWidth();
            int h = loadedImage.getHeight();
            if (w > h) {
                loadedImage = AppUtil.scaleImage(loadedImage, w > showMaxWidth ? showMaxWidth : w, 0);
            } else {
                loadedImage = AppUtil.scaleImage(loadedImage, h > showMaxHeight ? showMaxHeight : h, 0);
            }

            mDrawable.bitmap = loadedImage;
            mDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());
            mContainer.setText(mContainer.getText());
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            try {
                setBitmap(loadedImage);
            } catch (Exception ex) {
                StatService.reportException(mContext, new Exception(imageUri));
            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            Bitmap failBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.html_image_fail);
            mDrawable.bitmap = failBitmap;
            mDrawable.setBounds(0, 0, failBitmap.getWidth(), failBitmap.getHeight());
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }
    }

    private class CacheDrawable extends BitmapDrawable {
        public  Bitmap bitmap;
        private Bitmap loadBitmap;

        public CacheDrawable() {
            loadBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.html_image_loading);
            setBounds(0, 0, loadBitmap.getWidth(), loadBitmap.getHeight());
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (bitmap == null) {
                canvas.drawBitmap(loadBitmap, 0, 0, new Paint());
                return;
            }

            canvas.drawBitmap(bitmap, 0, 0, new Paint());
        }
    }
}
