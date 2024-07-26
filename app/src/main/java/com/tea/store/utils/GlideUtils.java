package com.tea.store.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class GlideUtils {
    public static void bind(Context context, ImageView view, Object load) {
        Glide.with(context.getApplicationContext())
                .load(load)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.with(new DrawableCrossFadeFactory.Builder(250).setCrossFadeEnabled(true).build()))
                .into(view);
    }

    public static void bind(Context context, ImageView view, Object load, int duration) {
        Glide.with(context.getApplicationContext())
                .load(load)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.with(new DrawableCrossFadeFactory.Builder(duration).setCrossFadeEnabled(true).build()))
                .into(view);
    }

    public static void bindCrop(Context context, ImageView view, Object load) {
        Glide.with(context.getApplicationContext())
                .load(load)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .transition(DrawableTransitionOptions.with(new DrawableCrossFadeFactory.Builder(250).setCrossFadeEnabled(true).build()))
                .into(view);
    }

    public static void bindBlur(Context context, ImageView view, Object load, int radius, int sampling) {
        Glide.with(context.getApplicationContext())
                .load(load)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(640, 480)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(radius, sampling)))
                .into(view);
    }

    public static void bindBlur(Context context, ImageView view, Object load) {
        bindBlur(context, view, load, null, 25, 8);
    }

    public static void bindBlur(Context context, ImageView view, Object load, Drawable placeholder, int radius, int sampling) {
        Glide.with(context.getApplicationContext())
                .load(load)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(placeholder)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(radius, sampling)))
                .into(view);
    }

    public static void bindBlurCross(Context context, ImageView view, Object load, int duration) {
        Glide.with(context.getApplicationContext())
                .load(load)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 8)))
                .transition(DrawableTransitionOptions.with(new DrawableCrossFadeFactory.Builder(duration).setCrossFadeEnabled(true).build()))
                .into(view);
    }
}
