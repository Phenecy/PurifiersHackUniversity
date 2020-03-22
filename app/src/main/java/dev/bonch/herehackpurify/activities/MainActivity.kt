package dev.bonch.herehackpurify.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.here.android.mpa.common.ApplicationContext
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.common.Version
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import dev.bonch.herehackpurify.Main
import dev.bonch.herehackpurify.R
import dev.bonch.herehackpurify.fragments.nav.HomeFragment
import dev.bonch.herehackpurify.model.pojo.Point
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var mainFab: FloatingActionButton
    private lateinit var location: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigationDrawer()
        if (intent.hasExtra("data")){
            location = intent.getStringExtra("data")!!
            //Main.point = Point(location, 1, 1)
            navController.navigate(R.id.action_nav_home_to_statusFragment)
        }

        fab.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }
//        navController.currentDestination.label
    }

    private fun setupNavigationDrawer() {
        navController = Navigation.findNavController(this, R.id.fragment_container)
    }
}