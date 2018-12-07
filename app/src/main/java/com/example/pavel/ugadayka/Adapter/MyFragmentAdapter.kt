package com.example.pavel.ugadayka.Adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.pavel.ugadayka.Common.Common
import com.example.pavel.ugadayka.Question_fragment
import java.lang.StringBuilder

class MyFragmentAdapter(fm:FragmentManager, var context: Context,
                        var fragmentList:List<Question_fragment>):FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return StringBuilder("Question ").append(position+1).toString()
    }

    internal  var instance: MyFragmentAdapter ?= null

}