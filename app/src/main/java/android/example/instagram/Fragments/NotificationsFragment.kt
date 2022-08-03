package android.example.instagram.Fragments

import android.example.instagram.Adapter.NotificationsAdapter
import android.example.instagram.R
import android.example.instagram.databinding.FragmentNotificationsBinding
import android.example.instagram.models.Notifications
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class FavoriteFragment : Fragment(R.layout.fragment_notifications) {

    private lateinit var binding: FragmentNotificationsBinding
    private var notificationsAdapter: NotificationsAdapter? = null
    private var notificationList:MutableList<Notifications>?=null
    private  var firebaseUser: FirebaseUser?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser


        return binding.root
    }


}