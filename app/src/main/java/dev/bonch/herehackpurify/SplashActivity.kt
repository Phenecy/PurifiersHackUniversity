package dev.bonch.herehackpurify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.HandlerCompat.postDelayed

class SplashActivity : AppCompatActivity() {

    private lateinit var splashLogo: ImageView
    private lateinit var animation: Animation
    private lateinit var splashLayout: ConstraintLayout

    private val SPLASH_DURATION = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashLayout = findViewById(R.id.splash_layout)
        splashLogo = findViewById(R.id.splash_logo)
        animation = AnimationUtils.loadAnimation(baseContext, R.anim.logo_rotation)
    }

    override fun onResume() {
        super.onResume()
        initFunctionality()
    }

    private fun initFunctionality() {
        splashLayout.postDelayed({
            splashLogo.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationRepeat(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {

                }
            })
        }, SPLASH_DURATION.toLong())
    }
}