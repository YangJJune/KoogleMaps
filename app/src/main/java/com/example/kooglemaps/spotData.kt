package com.example.kooglemaps

/*
cord : 스팟의 좌표
desc : 설명
tags : 태그 배열
review : 한 줄 평들 배열
likes : 좋아요 수
*/
data class spotData(val title:String = "", val cord1:Double = 0.0 , val cord2:Double = 0.0, var desc:String = "",
                    var tags:ArrayList<String> ?= null, var review:ArrayList<String> ?= null, var likes:Int = 0)