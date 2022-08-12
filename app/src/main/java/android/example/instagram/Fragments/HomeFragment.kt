package android.example.instagram.Fragments



import android.annotation.SuppressLint
import android.example.instagram.Adapter.PostsAdapter
import android.example.instagram.databinding.FragmentHomeBinding
import android.example.instagram.models.Post
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var postsAdapter: PostsAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.rvHome.layoutManager = linearLayoutManager
        postList = ArrayList()
        postsAdapter = context?.let { PostsAdapter(it, postList as ArrayList<Post>) }
        binding.rvHome.adapter = postsAdapter

        checkFollowings()

        return binding.root
    }

    private fun checkFollowings() {
        followingList = ArrayList()
        val followingRef = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")
        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (followingList as ArrayList<*>).clear()
                    for (snap in snapshot.children){
                        snap.key?.let {
                            (followingList as ArrayList<String>)
                                .add(it)
                        }
                        retrievePosts()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrievePosts() {
        val postsRef = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()
                for (snap in snapshot.children){
                    val posts = snap.getValue(Post::class.java)
                    for (userID in (followingList as ArrayList<*>)){
                        if (posts!!.getPublisher() == userID){

                            postList!!.add(posts)
                        }
                        postsAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

