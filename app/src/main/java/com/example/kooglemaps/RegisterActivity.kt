package com.example.kooglemaps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kooglemaps.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class RegisterActivity:AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var dbcon:dbController

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbcon = dbController()
        auth = Firebase.auth
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        registerBinding.registerBtn.setOnClickListener {

            val email:String = registerBinding.emailedit.text.toString()
            val pw:String = registerBinding.passwordEdit.text.toString()

            if(pw.equals(registerBinding.passwordEdit2.text.toString())) {
                auth.createUserWithEmailAndPassword(email,pw).addOnCompleteListener(this) { task->
                    if(task.isSuccessful){
                        val user = auth.currentUser
                        user!!.updateProfile(userProfileChangeRequest {
                            displayName = registerBinding.idEdit.text.toString()
                        })
                        Toast.makeText(this,"회원가입 성공",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else{
                        if(task.exception!! is FirebaseAuthUserCollisionException){
                            val tmpException:FirebaseAuthUserCollisionException = (task.exception as FirebaseAuthUserCollisionException?)!!
                            when(tmpException.errorCode){
                                "ERROR_INVALID_EMAIL"->{
                                    Toast.makeText(this,"올바른 이메일이 아닙니다",Toast.LENGTH_SHORT).show()
                                }
                                "ERROR_EMAIL_ALREADY_IN_USE" ->{
                                    Toast.makeText(this,"이미 사용 중인 email입니다. 다른 email을 사용해주세요",Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this,"회원가입 실패, 문제확인을 위해 개발자에 문의 바랍니다",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        else if(task.exception!! is FirebaseAuthInvalidCredentialsException){
                            val tmpException:FirebaseAuthInvalidCredentialsException = (task.exception as FirebaseAuthInvalidCredentialsException?)!!
                            Log.d("error",tmpException.errorCode)
                            when(tmpException.errorCode){
                                "ERROR_INVALID_EMAIL" ->{
                                    Toast.makeText(this,"올바른 형태의 이메일이 아닙니다",Toast.LENGTH_SHORT).show()
                                }
                                "ERROR_WEAK_PASSWORD" ->{
                                    Toast.makeText(this,"올바른 형태의 비밀번호가 아닙니다",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        else{
                            Toast.makeText(this,task.exception!!.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            else{
                Toast.makeText(this,"PW가 PW확인과 동일하지 않습니다",Toast.LENGTH_SHORT).show()
            }
        }
    }
}