package com.example.kooglemaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kooglemaps.databinding.ActivityMainBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var data:HashMap<String,spotData>
    val dbController = dbController()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()


        //var dbCon = dbController() //dbController
        //data = dbCon.getData()
        dbController.setData(spotData("test1",1.1, 2.2, "dd", ArrayList(), ArrayList(), 0))
        Log.d("test", dbController.getData("test1").title)
    }

    fun initLayout(){

    }
}