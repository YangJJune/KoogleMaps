package com.example.kooglemaps

class dbController (){
    private var data:HashMap<String, spotData> = HashMap()

    init{
        initDB()
    }

    fun initDB(){
            //TODO dbconnecting을 하세요
        var tmp:ArrayList<String> = ArrayList()
        tmp.add("설명1")
        tmp.add("설명2")
        tmp.add("설명3")
        addData("1",spotData(37.543675,127.077067,"새천년관입니다",tmp,tmp,0 ))
        addData("2",spotData(37.541843, 127.078040,"학생회관입니다",tmp,tmp,0))
    }

    fun getData():HashMap<String, spotData>{
        return data;
    }

    fun addData(key:String, d:spotData){
        data.put(key, d)
    }

    fun removeData(key:String){
        data.remove(key)
    }
}