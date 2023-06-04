package com.example.kooglemaps

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
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
            goBackBtn.setOnClickListener {
                /* 뒤로가기 버튼 클릭 시 이벤트 처리 */
                // 이전 화면(구글 맵 화면)으로 이동
                /* 임시로 로그인 화면으로 이동하는 걸로 구현 */
                val intent = Intent(this@AddSpotActivity, MainActivity::class.java)
                startActivity(intent)
            }

            addBtn.setOnClickListener { 
                /* 추가 버튼 클릭 시 이벤트 처리 */
                // 스팟 이름 입력했는지 확인
                if(spotName.text.toString().equals("")||spotName.text.toString()==null
                    ||spotName.text.toString().replace(" ", "").equals("")){
                    Toast.makeText(this@AddSpotActivity,
                        "스팟 이름은 필수로 입력해야 합니다!", Toast.LENGTH_SHORT).show()
                }
                // 지도 위치 선택했는지 확인
                //else if(){
                // }

                // 스팟 설명 입력했는지 확인
                else if(spotDescription.text.toString().equals("")||spotDescription.text.toString()==null
                    ||spotDescription.text.toString().replace(" ", "").equals("")){
                    Toast.makeText(this@AddSpotActivity,
                        "스팟 설명은 필수로 입력해야 합니다!", Toast.LENGTH_SHORT).show()
                }

                // 전부 다 입력 됐으면 DB로 정보 넘기고 지도 화면으로 복귀
                else{
                    val DBcontroller = dbController()
                    DBcontroller.setData(
                        spotData(spotName.text.toString(), 1.0, 2.0,
                        spotDescription.text.toString(), null, null, null )
                    )

                    val intent = Intent(this@AddSpotActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }


            /* 사용자 입력에 따른 처리 */
            var txt = ""

            // spot name : 최대 15자
            // 15자 이상이면 경고 알림
            spotName.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) {
                    txt = s.toString()
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) {
                    if(spotName.length() > 15){
                        nametxtCount.setTextColor(Color.RED)
                        Toast.makeText(this@AddSpotActivity,
                            "스팟 이름은 최대 15자까지 입력 가능합니다!", Toast.LENGTH_SHORT).show()
                        spotName.setText(txt)
                        spotName.setSelection(spotName.length())
                        nametxtCount.setText("${spotName.length()} / 15")
                    }
                    else if(spotName.length() < 15){
                        nametxtCount.setTextColor(Color.GRAY)
                        nametxtCount.setText("${spotName.length()} / 15")
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            // spot description : 최대 150자 & 3줄
            // 150자 이상이면 경고 알림
            spotDescription.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) {
                    txt = s.toString()
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) {
                    if(spotDescription.length() > 150){
                        descriptiontxtCount.setTextColor(Color.RED)
                        Toast.makeText(this@AddSpotActivity,
                            "스팟 설명은 최대 150자까지 입력 가능합니다!", Toast.LENGTH_SHORT).show()
                        spotDescription.setText(txt)
                        spotDescription.setSelection(spotDescription.length())
                        descriptiontxtCount.setText("${spotDescription.length()} / 150")
                    }
                    else if(spotDescription.length() < 150){
                        descriptiontxtCount.setTextColor(Color.GRAY)
                        descriptiontxtCount.setText("${spotDescription.length()} / 150")
                    }

                    if(spotDescription.lineCount > 5){
                        Toast.makeText(this@AddSpotActivity,
                            "스팟 설명은 최대 5줄까지 입력 가능합니다!", Toast.LENGTH_SHORT).show()
                        spotDescription.setText(txt)
                        spotDescription.setSelection(spotDescription.length())
                        descriptiontxtCount.setText("${spotDescription.length()} / 150")
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
        }
    }
}
