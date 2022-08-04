package android.example.instagram.Fragments

import android.content.Context
import android.example.instagram.Adapter.MyPostsAdapter
import android.example.instagram.Adapter.MySavedPhotosAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.instagram.R
import android.example.instagram.databinding.FragmentMyPostsProfileBinding
import android.example.instagram.databinding.FragmentSavedPostsProfileBinding
import android.example.instagram.models.Post
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SavedPostsProfileFragment : Fragment() {

    private lateinit var binding: FragmentSavedPostsProfileBinding

    private lateinit var profileId: String
    private var savesAdapter: MySavedPhotosAdapter? = null
    private var savesList: MutableList<Post>? = null
    private var firebaseUser: FirebaseUser? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedPostsProfileBinding.inflate(layoutInflater, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val gridLayoutManager = GridLayoutManager(context,3, GridLayoutManager.VERTICAL, false)
        binding.recyclerViewMySavedPhotos.setHasFixedSize(true)
        binding.recyclerViewMySavedPhotos.layoutManager = gridLayoutManager
        savesList = ArrayList()
        savesAdapter = context?.let { MySavedPhotosAdapter(it, savesList as ArrayList<Post>) }
        binding.recyclerViewMySavedPhotos.adapter = savesAdapter


        savesAdapter!!.notifyDataSetChanged()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("profileId", "none")!!
        }
        retrieveSavedPhotos()
    }


    private fun retrieveSavedPhotos() {
        val savedIds: MutableList<String> = ArrayList()
        FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Saves")
            .child(firebaseUser!!.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snapshot in snapshot.children){
                        savedIds.add(snapshot.key!!)
                    }
                    FirebaseDatabase
                        .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                        .reference
                        .child("Posts")
                        .addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot1: DataSnapshot) {
                                savesList?.clear()
                                for (snapshot1 in snapshot1.children){
                                    val post = snapshot1.getValue(Post::class.java)
                                    for (id in savedIds){
                                        if (post!!.getPostId() == (id)){
                                            savesList!!.add(post)
                                        }
                                    }
                                }
                                savesAdapter!!.notifyDataSetChanged()
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }



    companion object{
        fun newInstance() = SavedPostsProfileFragment()
    }



}