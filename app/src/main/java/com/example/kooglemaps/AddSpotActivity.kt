package com.example.kooglemaps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kooglemaps.databinding.ActivityAddSpotBinding
import com.example.kooglemaps.databinding.ActivitySpotBinding

// spot 추가하는 작업 수행
class AddSpotActivity: AppCompatActivity() {
    lateinit var binding: ActivityAddSpotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddSpotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout() {
        binding.apply {

        }
    }
}
