package com.example.a7minutesworkoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // setSupportActionBar(toolbar)
        //tvTimer.text = "${(timerDuration/1000).toString()}"
        //btnStart.setOnClickListener
        llStart.setOnClickListener {
            val intent = Intent(this, ExerciseActivity :: class.java)
            startActivity(intent)
        }
    }

}
