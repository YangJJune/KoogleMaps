package com.example.kooglemaps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kooglemaps.databinding.ActivtyMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.play.integrity.internal.x
import java.net.MalformedURLException
import java.net.URL

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener{
    lateinit var binding: ActivtyMapBinding
    lateinit var googleMap: GoogleMap
    private lateinit var getResult:ActivityResultLauncher<Intent>

    val dbController = dbController()
    lateinit var allMarker:HashMap<String, spotData>    //전체 marker Data

    private var clickedMarker: Marker ?= null

    val loc = LatLng(37.554752, 126.970631)//서울역 좌표
    val konkuk_loc = LatLng(37.542402, 127.076903)

    //건물 스팟 이미지 대응 해쉬맵
    val imageMap = HashMap<LatLng, Int>().apply {
        put(LatLng(37.543905290591916, 127.07518246024847), R.drawable.eduscience)
        put(LatLng(37.54134046118512, 127.07785427570343), R.drawable.student2)
        put(LatLng(37.54187214948923, 127.07480896264316), R.drawable.law)
        put(LatLng(37.54287703002882, 127.07815770059823), R.drawable.korean)
        put(LatLng(37.542081101954885, 127.0725465193391), R.drawable.lifescience)
        put(LatLng(37.5404881569232, 127.07945823669435), R.drawable.newengine)
        put(LatLng(37.54287703002882, 127.0732754096389), R.drawable.design)
        put(LatLng(37.54096934050193, 127.07438785582781), R.drawable.muse)
        put(LatLng(37.54367268376885, 127.0772011578083), R.drawable.newyear)
        put(LatLng(37.539719054290636, 127.07312654703857), R.drawable.teamprj)
        put(LatLng(37.54342731601352, 127.07821570336819), R.drawable.ground)
        put(LatLng(37.54139708616991, 127.08071116358042), R.drawable.math)
        put(LatLng(37.540166214131915, 127.07146592438222), R.drawable.hospital)
        put(LatLng(37.543149780981096, 127.07518547773361), R.drawable.hangjeong)
        put(LatLng(37.54186656678174, 127.07787338644265), R.drawable.student)
        put(LatLng(37.5403568278269, 127.07435097545387), R.drawable.animallifesc)
        put(LatLng(37.544242103985376, 127.07573667168617), R.drawable.research)
        put(LatLng(37.542188768073075, 127.07380313426256), R.drawable.library)
        put(LatLng(37.544253800713605, 127.07621846348047), R.drawable.ceo)
        put(LatLng(37.54147152270421, 127.07937072962524), R.drawable.engin)
        put(LatLng(37.539362813258855, 127.07806482911111), R.drawable.kulhouse)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivtyMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //addImagemap()
        //init Launcher
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val x = it.data?.getDoubleExtra("x",0.0)
                val y = it.data?.getDoubleExtra("y",0.0)

                if(x==0.0 || y==0.0){
                    return@registerForActivityResult;
                }

                val tmpLoc = LatLng(x!!,y!!)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tmpLoc,18.0f))
            }
            else if(it.resultCode== RESULT_CANCELED){
                //nothing
            }
        }

        initmap()

        //initspinner()

        initLayout()
    }

    override fun onStart() {
        super.onStart()
        for(i in dbController.updatedMap){
            allMarker.put(i.key, i.value)
        }
    }

    private fun initLayout() {

        binding.btnFab.setOnClickListener {
        }
        binding.btnFind.setOnClickListener{
            val i = Intent(this, SearchActivity::class.java)
            getResult.launch(i)
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
            if(intent.getStringExtra("Added") != null) {
                val newLocX = intent.getStringExtra("xPoint")!!.toDouble()
                val newLocY = intent.getStringExtra("yPoint")!!.toDouble()

                val newLatLng = LatLng(newLocX, newLocY)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 18.0f))
            }
            else {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(konkuk_loc, 18.0f))
            }
            //zoom level 제한하기
            googleMap.setMinZoomPreference(16.0f)//zoom의 최소 레벨 설정
            googleMap.setMaxZoomPreference(22.0f)//zoom 최대 레벨 설정

            //val konkuk_loc = LatLng(37.542402, 127.076903)
            val bounds = LatLngBounds.Builder()
                .include(LatLng(37.547660, 127.074053)) // 좌상단 좌표
                .include(LatLng(37.538032, 127.081679)) // 우하단 좌표
                .build()

            //아예 지정 범위를 나가지 못하는 코드
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

            //건물 정보 추가하는 파트
            CoroutineScope(Dispatchers.Main).launch {
                CoroutineScope(Dispatchers.IO).async {
                    allMarker = dbController.getAllData()
                }.await()
                for(i in allMarker){
                    val value = i.value
                    val marker = MarkerOptions()
                    marker.position(LatLng(value.cord1, value.cord2))
                    marker.title(value.title)
                    val xy = LatLng(value.cord1, value.cord2)
                    //이미지 변경하기
                    if(imageMap.contains(xy)) {
                        val dr = imageMap.get(xy)
                        val bitmapdraw =  resources.getDrawable(dr!!) as BitmapDrawable
                            //resources.obtainTypedArray(R.array.baseB)
                            //imageMap.get(xy) as BitmapDrawable
                            //resources.getDrawable(imageMap.get(xy)) as BitmapDrawable
                        val b = bitmapdraw.bitmap
                        val smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false)
                        marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    }

                    googleMap.addMarker(marker)
                }
            }


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
                if(auth.currentUser?.isAnonymous == true){
                    Toast.makeText(this@MapActivity,"비회원은 이용이 불가합니다", Toast.LENGTH_SHORT).show()
                }
                else {
                    googleMap.addMarker(MarkerOptions().position(latLng).title("Clicked Marker"))
                }
            }

            googleMap.setOnMarkerClickListener(this)
            googleMap.setOnMapClickListener(this)

            binding.btnFab.setOnClickListener {
                Toast.makeText(this@MapActivity,
                    "맵을 터치하여 스팟을 추가해주세요", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        /*
        if (marker.title == "Clicked Marker") {
            showInputDialog(marker)
        }
        return false
        */
        val i = Intent(this, SpotActivity::class.java)
        i.putExtra("title", marker.title.toString())
        i.putExtra("content", marker.snippet.toString())
        i.putExtra("loc", marker.position.toString())
        //DB 연계 수정
        i.putExtra("desc", allMarker.get(marker.title)?.desc)
        i.putExtra("likeUser", allMarker.get(marker.title)?.likeUser as ArrayList<String>)
        i.putExtra("uid", intent.getStringExtra("uid").toString())
        i.putExtra("spot", allMarker.get(marker.title))
        startActivity(i)

        return true
    }
    private fun showInputDialog(marker: Marker) {//임시 마커 생성시 입력 다이얼로그
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
        val i = Intent(this, AddSpotActivity::class.java)
        i.putExtra("loc", p0.toString())
        // 기존 마커가 있는 경우 제거합니다.
        //clickedMarker?.remove()

        if(auth.currentUser?.isAnonymous == true){
            Toast.makeText(this@MapActivity,"비회원은 이용이 불가합니다", Toast.LENGTH_SHORT).show()
        }
        else {
            // 마커를 추가합니다.
            clickedMarker =
                googleMap.addMarker(MarkerOptions().position(p0).title("Click to set title"))
            val i = Intent(this, AddSpotActivity::class.java)
            i.putExtra("loc", p0.toString())

        startActivity(i)
    }


}
