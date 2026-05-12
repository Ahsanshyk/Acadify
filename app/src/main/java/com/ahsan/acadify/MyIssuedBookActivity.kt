package com.ahsan.acadify

import ViewPagerAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MyIssuedBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_issued_book)

        val viewPager = findViewById<ViewPager>(R.id.fragmentContainer)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)

        val tabLayout = findViewById<TabLayout>(R.id.include)
        tabLayout.setupWithViewPager(viewPager)
    }
}