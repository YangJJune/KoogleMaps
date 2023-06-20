package com.example.kooglemaps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kooglemaps.databinding.ActivityRegisterBinding
import com.example.kooglemaps.databinding.ActivitySpotBinding

// spot icon 클릭 시 해당 spot 정보 띄우는 작업 수행
class SpotActivity: AppCompatActivity() {
    lateinit var binding: ActivitySpotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySpotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout(){
        binding.apply {
            goBackBtn.setOnClickListener {
                // 뒤로가기 버튼 클릭 시 이벤트 처리
            }

            favoriteBtn.setOnClickListener {
                // 좋아요 버튼 클릭 시 이벤트 처리
                // 누르지 않은 상태 : image source를 red로 변경하고, 숫자 증가
                // 이미 누른 상태 : image source를 기본으로 변경하고, 숫자 감소

            }
        }
    }
}