package com.example.presensiusingmap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {
//    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        binding = ActivityMainBinding.inflate(layoutInflater) // BINDING
        val buttPresensi = findViewById<Button>(R.id.butt_presensi)
        val buttLihatPresensi = findViewById<Button>(R.id.butt_lihatPresensi)
        buttPresensi.setOnClickListener(){
            val intent = Intent(this, CheckPresensi::class.java)
            startActivity(intent)
        }
        buttLihatPresensi.setOnClickListener(){
            val intent = Intent(this, LihatPresensi::class.java)
            startActivity(intent)
        }


//        setContentView(binding.root)
    }
}