package dev.bonch.herehackpurify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dev.bonch.herehackpurify.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private lateinit var viewPager: ViewPager
private lateinit var tabLayout: TabLayout
private lateinit var bContinue: Button

class TutorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        initViews()
        setViewPagerAdapter()
//        animateViewPager()

        tabLayout.setupWithViewPager(viewPager, true)
        bContinue.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

//    fun animateViewPager() {
//        CoroutineScope(Dispatchers.Main).launch {
//            while (true) {
//                for (i in 0..2) {
//                    delay(3000)
//                    viewPager.currentItem = i
//                }
//            }
//
//        }
//    }

    fun setViewPagerAdapter() {
        val adapter = initPagerAdapter()
        viewPager.adapter = adapter
    }

    fun initPagerAdapter(): PagerAdapter {
        val fragmentAdapter =
            dev.bonch.herehackpurify.adapters.TutorialAdapter(
                supportFragmentManager
            )
        return fragmentAdapter
    }

    private fun initViews() {
        bContinue = findViewById(R.id.tutorial_continue_btn)
        viewPager = findViewById(R.id.tutorial_pager)
        tabLayout = findViewById(R.id.tabDots)
    }
}