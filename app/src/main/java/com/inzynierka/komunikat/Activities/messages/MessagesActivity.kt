package com.inzynierka.komunikat.Activities.messages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inzynierka.komunikat.databinding.ActivityMessegesBinding

class MessagesActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMessegesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessegesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Do kogo piszemy?"
        //TODO: aktywność będzie do wyjebania. Pamiętaj żeby posprzątać layout
    }
}