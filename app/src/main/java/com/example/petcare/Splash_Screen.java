package com.example.petcare;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        ImageView imageView=findViewById(R.id.splash_logo);

        Animation animation= AnimationUtils.loadAnimation(this,R.anim.splash_animation);
        imageView.setAnimation(animation);

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    Intent intent=new Intent(Splash_Screen.this, Start_Page.class);
                    startActivity(intent);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}