package com.example.kooglemaps

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

/*
    내일 할거
    - itemAdd callBack fun으로 업데이트된 횟수 구현
    - 그걸로 업데이트 된 count만큼 최신 데이터 불러오기
*/
class dbController (){

    companion object{   //static 변수,함수
        var updatedCount = 0;
        val removedKey = HashSet<String>()

        fun countIncrement(){
            updatedCount++
        }
        fun resetCount(){
            updatedCount = 0
        }
        fun getCount() : Int{
            return updatedCount
        }
    }

    val DBConnect = Firebase.database
    val spotTable = DBConnect.getReference("spotDB")
    val allDBMap = HashMap<String, spotData>()
    val updatedMap = HashMap<String, spotData>()
    //val userTable = DBConnect.getReference("userDB")


    val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var userDB:HashMap<String, String>

    /*suspend fun getNickname(uid:String) : String {
        lateinit var dataSnapshot:DataSnapshot

        withContext(Dispatchers.IO) {
            dataSnapshot = userTable.child(uid).get().await()
        }

        val nickname:String = dataSnapshot.child("nickname").value as String
        if(nickname!=null){
            return nickname
        }
        else{
            throw java.lang.NullPointerException("Can't Find Nickname, is UID valid?")
        }
    }
    fun setNickname(uid:String, newNickname:String){    //key value 찾지 못할 시 insert 있으면 update
        userTable.child(uid).setValue(newNickname)
    }*/

    init{
        spotTable.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //countIncrement()
                //Log.e("DB", "Add" + updatedCount.toString())

                lateinit var returnData:spotData
                val temp = snapshot
                var tags = ArrayList<String>()
                var review = ArrayList<String>()
                var likes = ArrayList<String>()
                var cord1 = 0.0
                var cord2 = 0.0

                val title = temp.child("title").value as String
                try {
                    cord1 = temp.child("cord1").value as Double
                    cord2 = temp.child("cord2").value as Double
                    tags = temp.child("tags").value as ArrayList<String>
                    review = temp.child("review").value as ArrayList<String>
                    likes = temp.child("likes").value as ArrayList<String>
                }catch (E : java.lang.Exception){       //if ArrayList is null

                }
                val desc = temp.child("desc").value as String

                returnData = spotData(title, cord1, cord2, desc, tags, review, likes)
                Log.v("String", title)
                updatedMap.put(title, returnData)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                countIncrement()
//                Log.e("DB", "Change")

                allDBMap
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                try {
                    val removed = snapshot.child("title").value as String
                    removedKey.add(removed)
                    Log.d("DBtest", removed)
                }catch (e: Exception){
                    Log.e("DBError", e.toString())
                }
                Log.e("DB", "Removed")
            }
            //삭제된 spot들 파악

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //Not used
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DBError", error.toException().toString())
            }

        })
    }

    suspend fun updateMap() : HashMap<String, spotData>{
        val returnMap = updatedMap.clone() as HashMap<String, spotData>
        for(i in returnMap.iterator()){
            allDBMap.put(i.key, i.value)
        }
        for(i in removedKey){
            allDBMap.remove(i)
        }



        updatedMap.clear()
        return returnMap
    }

    suspend fun getAllData() : HashMap<String, spotData>{
        lateinit var dataSnapshot:DataSnapshot

        var tags = ArrayList<String>()
        var review = ArrayList<String>()
        var likes = ArrayList<String>()
        var cord1 = 0.0
        var cord2 = 0.0

        withContext(Dispatchers.IO) {
            dataSnapshot = spotTable.get().await()
        }
        for(temp in dataSnapshot.children){
            val title = temp.child("title").value as String
            try {
                cord1 = temp.child("cord1").value as Double
                cord2 = temp.child("cord2").value as Double
                tags = temp.child("tags").value as ArrayList<String>
                review = temp.child("review").value as ArrayList<String>
                likes = temp.child("likes").value as ArrayList<String>
            }catch (E : java.lang.Exception){       //if ArrayList is null

            }
            val desc = temp.child("desc").value as String

            val returnData = spotData(title, cord1, cord2, desc, tags, review, likes)

            allDBMap.put(title ,returnData)
        }
        Log.d("DB", "in DB")
        return allDBMap
    }

    suspend fun getData(key:String) : spotData{
        var returnData = spotData()
        lateinit var dataSnapshot:DataSnapshot

        withContext(Dispatchers.IO) {
            dataSnapshot = spotTable.child(key).get().await()
            Log.v("wait In Scope", dataSnapshot.child("title").getValue() as String)
        }

        val temp = dataSnapshot
        var tags = ArrayList<String>()
        var review = ArrayList<String>()
        var likes = ArrayList<String>()

        val title = temp.child("title").value as String
        val cord1 = temp.child("cord1").value as Double
        val cord2 = temp.child("cord2").value as Double
        try {
            tags = temp.child("tags").value as ArrayList<String>
            review = temp.child("review").value as ArrayList<String>
            likes = temp.child("likes").value as ArrayList<String>
        }catch (E : java.lang.Exception){       //if ArrayList is null

        }
        val desc = temp.child("desc").value as String

        returnData = spotData(title, cord1, cord2, desc, tags, review, likes)
        Log.v("String", title)

        return returnData
    }

    fun setData(d:spotData){    //key value 찾지 못할 시 insert 있으면 update
        spotTable.child(d.title).setValue(d)
    }
}