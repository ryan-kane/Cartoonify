package com.example.cartoonify.Pixelate

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

import androidx.viewpager2.adapter.FragmentStateAdapter

class PixelatePagerAdapter(f: Fragment): FragmentStateAdapter(f) {
    private val options = arrayListOf<Int>()

    override fun getItemCount(): Int {
        return options.size
    }

    override fun createFragment(position: Int): Fragment {
        TODO("Not yet implemented")
    }


}