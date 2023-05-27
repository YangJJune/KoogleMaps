package com.example.kooglemaps2

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.example.kooglemaps2.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(){
    lateinit var binding :ActivityMainBinding
    lateinit var googleMap: GoogleMap

    val konkuk_loc = LatLng(37.542402, 127.076903)
    
    //사용자의 현재위치 사용하기
    var user_loc = konkuk_loc
    //lateinit var fusedLocationProviderCilent = FusedLocationProviderClient


    //구글맵에 폴리라인 그리기 -> 위도경도 좌표의 배열이 필요하다
    val arrLoc = ArrayList<LatLng>()

    val textarr = arrayListOf<String>("이미지", "리스트", "팀소개")
    val imgarr = arrayListOf<Int>(R.drawable.baseline_location_on_24,
        R.drawable.baseline_add_location_alt_24, R.drawable.baseline_people_24)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
        //initmap()
        //initspinner()
    }

    private fun initLayout() {
        binding.viewpager.adapter = MyViewPagerAdapter(this)
        //main activity도 FragmentActivity에서의 파생이므로 main == this 를 인자로 넣을 수 있다.

        //5. tab 과 viewPager를 중재할 mediator 생성이 필요
        TabLayoutMediator(binding.tablayout, binding.viewpager){

                tab, pos -> //tab과 position정보
            tab.text = textarr[pos]

            //7.만든 이미지 를 pos 따라 붙이기
            tab.setIcon(imgarr[pos])

        }.attach()//attach까지 해야 붙는다

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

            //initUserLocation()




            //맵 마커
            val option = MarkerOptions()
            option.position(konkuk_loc)
            option.icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )
            option.title("역")//마커의 윗쪽 큰글씨
            option.snippet("서울역")//마커의 아랫쪽 작은글씨
            googleMap.addMarker(option)?.showInfoWindow()

            //맵에서 클릭한 부분의 위도,경도를 가지고 오는 이벤트처리
            googleMap.setOnMapClickListener {
                arrLoc.add(it)//클릭한 지점의 위도 경도를 배열에 추가한다

                //맵에 그려진 폴리곤, 폴리라인 지우는 함수 googleMap.clear()
                googleMap.clear()

                //클릭한 지점에 마커 찍기
                val clickPoint = MarkerOptions()
                clickPoint.position(it)
                clickPoint.icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
                clickPoint.title("지금")//마커의 윗쪽 큰글씨
                clickPoint.snippet("찍은곳")//마커의 아랫쪽 작은글씨
                googleMap.addMarker(clickPoint)


                //폴리라인을 그리려면 폴리라인 옵션 개체가 필요
                //val polyOp = PolylineOptions().color(Color.GREEN).addAll(arrLoc)
                //googleMap.addPolyline(polyOp)

                //폴리곤은 그리려면(면 포함) 옵션 개체 필요
                val polygonOp = PolygonOptions().fillColor(
                    Color.argb(142,255,211, 100))//첫번째 인자 == 투명도, 면생상
                    .strokeColor(Color.GREEN).addAll(arrLoc)//선의 색깔 설정
                googleMap.addPolygon(polygonOp)

                
            }
        }

    }
    */




    /*
    private fun initUserLocation() {
        fusedLocationProviderCilent = LocationServices.getFusedLocationProviderClient(
            this, getLastLocation()
        )
    }

    private fun getLastLocation() {
        when{
            checkFineLocationPermission()->{
                if(checkGPSProvider()){
                    showGPSSetting()
                }
            }
        }
    }
    */

}