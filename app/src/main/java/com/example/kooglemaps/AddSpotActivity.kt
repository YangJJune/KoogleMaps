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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

// spot 추가하는 작업 수행
class AddSpotActivity: AppCompatActivity() {
    lateinit var binding: ActivityAddSpotBinding
    lateinit var googleMap : GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddSpotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
        initmap()
    }

    private fun initLayout() {
        binding.apply {
            goBackBtn.setOnClickListener {
                /* 뒤로가기 버튼 클릭 시 이벤트 처리 */
                // 이전 화면(구글 맵 화면)으로 이동
                /* 임시로 로그인 화면으로 이동하는 걸로 구현 */
                val intent = Intent(this@AddSpotActivity, MapActivity::class.java)
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

                    val intent = Intent(this@AddSpotActivity, MapActivity::class.java)
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

    private fun initmap() {
        val loc = intent.getStringExtra("loc")
        //lat/lng: (37.542402,127.076903) 로 나온다.

        val regex = Regex("""\d+\.\d+""")
        val matches = regex.findAll(loc!!)
        val numbers = matches.map { it.value }.toList()

        var x :Double = 0.0
        var y :Double = 0.0

        if (numbers.size == 2) {
            x= numbers[0].toDouble()
            y= numbers[1].toDouble()

            // x와 y 변수 사용 예시
            println("x: $x")
            println("y: $y")
        } else {
            println("좌표를 찾을 수 없습니다.")
        }
        //여기까지 클릭한 마커의 좌표 가져오기

        //초기화 작업 -> 구글맵 초기화는 map fragment를 통해서 가능하므로 접근
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_add_spot)
                as SupportMapFragment//map fragment로 변환
        mapFragment.getMapAsync{
            googleMap = it
            //구글맵 객체 로딩 완료되면 카메라 이동!
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(x, y),18.0f))
            //zoom level 제한하기
            googleMap.setMinZoomPreference(18.0f)//zoom의 최소 레벨 설정
            googleMap.setMaxZoomPreference(18.0f)//zoom 최대 레벨 설정

            //val konkuk_loc = LatLng(37.542402, 127.076903)
            val bounds = LatLngBounds.Builder()
                .include(LatLng(x, y)) // 좌상단 좌표
                .include(LatLng(x, y)) // 우하단 좌표
                .build()

            //맵 마커
            val option = MarkerOptions()
            option.position(LatLng(x, y))
            option.icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
            option.title("스팟 추가 위치")//마커의 윗쪽 큰글씨
            googleMap.addMarker(option)?.showInfoWindow()

            googleMap.setOnMapClickListener { latLng ->
                // 클릭한 위치에 마커를 추가합니다.
                googleMap.addMarker(MarkerOptions().position(latLng).title("Clicked Marker"))
            }

            googleMap.setLatLngBoundsForCameraTarget(bounds)
        }

    }
}