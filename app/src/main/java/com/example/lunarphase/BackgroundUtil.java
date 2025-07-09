package com.example.lunarphase;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class BackgroundUtil {
    public static void applyBackground(Context context, View rootView, VideoView videoView) {
        SharedPreferences prefs = context.getSharedPreferences("BackgroundPrefs", Context.MODE_PRIVATE);
        String bgType = prefs.getString("backgroundType", "video");
        String uriString = prefs.getString("galleryImageUri", null);

        if (bgType.equals("video")) {
            videoView.setVisibility(View.VISIBLE);
            Uri videoUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.bg_back);
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                mp.setVolume(0f, 0f);
                videoView.start();
            });
        } else {
            videoView.setVisibility(View.GONE);
            switch (bgType) {
                case "static1": rootView.setBackgroundResource(R.drawable.static_bg_1); break;
                case "static2": rootView.setBackgroundResource(R.drawable.static_bg_2); break;
                case "static3": rootView.setBackgroundResource(R.drawable.static_bg_3); break;
                case "static4": rootView.setBackgroundResource(R.drawable.static_bg_4); break;
                case "gallery":
                    if (uriString != null) {
                        Uri imageUri = Uri.parse(uriString);
                        Glide.with(context)
                                .load(imageUri)
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        rootView.setBackground(resource);
                                    }
                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                                });
                    }
                    break;
            }
        }
    }
}
