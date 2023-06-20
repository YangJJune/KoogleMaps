package com.example.kooglemaps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.kooglemaps.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = Firebase.auth
        setContentView(binding.root)
        initLayout()

        var dbCon = dbController() //dbController
    }
    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

    }
    fun initLayout(){
        binding.loginBtn.setOnClickListener {
            val email = binding.idEdit.text.toString()
            val pw = binding.passwordEdit.text.toString()
            auth.signInWithEmailAndPassword(email,pw).addOnCompleteListener(this){task->
                 if(task.isSuccessful){
                     //지도가 뜨는 엑티비티로 전환
                     val tmpIntent = Intent(this, MapActivity::class.java)
                     tmpIntent.putExtra("uid", auth.currentUser?.uid)
                     Toast.makeText(this,"로그인 성공", Toast.LENGTH_SHORT).show()
                     launcher.launch(tmpIntent)
                 }else{
                     if(task.exception is FirebaseAuthInvalidCredentialsException){
                         val tmpException:FirebaseAuthInvalidCredentialsException = (task.exception as FirebaseAuthInvalidCredentialsException)!!
                         Toast.makeText(this,tmpException.errorCode, Toast.LENGTH_SHORT).show()
                         if(tmpException.errorCode.equals("ERROR_INVALID_EMAIL")){
                             Toast.makeText(this, "아이디는 이메일이어야 합니다\n 다시 한번 확인해주세요", Toast.LENGTH_SHORT).show()
                         }
                         else{
                             Toast.makeText(this, task.exception.toString()+"로그인 실패\n로그인 중에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show()
                         }
                     }
                     else if(task.exception is FirebaseAuthInvalidUserException){
                         val tmpException:FirebaseAuthInvalidUserException = (task.exception as FirebaseAuthInvalidUserException)!!
                         Toast.makeText(this, "로그인 정보가 잘못되었습니다.\n다시 한번 확인해주세요", Toast.LENGTH_SHORT).show()
                     }

                     else {
                         Toast.makeText(this, task.exception.toString()+"로그인 실패, 로그인 중에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show()
                     }
                 }
            }
        }
        binding.registerBtn.setOnClickListener {
            val i = Intent(this@MainActivity, RegisterActivity::class.java)
            launcher.launch(i)
        }
    }
}