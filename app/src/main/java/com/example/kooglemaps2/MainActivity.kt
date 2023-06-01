package com.example.kooglemaps2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.kooglemaps2.R
import com.example.kooglemaps2.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
//
class MainActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener{
    lateinit var binding : ActivityMainBinding
    lateinit var googleMap: GoogleMap

    private var clickedMarker: Marker ?= null

    val loc = LatLng(37.554752, 126.970631)//서울역 좌표
    val konkuk_loc = LatLng(37.542402, 127.076903)

    //구글맵에 폴리라인 그리기 -> 위도경도 좌표의 배열이 필요하다
    val arrLoc = ArrayList<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initmap()

        //initspinner()

        initLayout()
    }

    private fun initLayout() {
        binding.btnFab.setOnClickListener {

        }
    }


    /*
    private fun initspinner() {
        //스피너 초기화
        //스피너는 어댑터가 필요한 유닛이므로 어댑터 먼저 만든다.
        val adapter = ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_dropdown_item, ArrayList<String>()
        )
        //adapter에 data 넣기
        adapter.add("Hybird")//map type유형
        adapter.add("Normal")
        adapter.add("Satellite")
        adapter.add("Terrian")

        binding.apply {
            spinner.adapter = adapter//어댑터 부착
            
            spinner.setSelection(1) //map type의 디폴트를 normal로 설정

            //스피너 이벤트 처리 -> 맵 타입 바꾸도록
            spinner.onItemSelectedListener = object : OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    //선택했을 때 이벤트 처리
                    when(position){
                        0->{
                            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                        }
                        1->{
                            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                        }
                        2->{
                            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        }
                        3->{
                            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                   //선택안했을 때
                }

            }
        }
    }



     */
    private fun initmap() {
        //초기화 작업 -> 구글맵 초기화는 map fragment를 통해서 가능하므로 접근
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map)
        as SupportMapFragment//map fragment로 변환

        //mapFragment.getMapAsync(this)//초기화 방법 1 인자로 onmapreadycallback 인터페이스==this
        mapFragment.getMapAsync{
            googleMap = it//초기화 방법 2 어차피 인터페이스 메소드 1개뿐이라 간단

            //구글맵 객체 로딩 완료되면 카메라 이동!
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(konkuk_loc,18.0f))
            //zoom level 제한하기
            googleMap.setMinZoomPreference(16.0f)//zoom의 최소 레벨 설정
            googleMap.setMaxZoomPreference(22.0f)//zoom 최대 레벨 설정

            //val konkuk_loc = LatLng(37.542402, 127.076903)
            val bounds = LatLngBounds.Builder()
                .include(LatLng(37.547660, 127.074053)) // 좌상단 좌표
                .include(LatLng(37.538032, 127.081679)) // 우하단 좌표
                .build()


            googleMap.setLatLngBoundsForCameraTarget(bounds)
            /*
            //사용자가 학교 근처를 넘어서려고 할 때, 다시 돌아오도록 하는 코드
            googleMap.setOnCameraMoveListener {
                val cameraBounds = googleMap.projection.visibleRegion.latLngBounds
                if (!bounds.contains(cameraBounds.center)) {
                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0)
                    googleMap.moveCamera(cameraUpdate)
                }
            }
            */


            //맵 마커
            val option = MarkerOptions()
            option.position(konkuk_loc)
            option.icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )
            option.title("청심대")//마커의 윗쪽 큰글씨
            option.snippet("그늘 굿")//마커의 아랫쪽 작은글씨
            googleMap.addMarker(option)?.showInfoWindow()

            googleMap.setOnMapClickListener { latLng ->
                // 클릭한 위치에 마커를 추가합니다.
                googleMap.addMarker(MarkerOptions().position(latLng).title("Clicked Marker"))
            }

            googleMap.setOnMarkerClickListener(this)
            googleMap.setOnMapClickListener(this)

        }

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker.title == "Clicked Marker") {
            showInputDialog(marker)
        }
        return false
    }
    private fun showInputDialog(marker: Marker) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("마커 제목 입력")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("확인") { _, _ ->
            val title = input.text.toString().trim()
            if (title.isNotEmpty()) {
                marker?.title = title
            } else {
                marker?.remove()
            }
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            marker?.remove()
            dialog.dismiss()
        }

        builder.show()
    }

    override fun onMapClick(p0: LatLng) {
        // 기존 마커가 있는 경우 제거합니다.
        //clickedMarker?.remove()

        // 마커를 추가합니다.
        clickedMarker = googleMap.addMarker(MarkerOptions().position(p0).title("Click to set title"))

        // 사용자로부터 제목을 입력받는 다이얼로그를 표시합니다.
        showInputDialog(clickedMarker!!)
    }

}