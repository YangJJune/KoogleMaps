package com.example.kooglemaps2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

//2. fragment state adpater 상속
//3. 생성자 수정 class MyViewPagerAdapter : FragmentStateAdapter{
//class MyViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){
//                         (fragmentActivity 의미 == , FragmentActivity == ,
//                         FragmentStateAdapter는 안드로이드 서비스로 존재하는가? 인자로 받는 fragmentActivity는?

class MyViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){
    //4. alt enter로 implement 수행
    override fun getItemCount(): Int {
        return 3//fragment 2개 이므로 ++ 새로운 팀 소개 탭을 추가하였으므로 3개
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->MapsFragment()
            1->aFragment()
            2->bFragment()

            else->MapsFragment()//default 경우
        }
    }
}