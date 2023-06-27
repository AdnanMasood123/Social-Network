package com.example.socialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class BotSplashScreenActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_splash_screen);

        Thread th=new Thread()
        {
            @Override
            public void run()
            {
                super.run();

                try
                {
                   sleep(5000);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    Intent intent=new Intent(getApplicationContext(),ChatBotsActivity.class);
                    startActivity(intent);
                }

            }
        };
        th.start();
    }
}