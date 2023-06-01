package com.example.kooglemaps

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kooglemaps.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity:AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var registerBinding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)
        Toast.makeText(this,"11",Toast.LENGTH_SHORT).show()
        registerBinding.registerBtn.setOnClickListener {
            
            val email:String = registerBinding.emailedit.text.toString()
            val pw:String = registerBinding.passwordEdit.text.toString()
            if(pw.equals(registerBinding.passwordEdit2.text.toString())) {
                auth.createUserWithEmailAndPassword(email,pw).addOnCompleteListener(this) { task->
                    if(task.isSuccessful){
                        Toast.makeText(this,"성공",Toast.LENGTH_SHORT).show()
                    }
                    else
                        Toast.makeText(this,"실패",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}