package com.ahsan.acadify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ahsan.acadify.Adapters.ViewPagerManegementAdapter

class LibraryManagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_management)

        val viewPager = findViewById<ViewPager>(R.id.fragmentContainer)
        viewPager.adapter = ViewPagerManegementAdapter(supportFragmentManager)

        val tabLayout = findViewById<TabLayout>(R.id.include)
        tabLayout.setupWithViewPager(viewPager)
    }
}