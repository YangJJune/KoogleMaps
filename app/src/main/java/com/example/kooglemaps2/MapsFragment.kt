package com.example.kooglemaps2

import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.kooglemaps2.databinding.ActivityMainBinding
import com.example.kooglemaps2.databinding.FragmentMapsBinding
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions

class MapsFragment : Fragment() {
    lateinit var mapView: MapView

    lateinit var binding : FragmentMapsBinding
    lateinit var googleMap: GoogleMap

    val konkuk_loc = LatLng(37.542402, 127.076903)

    //사용자의 현재위치 사용하기
    var user_loc = konkuk_loc
    //lateinit var fusedLocationProviderCilent = FusedLocationProviderClient


    //구글맵에 폴리라인 그리기 -> 위도경도 좌표의 배열이 필요하다
    val arrLoc = ArrayList<LatLng>()

    private val callback = OnMapReadyCallback { googleMap ->

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}