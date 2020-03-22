package dev.bonch.herehackpurify.activities

import android.Manifest
import android.content.Context
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
import dev.bonch.herehackpurify.R

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
            navController.navigate(R.id.action_nav_home_to_statusFragment)
        }




//        navController.currentDestination.label
    }

    private fun setupNavigationDrawer() {
        navController = Navigation.findNavController(this, R.id.fragment_container)
    }
}