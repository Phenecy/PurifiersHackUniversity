package dev.bonch.herehackpurify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import dev.bonch.herehackpurify.R
import dev.bonch.herehackpurify.model.pojo.Client
import dev.bonch.herehackpurify.model.repository.TestRep
import kotlinx.android.synthetic.main.activity_registration.*

private lateinit var bContinue: Button

class RegistrationActivity : AppCompatActivity() {
    var phone: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        phone = intent.extras?.getString("dev.bonch.herehackpurify.PHONE_KEY") ?: ""


        bContinue = findViewById(R.id.reg_continue_btn)
        bContinue.setOnClickListener {
            createClient()
        }
    }

    private fun createClient(){
        val client = Client(
            0,
            user_phone_et.text.toString(),
            user_phone_et.text.toString(),
            phone,
            birthday_et.text.toString().toInt()
        )
        TestRep.createClient(this, client)
    }

    fun regSuccess(){
        val intent = Intent(this, TutorialActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}