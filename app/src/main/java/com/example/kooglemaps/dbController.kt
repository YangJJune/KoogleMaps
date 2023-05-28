package com.example.kooglemaps

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class dbController (){
    val DBConnect = Firebase.database
    val spotTable = DBConnect.getReference("spotDB")


    fun getData(key:String) : spotData{
        var returnData = spotData()
        spotTable.child(key).get().addOnSuccessListener{
            if(it.exists()){
                val title = it.child("title").value.toString()
                val cord1 = it.child("cord1").value as Double
                val cord2 = it.child("cord2").value as Double
                val likes = it.child("likes").value as Long
                val desc = it.child("desc").value.toString()
                Log.d("DB2312312",title)
                returnData = spotData(title, cord1, cord2, desc, ArrayList(), ArrayList(), likes.toInt())
                Log.d("hash", returnData.hashCode().toString())
            }else{
                Log.e("DB", "key is not exists")
            }
        }.addOnFailureListener {
            Log.e("DB", "failed to get more : " + it.toString())
        }
        Log.d("hash", returnData.hashCode().toString())
        Log.d("test2", returnData.title)
        return returnData
    }

    fun setData(d:spotData){    //key value 찾지 못할 시 insert 있으면 update
        spotTable.child(d.title).setValue(d)
    }

//    fun removeData(key:String){
//        data.remove(key)
//    }
}
