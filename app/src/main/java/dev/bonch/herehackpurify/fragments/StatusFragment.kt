package dev.bonch.herehackpurify.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.bonch.herehackpurify.R
import kotlinx.android.synthetic.main.fragment_status.*

class StatusFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cleareBin.setOnClickListener {
            //findNavController().navigate(R.id.action_nav_home_to_binCreateFragment) SEND REQUEST
        }
    }
}