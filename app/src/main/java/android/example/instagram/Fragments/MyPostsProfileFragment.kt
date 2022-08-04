package android.example.instagram.Fragments

import android.content.Context
import android.example.instagram.Adapter.MyPostsAdapter
import android.example.instagram.Adapter.PostsAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.instagram.R
import android.example.instagram.databinding.FragmentMyPostsProfileBinding
import android.example.instagram.models.Post
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MyPostsProfileFragment : Fragment() {

    private lateinit var binding: FragmentMyPostsProfileBinding
    private lateinit var profileId: String
    private var postsAdapter: MyPostsAdapter? = null
    private var postList: MutableList<Post>? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostsProfileBinding.inflate(layoutInflater, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val gridLayoutManager = GridLayoutManager(context,3,GridLayoutManager.VERTICAL, false)
        binding.recyclerViewMyPosts.setHasFixedSize(true)
        binding.recyclerViewMyPosts.layoutManager = gridLayoutManager
        postList = ArrayList()
        postsAdapter = context?.let { MyPostsAdapter(it, postList as ArrayList<Post>) }
        binding.recyclerViewMyPosts.adapter = postsAdapter

        postsAdapter!!.notifyDataSetChanged()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("profileId", "none")!!
        }
        retrievePosts()
    }


    private fun retrievePosts() {
        val postsRef = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()
                for (snapshot in snapshot.children){
                    val posts = snapshot.getValue(Post::class.java)
                        if (posts!!.getPublisher().equals(profileId))
                            postList!!.add(posts)
                }
                postsAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    companion object{
        fun newInstance() = MyPostsProfileFragment()
    }



}