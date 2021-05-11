package com.example.myexcellentbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton reg_block;
    private LinearLayout sv;
    private LinearLayout show_video;
    private EditText EmailEdit, PasswordEdit, RegisterPassword, RepeatPassword, RegisterMail;
    private Button EntryButton, ExitButton, CommitButton;
    private FirebaseAuth mAuth;
    private Context context;
    private String passwordIn, passwordInRepeat, mailIn;

    /* Программные методы */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // делаем полноэкранное
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.login_activity);

        init();
        startBackground();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.EntryButton:
                String EmailText = EmailEdit.getText().toString();
                String PasswordText = PasswordEdit.getText().toString();
                if (!EmailText.equals("") && !PasswordText.equals("")) {
                    mAuth.signInWithEmailAndPassword(EmailText, PasswordText).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(context, SelectActivity.class);
                                startActivity(intent);
                                setMassage("User sign in successful!");
                            } else {
                                setMassage("User sign in failed!");
                            }
                        }
                    });
                } else {
                    setMassage("Please enter email and password!");
                }
                break;
            case R.id.ExitButton:
                finish();
                break;
            case R.id.reg_block:
                if (sv.getVisibility() == View.GONE) {
                    sv.setVisibility(View.VISIBLE);
                    final Animation show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_video);
                    show_video.startAnimation(show);
                } else if (sv.getVisibility() == View.VISIBLE) {
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
                break;
            case R.id.CommitButton:
                if (sv.getVisibility() == View.VISIBLE) {
                    passwordIn = RegisterPassword.getText().toString();
                    passwordInRepeat = RepeatPassword.getText().toString();
                    mailIn = RegisterMail.getText().toString();
                    if (!passwordIn.equals("") && !passwordInRepeat.equals("") && !mailIn.equals("")) {
                        if (!passwordIn.equals(passwordInRepeat)){
                            setMassage("Passwords are different, please try again!");
                        }
                        else {
                            if (passwordIn.length() < 8) {
                                setMassage("Weak password, please try again!");
                            }
                            else {
                                mAuth.createUserWithEmailAndPassword(mailIn, passwordIn).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            setMassage("User sign up successful!");
                                        }
                                        else {
                                            setMassage("User sign up failed!");
                                        }
                                    }
                                });
                            }
                        }
                    }
                    else {
                        setMassage("Please enter your password!");
                    }
                }
                break;
        }
    }

    /* Мои функции{ */

    private void init() {

        /* Блок входа */
        context = this.getApplicationContext();
        show_video = (LinearLayout) findViewById(R.id.show_register);
        reg_block = (ImageButton) findViewById(R.id.reg_block);
        sv = (LinearLayout) findViewById(R.id.camera_view);
        EmailEdit = (EditText) findViewById(R.id.EmailEdit);
        PasswordEdit = (EditText) findViewById(R.id.PasswordEdit);

        EntryButton = (Button) findViewById(R.id.EntryButton);
        EntryButton.setOnClickListener(this);

        ExitButton = (Button) findViewById(R.id.ExitButton);
        ExitButton.setOnClickListener(this);

        reg_block = (ImageButton) findViewById(R.id.reg_block);
        reg_block.setOnClickListener(this);

        /* Блок регистрации */
        RegisterMail = (EditText) findViewById(R.id.RegisterMail);
        RegisterPassword = (EditText) findViewById(R.id.RegisterPassword);
        RepeatPassword = (EditText) findViewById(R.id.RepeatPassword);

        CommitButton = (Button) findViewById(R.id.CommitButton);
        CommitButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    public void setMassage(String reason) {
        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
    }

    private void startBackground() {
        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }
}