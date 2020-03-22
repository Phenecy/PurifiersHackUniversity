package dev.bonch.herehackpurify.fragments.nav

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.here.android.mpa.guidance.NavigationManager
import dev.bonch.herehackpurify.R
import dev.bonch.herehackpurify.activities.LocationActivity
import dev.bonch.herehackpurify.activities.RegistrationActivity
import kotlinx.android.synthetic.main.auth_afterload.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardView1.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_binCreateFragment)
        }
    }
}