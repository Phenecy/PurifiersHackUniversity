package dev.bonch.herehackpurify.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import dev.bonch.herehackpurify.R
import dev.bonch.herehackpurify.fragments.SMSDialogFragment

class SplashActivity : AppCompatActivity() {

    //Splash views
    private lateinit var splashLogo: ImageView
    private lateinit var animation: Animation
    private lateinit var splashLayout: ConstraintLayout

    //Auth views
    private lateinit var ePhone: EditText
    private lateinit var bSendSms: Button

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
//                    auth = ...
//                    val currentUser = auth.
//                    updateUI(currentUser)
                    updateUI()
                }
            })
        }, SPLASH_DURATION.toLong())
    }

    //    private fun updateUI(user: User?)
    private fun updateUI() {
//        if (user != null) {
//            intent = Intent(this@SplashActivity, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//            startActivity(intent)
//            finish()
//        }
        setContentView(R.layout.auth_afterload)

        ePhone = findViewById(R.id.user_name_et)
        bSendSms = findViewById(R.id.sms_send_btn)

        bSendSms.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("sms_code")
            if (prev != null) {
                fragmentTransaction.remove(prev)
            }
            fragmentTransaction.addToBackStack(null)
            val dialogFragment = SMSDialogFragment()
            dialogFragment.show(fragmentTransaction, "reset_pass")

        }
    }
}