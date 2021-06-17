package com.edusoho.kuozhi.clean.widget.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.stat.StatService;

public class ESImageGetter implements Html.ImageGetter {

    public static final String QUESTION_CHOICE = "question_choice";

    private DisplayImageOptions mOptions;
    private Context             mContext;
    private TextView            mContainer;
    private String              mType;

    public ESImageGetter(Context context, TextView view) {
        this.mContainer = view;
        this.mContext = context;
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.default_error)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.NONE_SAFE)
                .build();
    }

    public void setType(String type) {
        mType = type;
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
            float maxWidth = mContainer.getWidth();

            int realWidth = loadedImage.getWidth();
            int realHeight = loadedImage.getHeight();

            float scale = createScale(maxWidth, loadedImage);
            float scaleY = scale;
            if (scaleY * realHeight > 8192) {
                scaleY = 8192.0f / realHeight;
            }
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scaleY);
            mDrawable.bitmap = Bitmap.createBitmap(loadedImage, 0, 0, realWidth, realHeight, matrix, true);
            realHeight = (int) (realHeight * scaleY);
            if (realHeight > 8192) {
                realHeight = 8192;
            }
            mDrawable.setBounds(0, 0, (int) (realWidth * scale), realHeight);
            mContainer.setText(mContainer.getText());
        }

        private float createScale(float maxWidth, Bitmap loadedImage) {
            float scale;
            if (QUESTION_CHOICE.equals(mType)) {
                int realWidth = loadedImage.getWidth();
                scale = (float) realWidth / maxWidth;
                if (scale < 0.5f) {
                    scale = (maxWidth / 2) / (float) realWidth;
                } else if (scale > 0.5f && scale < 1.0f) {
                    scale = 1.0f;
                } else if (scale > 1.0f) {
                    scale = (float) realWidth / maxWidth;
                }
            } else {
                scale = maxWidth / (float) loadedImage.getWidth();
            }
            return scale;
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
