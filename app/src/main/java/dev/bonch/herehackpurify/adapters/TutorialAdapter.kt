package dev.bonch.herehackpurify.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import dev.bonch.herehackpurify.fragments.viewpager.BecomeFragment
import dev.bonch.herehackpurify.fragments.viewpager.ChooseFragment
import dev.bonch.herehackpurify.fragments.viewpager.GainFragment
import java.util.ArrayList

@Suppress("DEPRECATION")
class TutorialAdapter(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager) {

    var mFrags: ArrayList<Fragment> =
        arrayListOf(ChooseFragment(), GainFragment(), BecomeFragment())


    override fun getItem(position: Int): Fragment {
        val index = position % mFrags.size
        return mFrags[index]
    }

    override fun getCount(): Int {
        return 3
    }
}