package android.example.instagram.Fragments

import android.content.Context
import android.example.instagram.Adapter.NotificationsAdapter
import android.example.instagram.Adapter.PostsAdapter
import android.example.instagram.R
import android.example.instagram.databinding.FragmentNotificationsBinding
import android.example.instagram.models.Notifications
import android.example.instagram.models.Post
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList


class FavoriteFragment : Fragment(R.layout.fragment_notifications) {

    private lateinit var binding: FragmentNotificationsBinding
    private var notificationsAdapter: NotificationsAdapter? = null
    private var notificationList:MutableList<Notifications>?=null
    private var firebaseUser: FirebaseUser?=null
    private var postId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        postId = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)!!.getString("postid", "none")
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.recyclerViewNotifications.layoutManager = linearLayoutManager
        notificationList = ArrayList()
        notificationsAdapter = context?.let { NotificationsAdapter(it, notificationList as ArrayList<Notifications>) }
        binding.recyclerViewNotifications.adapter = notificationsAdapter

        readNotifications()


        return binding.root
    }

    private fun readNotifications() {
        FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    notificationList?.clear()
                    for (snapshot in snapshot.children){
                        notificationList!!.add(snapshot.getValue(Notifications::class.java)!!)
                    }
                    Collections.reverse(notificationList)
                    notificationsAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }


}