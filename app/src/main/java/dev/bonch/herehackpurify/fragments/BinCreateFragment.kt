package dev.bonch.herehackpurify.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.bonch.herehackpurify.R
import dev.bonch.herehackpurify.activities.LocationActivity
import kotlinx.android.synthetic.main.fragment_bin_create.*
import kotlinx.android.synthetic.main.fragment_home.*

class BinCreateFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bin_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binCardView.setOnClickListener {
            var intent = Intent(context, LocationActivity::class.java)
            startActivity(intent)
        }
    }
}