package com.example.kooglemaps

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.components.ComponentRuntime
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class dbController (){
    val DBConnect = Firebase.database
    val spotTable = DBConnect.getReference("spotDB")
    val scope = CoroutineScope(Dispatchers.IO)
    val tempArr = ArrayList<spotData>()
    var tempSpot = spotData()
    var shield = true

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
        spotTask = spotTable.child(key).get().getResult()


        val temp = spotTask.child(key)
        if(temp.exists())
            Log.d("dd","dddd")

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

    fun getFBData(key:String) : spotData{
        var returnData = spotData()
        var dataSnapshot = spotTable.child(key).get()
            var tempString = "not changed"
            if(scope.async {
                dataSnapshot = spotTable.child(key).get()
                Log.v("wait In Scope", tempString)
            }.isCompleted){
                val temp = dataSnapshot.getResult()
                val title = temp.child("title").value as String
                val cord1 = temp.child("cord1").value as Double
                val cord2 = temp.child("cord2").value as Double
                val tags = temp.child("tags").value as ArrayList<String>
                val reviews = temp.child("reviews").value as ArrayList<String>
                val desc = temp.child("desc").value as String
                val likes = temp.child("likes").value as Long
                returnData = spotData(title, cord1, cord2, desc, tags, reviews, likes.toInt())
                Log.v("String", title)

            }
        return returnData
    }

    fun getDataHelper(key: String) : DataSnapshot{

    }

    fun getDataHelper2() : spotData{
        return tempSpot
    }

    fun setData(d:spotData){    //key value 찾지 못할 시 insert 있으면 update
        spotTable.child(d.title).setValue(d)
    }

//    fun removeData(key:String){
//        data.remove(key)
//    }
}
