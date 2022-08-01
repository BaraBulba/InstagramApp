package android.example.instagram.Adapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

//class SectionPagerAdapter(private val mCtx: Context, fm: FragmentManager, data: Bundle):
//    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
//
//    private var fragmentBundle: Bundle
//
//    init {
//        fragmentBundle = data
//    }
//
//    override fun getCount(): Int = 2
//
//    override fun getItem(position: Int): Fragment {
//        var fragment: Fragment? = null
//        when(position){
//            0 -> fragment = FollowersFragment()
//            1 -> fragment = FollowingFragment()
//        }
//        fragment?.arguments = this.fragmentBundle
//        return fragment as Fragment
//    }
//}