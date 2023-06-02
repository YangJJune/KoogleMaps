package com.example.kooglemaps

import android.provider.ContactsContract.Data
import android.util.Log
import android.view.inputmethod.CorrectionInfo
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
import kotlinx.coroutines.NonCancellable.children
import kotlinx.coroutines.tasks.await

class dbController (){
    val DBConnect = Firebase.database
    val spotTable = DBConnect.getReference("spotDB")
    val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getAllData() : HashMap<String, spotData>{
        lateinit var dataSnapshot:DataSnapshot
        var returnMap = HashMap<String, spotData>()

        var tags = ArrayList<String>()
        var review = ArrayList<String>()

        withContext(Dispatchers.IO) {
            dataSnapshot = spotTable.get().await()
        }

        for(temp in dataSnapshot.children){
            val title = temp.child("title").value as String
            val cord1 = temp.child("cord1").value as Double
            val cord2 = temp.child("cord2").value as Double
            try {
                tags = temp.child("tags").value as ArrayList<String>
                review = temp.child("review").value as ArrayList<String>
            }catch (E : java.lang.Exception){       //if ArrayList is null
                tags = ArrayList<String>()
                review = ArrayList<String>()
            }
            val desc = temp.child("desc").value as String
            val likes = temp.child("likes").value as Long

            val returnData = spotData(title, cord1, cord2, desc, tags, review, likes.toInt())

            returnMap.put(title ,returnData)
        }

        return returnMap
    }

    suspend fun getData(key:String) : spotData{
        var returnData = spotData()
        lateinit var dataSnapshot:DataSnapshot

        withContext(Dispatchers.IO) {
            dataSnapshot = spotTable.child(key).get().await()
            Log.v("wait In Scope", dataSnapshot.child("title").getValue() as String)
        }

        val temp = dataSnapshot
        lateinit var tags:ArrayList<String>
        lateinit var review:ArrayList<String>

        val title = temp.child("title").value as String
        val cord1 = temp.child("cord1").value as Double
        val cord2 = temp.child("cord2").value as Double
        try {
            tags = temp.child("tags").value as ArrayList<String>
            review = temp.child("review").value as ArrayList<String>
        }catch (E : java.lang.Exception){       //if ArrayList is null
            tags = ArrayList<String>()
            review = ArrayList<String>()
        }
        val desc = temp.child("desc").value as String
        val likes = temp.child("likes").value as Long

        returnData = spotData(title, cord1, cord2, desc, tags, review, likes.toInt())
        Log.v("String", title)

        return returnData
    }

    fun setData(d:spotData){    //key value 찾지 못할 시 insert 있으면 update
        spotTable.child(d.title).setValue(d)
    }
}
