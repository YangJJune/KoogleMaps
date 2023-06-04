package com.example.kooglemaps

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kooglemaps.databinding.ActivityRegisterBinding
import com.example.kooglemaps.databinding.ActivitySpotBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// spot icon 클릭 시 해당 spot 정보 띄우는 작업 수행
class SpotActivity: AppCompatActivity() {
    lateinit var binding: ActivitySpotBinding
    var favoriteColor = "gray"
    val DBcontroller = dbController()
    var curSpotData = spotData()
    var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySpotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
        initEvent()
    }

    fun initLayout() {
        // intent를 통해 장소의 id 전달 받고,
        // 해당 장소의 DB정보 불러와서 화면에 띄우는 작업 수행
        CoroutineScope(Dispatchers.IO).launch {
            curSpotData = DBcontroller.getData("test")
        }

        // 현재 로그인한 user 정보 불러옴 => 좋아요 표시에 이용
        uid = intent.getStringExtra("uid").toString()
        Toast.makeText(this@SpotActivity, uid, Toast.LENGTH_SHORT).show()

        // spotDB의 좋아요 누른 uid 리스트에 현재 로그인 된 user의 id 있는지 탐색
        var like = curSpotData.likeUser!!.contains(uid)
        var likeCount = curSpotData.likeUser!!.size

        // 있으면 빨간 하트로 설정 & 없으면 회색으로 설정
        binding.apply {
            spotName.setText(curSpotData.title)
            spotDescription.setText(curSpotData.desc)
            if(like){
                favoriteBtn.setImageResource(R.drawable.baseline_favorite_24_red)
                favoriteColor = "red"
            }
            else{
                favoriteBtn.setImageResource(R.drawable.baseline_favorite_24)
                favoriteColor = "gray"
            }
            favoriteNum.setText(likeCount)
        }
    }

    fun initEvent(){
        binding.apply {
            goBackBtn.setOnClickListener {
                // 뒤로가기 버튼 클릭 시 이벤트 처리
                // 이전 화면(구글 맵 화면)으로 이동
                /* 임시로 로그인 화면으로 이동하는 걸로 구현 */
                val intent = Intent(this@SpotActivity, MainActivity::class.java)
                startActivity(intent)
            }

            favoriteBtn.setOnClickListener {
                // 좋아요 버튼 클릭 시 이벤트 처리
                // 누르지 않은 상태 : image source를 red로 변경하고, 숫자 증가
                // 이미 누른 상태 : image source를 기본으로 변경하고, 숫자 감소

                var favoriteCount = favoriteNum.text.toString().toInt()

                if(favoriteColor == "gray") {
                    // 누르지 않은 상태에서 클릭 (gray=>red)
                    favoriteBtn.setImageResource(R.drawable.baseline_favorite_24_red)
                    favoriteNum.setText("${++favoriteCount}")
                    favoriteColor = "red"

                    /* SpotDB의 좋아요 수 및 좋아요 누른 uid 리스트 수정 */
                    if(curSpotData.likeUser!!.contains(uid)){
                        curSpotData.likeUser!!.remove(uid)
                    }

                }
                else if(favoriteColor == "red"){
                    // 누른 상태에서 클릭 (red=>gray)
                    favoriteBtn.setImageResource(R.drawable.baseline_favorite_24)
                    favoriteNum.setText("${--favoriteCount}")
                    favoriteColor = "gray"

                    /* SpotDB의 좋아요 수 및 좋아요 누른 uid 리스트 수정 */
                    if(!curSpotData.likeUser!!.contains(uid)){
                        curSpotData.likeUser!!.add(uid)
                    }
                }
            }
        }
    }
}