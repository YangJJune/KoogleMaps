package com.example.kooglemaps

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kooglemaps.databinding.ActivityRegisterBinding
import com.example.kooglemaps.databinding.ActivitySpotBinding
import com.example.kooglemaps.databinding.AlertdialogEdittextBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
>>>>>>>>> Temporary merge branch 2

// spot icon 클릭 시 해당 spot 정보 띄우는 작업 수행
class SpotActivity: AppCompatActivity() {
    lateinit var binding: ActivitySpotBinding
    var favoriteColor = "gray"
    var curSpotData = spotData()
    var uid = ""
<<<<<<<<< Temporary merge branch 1
    private lateinit var auth: FirebaseAuth
    var title = ""

    lateinit var googleMap: GoogleMap
    val dbCon = dbController()

    val data:ArrayList<spotData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        title = intent.getStringExtra("title") as String
        Log.v("title", title)
        Log.e("hash", curSpotData.hashCode().toString())

        binding = ActivitySpotBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        CoroutineScope(Dispatchers.Main).launch{
//            CoroutineScope(Dispatchers.IO).async{
//                curSpotData = dbCon.getData(title)
//            }.await()
//            Log.e("hash", curSpotData.hashCode().toString())
//        }
        curSpotData = intent.getSerializableExtra("spot") as spotData
        Log.i("title232", curSpotData.title)

        initmap()
        CoroutineScope(Dispatchers.Main).launch {
            initLayout()
            initEvent()
            initRecyclerView()
        }
    }

    override fun onStop() {
        super.onStop()
        dbCon.setData(curSpotData)
    }

    suspend fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        // 어댑터 객체 생성 후 초기화
        val adapter = SpotDataAdapter(curSpotData.review)
        Log.e("size", curSpotData.review.size.toString())

        binding.recyclerView.adapter = adapter

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )
    }

    suspend fun initLayout() {
        // intent를 통해 장소의 id 전달 받고,
        // 해당 장소의 DB정보 불러와서 화면에 띄우는 작업 수행
        val title = intent.getStringExtra("title")
        val desc = intent.getStringExtra("desc")
        CoroutineScope(Dispatchers.IO).async {
            curSpotData = dbCon.getData(title!!)
        }.await()
        Log.v("async", curSpotData.title)
        if(curSpotData.title == null)
            return

        var likeList = curSpotData.likeUser
        Log.d("likeSize", likeList?.size.toString())



        // 현재 로그인한 user 정보 불러옴 => 좋아요 표시에 이용
        uid = Firebase.auth.uid.toString()
        Log.i("uid", uid)
        Toast.makeText(this@SpotActivity, uid, Toast.LENGTH_SHORT).show()

        Log.v("size", likeList.size.toString())
        // spotDB의 좋아요 누른 uid 리스트에 현재 로그인 된 user의 id 있는지 탐색
        var like = likeList.contains(uid)

        // 있으면 빨간 하트로 설정 & 없으면 회색으로 설정
        binding.apply {
            if(like){
                favoriteBtn.setImageResource(R.drawable.baseline_favorite_24_red)
                favoriteColor = "red"
            }
            else{
                favoriteBtn.setImageResource(R.drawable.baseline_favorite_24)
                favoriteColor = "gray"
            }

            spotName.text = title
            favoriteNum.text = likeList.size.toString()
            spotDescription.text = desc
        }
    }

    suspend fun initEvent(){
        binding.apply {
            goBackBtn.setOnClickListener {
                // 뒤로가기 버튼 클릭 시 이벤트 처리
                // 이전 화면(구글 맵 화면)으로 이동
                /* 임시로 로그인 화면으로 이동하는 걸로 구현 */
                val intent = Intent(this@SpotActivity, MapActivity::class.java)
                startActivity(intent)
            }

            favoriteBtn.setOnClickListener {
                // 좋아요 버튼 클릭 시 이벤트 처리
                // 누르지 않은 상태 : image source를 red로 변경하고, 숫자 증가
                // 이미 누른 상태 : image source를 기본으로 변경하고, 숫자 감소

                if(auth.currentUser?.isAnonymous == true){
                    Toast.makeText(this@SpotActivity,"비회원은 이용이 불가합니다",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                var favoriteCount = favoriteNum.text.toString().toInt()

                if(favoriteColor == "gray") {
                    // 누르지 않은 상태에서 클릭 (gray=>red)
                    favoriteBtn.setImageResource(R.drawable.baseline_favorite_24_red)
                    favoriteNum.setText("${++favoriteCount}")
                    favoriteColor = "red"

                    /* SpotDB의 좋아요 수 및 좋아요 누른 uid 리스트 수정 */
                    if(!curSpotData.likeUser!!.contains(uid)){
                        curSpotData.likeUser!!.add(uid)
                    }
                    //Log.d("size", curSpotData.likeUser.size.toString())


                }
                else if(favoriteColor == "red"){
                    // 누른 상태에서 클릭 (red=>gray)
                    favoriteBtn.setImageResource(R.drawable.baseline_favorite_24)
                    favoriteNum.setText("${--favoriteCount}")
                    favoriteColor = "gray"

                    /* SpotDB의 좋아요 수 및 좋아요 누른 uid 리스트 수정 */
                    if(curSpotData.likeUser!!.contains(uid)){
                        curSpotData.likeUser!!.remove(uid)
                    }

                }
            }

            addBtn.setOnClickListener {
                // 리뷰 작성
                if(auth.currentUser?.isAnonymous == true){
                    Toast.makeText(this@SpotActivity,"비회원은 이용이 불가합니다",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val builder = AlertDialog.Builder(this@SpotActivity)
                val builderItem = AlertdialogEdittextBinding.inflate(layoutInflater)
                val editText = builderItem.editText
                with(builder){
                    setTitle("한 줄 평 추가")
                    setIcon(R.drawable.baseline_add_comment_24)
                    setMessage("한 줄 평을 입력해주세요")
                    setView(builderItem.root)
                    setPositiveButton("추가"){ dialogInterface: DialogInterface, i:Int ->
                        if(editText.text!=null)
                            Toast.makeText(this@SpotActivity, "한 줄 평 추가 완료", Toast.LENGTH_SHORT).show()
                        curSpotData.review.add(editText.text.toString())
                    }
                    setNegativeButton("취소", null)
                    show()
                }
           }
        }
    }

    private fun initmap() {
        val loc = intent.getStringExtra("loc")
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        //lat/lng: (37.542402,127.076903) 로 나온다.
        val desc = intent.getStringExtra("desc")
        var like = intent.getStringArrayListExtra("desc")
        if(like == null){
            like = ArrayList<String>()
        }

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
            .findFragmentById(R.id.map_spot)
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
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
            option.title(title)//마커의 윗쪽 큰글씨
            googleMap.addMarker(option)?.showInfoWindow()

            googleMap.setOnMapClickListener { latLng ->
                // 클릭한 위치에 마커를 추가합니다.
                googleMap.addMarker(MarkerOptions().position(latLng).title("Clicked Marker"))
            }

            googleMap.setLatLngBoundsForCameraTarget(bounds)

            binding.apply {
                spotName.text = title
                spotDescription.text = desc
            }
        }

    }
}