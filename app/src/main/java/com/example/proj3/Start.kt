package com.example.proj3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager

class Start : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start)
        var hand = Handler();
        hand.postDelayed(Runnable() {
            run() {
                // TODO Auto-generated method stub
                 var intent = Intent(this, SelectDevice::class.java)
                 startActivity(intent);
                 finish();
            }
        }, 2000)

    }

}
