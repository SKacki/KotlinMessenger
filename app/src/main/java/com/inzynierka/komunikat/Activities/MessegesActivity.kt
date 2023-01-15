package com.inzynierka.komunikat.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.inzynierka.komunikat.R

class MessegesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messeges)

        supportActionBar?.title = "Do kogo piszemy?"
    }
}