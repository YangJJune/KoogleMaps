package com.example.kooglemaps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.kooglemaps.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var data:HashMap<String,spotData>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = Firebase.auth
        setContentView(binding.root)
        initLayout()

        var dbCon = dbController() //dbController
        data = dbCon.getData()
    }
    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

    }
    fun initLayout(){
        val email = binding.idEdit.text.toString()
        val pw = binding.passwordEdit.text.toString()
        binding.loginBtn.setOnClickListener {

            val i = Intent(this, AddSpotActivity::class.java)
            startActivity(i)

//            val tmpIntent = Intent(this, SpotActivity::class.java)
//            tmpIntent.putExtra("userData", auth.currentUser)
//            startActivity(tmpIntent)

//            auth.signInWithEmailAndPassword(email,pw).addOnCompleteListener(this){ task->
//                if(task.isSuccessful){
//                    //지도가 뜨는 엑티비티로 전환
////                    val tmpIntent = Intent(this, SpotActivity::class.java)
////                    tmpIntent.putExtra("userData", auth.currentUser)
//                    Toast.makeText(this,"로그인 성공", Toast.LENGTH_SHORT).show()
//                }else{
//                    Toast.makeText(this,"로그인 실패", Toast.LENGTH_SHORT).show()
//                }
//            }
            //Toast.makeText(this,"로그인 시도", Toast.LENGTH_SHORT).show()
        }
        binding.registerBtn.setOnClickListener {
            val i = Intent(this@MainActivity, RegisterActivity::class.java)
            launcher.launch(i)
        }
    }
}