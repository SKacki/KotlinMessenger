package com.inzynierka.komunikat.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inzynierka.komunikat.R

class MessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messeges)

        supportActionBar?.title = "Do kogo piszemy?"
        //TODO: aktywność będzie do wyjebania. Pamiętaj żeby posprzątać layout
    }
}