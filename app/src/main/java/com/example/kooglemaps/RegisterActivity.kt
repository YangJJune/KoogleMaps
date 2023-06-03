package com.example.kooglemaps

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kooglemaps.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
                    else{
                        if(task.exception!! is FirebaseAuthUserCollisionException){
                            val tmpException:FirebaseAuthUserCollisionException = (task.exception as FirebaseAuthUserCollisionException?)!!
                            when(tmpException.errorCode){
                                "ERROR_EMAIL_ALREADY_IN_USE" ->{
                                    Toast.makeText(this,"이미 사용 중인 email입니다. 다른 email을 사용해주세요",Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this,"회원가입 실패, 문제확인을 위해 개발자에 문의 바랍니다",Toast.LENGTH_SHORT).show()
                                }
                            }

                        }


                    }
                }
            }

        }
    }
}