package com.example.kooglemaps

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kooglemaps.databinding.ActivitySearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchActivity: AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    lateinit var adapter: srchAdapter
    lateinit var allSpot:HashMap<String, spotData>
    var spotArr:ArrayList<spotData> = arrayListOf<spotData>()
    var foundSpot = arrayListOf<spotData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
        initRecyclerView()
    }
    fun initRecyclerView(){
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        adapter = srchAdapter(foundSpot)
        adapter.itemClickListener = object:srchAdapter.OnItemClickListener{
            override fun OnItemClick(position: Int, spot: spotData) {
                val intent = Intent(this@SearchActivity, MapActivity::class.java)
                intent.putExtra("x",spot.cord1)
                intent.putExtra("y",spot.cord2)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        binding.recyclerView.adapter = adapter
    }
    @SuppressLint("NotifyDataSetChanged")
    fun initLayout(){
        binding.goBackBtn.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            setResult(RESULT_CANCELED)
            finish()
        }
        binding.srchBtn.setOnClickListener {

            foundSpot.clear()
            val dbController:dbController = dbController()
            CoroutineScope(Dispatchers.Main).launch {
                CoroutineScope(Dispatchers.IO).async {
                    allSpot = dbController.getAllData()
                }.await()
                val txt = binding.srchTxt.text.toString()

                for(i in allSpot){
                    spotArr.add(i.value)
                }
                for(i in spotArr){
                    if(i.title.contains(txt)){
                        foundSpot.add(i)
                        continue
                    }
                    if(i.desc.contains(txt)){
                        foundSpot.add(i)
                        continue
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }
}