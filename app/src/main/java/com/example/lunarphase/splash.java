package com.example.lunarphase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class splash extends AppCompatActivity {


    TextView txtTitle;
    Animation fadeIn;
    private Handler handler = new Handler();
    private Runnable runnable;
    View main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        main = findViewById(R.id.main);
        txtTitle = findViewById(R.id.txtTitle);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in);
        txtTitle.startAnimation(fadeIn);
        txtTitle.setVisibility(View.VISIBLE);

        runnable = new Runnable() {
            @Override
            public void run() {
                Animation fadeOut = AnimationUtils.loadAnimation(splash.this, R.anim.splash_fade_out);
                main.startAnimation(fadeOut);

                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(splash.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        };
        handler.postDelayed(runnable ,3500);
        }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
