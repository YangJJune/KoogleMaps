package com.example.kooglemaps

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class dbController (){
    val DBConnect = Firebase.database
    val spotTable = DBConnect.getReference("spotDB")
    val scope = CoroutineScope(Dispatchers.IO)

    init{
        val postListenener = object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue<spotData>()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DB error", error.toString())
            }

        }
        spotTable.addValueEventListener(postListenener)
    }

    fun getData2(key: String) : spotData{
        var returnData = spotData(title = "null")       //if data not exists

        lateinit var spotTask:DataSnapshot
        scope.launch {
            spotTask = spotTable.get().addOnSuccessListener{
                Log.d("DB", "success")
                it
            }.addOnFailureListener{
                Log.d("DB", "failed")
            }.await()
        }
        val temp = spotTask


        val title = temp.child("title").value.toString()
        val cord1 = temp.child("cord1").value as Double
        val cord2 = temp.child("cord2").value as Double
        val tags = temp.child("tags").value as ArrayList<String>
        val reviews = temp.child("reviews").value as ArrayList<String>
        val likes = temp.child("likes").value as Long
        val desc = temp.child("desc").value.toString()

        returnData = spotData(title, cord1, cord2, desc, tags, reviews, likes.toInt())
        return returnData
    }

    fun getData(key:String) : spotData{
        var returnData = spotData()
        val dataSnapshot= spotTable.child(key).get().addOnSuccessListener{
            if(it.exists()){

            }else{
                Log.e("DB", "key is not exists")
            }
        }.addOnFailureListener {
            Log.e("DB", "failed to get more : " + it.toString())
        }
        val temp = dataSnapshot.getResult()
        val title = temp.child("title").value.toString()
        val cord1 = temp.child("cord1").value as Double
        val cord2 = temp.child("cord2").value as Double
        val likes = temp.child("likes").value as Long
        val desc = temp.child("desc").value.toString()
        returnData = spotData(title, cord1, cord2, desc, ArrayList(), ArrayList(), likes.toInt())

        Log.d("hash", returnData.hashCode().toString())
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
