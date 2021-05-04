package com.example.myexcellentbooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class LoginActivity extends AppCompatActivity {

    ImageButton reg_block;
    SurfaceView sv;
    LinearLayout show_video;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // делаем полноэкранное
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        setContentView(R.layout.anim_footer);

        show_video = (LinearLayout) findViewById(R.id.show_register);
        reg_block = (ImageButton) findViewById(R.id.reg_block);
        sv = (SurfaceView) findViewById(R.id.camera_view);

        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        reg_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sv.getVisibility() == View.GONE) {
                    sv.setVisibility(View.VISIBLE);
                    final Animation show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_video);
                    show_video.startAnimation(show);
                }
                else if (sv.getVisibility() == View.VISIBLE) {
                    final Animation hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_video);
                    hide.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            sv.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    show_video.startAnimation(hide);
                }
            }
        });

    }
}