package com.info85.aironmusic

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.info85.aironmusic.databinding.ActivityMainBinding
import com.info85.aironmusic.util.StoryPrefs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPlay.setOnClickListener {
            if (StoryPrefs.isIntroShown(this)) {
                startActivity(Intent(this, MapSelectActivity::class.java))
            } else {
                startActivity(Intent(this, StoryIntroActivity::class.java))
            }
        }
    }
}
