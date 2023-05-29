package com.example.kooglemaps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.kooglemaps.databinding.ActivityMainBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var data:HashMap<String,spotData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()

        var dbCon = dbController() //dbController
        data = dbCon.getData()
    }
    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

    }
    fun initLayout(){
        binding.loginBtn.setOnClickListener {
            Toast.makeText(this,"로그인 시도", Toast.LENGTH_SHORT).show()
        }
        binding.registerBtn.setOnClickListener {
            val i = Intent(this@MainActivity, RegisterActivity::class.java)
            launcher.launch(i)
        }
    }
}