package dev.bonch.herehackpurify.fragments.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.bonch.herehackpurify.R

class ChooseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            LayoutInflater.from(container!!.context)
                .inflate(R.layout.fragment_choose, container, false)
        return view
    }
}