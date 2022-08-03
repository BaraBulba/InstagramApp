package android.example.instagram.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.instagram.R
import android.example.instagram.databinding.FragmentMyPostsProfileBinding
import android.example.instagram.databinding.FragmentSavedPostsProfileBinding


class SavedPostsProfileFragment : Fragment() {

    private lateinit var binding: FragmentSavedPostsProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedPostsProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    companion object{
        fun newInstance() = SavedPostsProfileFragment()
    }

}