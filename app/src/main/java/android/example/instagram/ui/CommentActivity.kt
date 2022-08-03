package android.example.instagram.ui

import android.example.instagram.Adapter.CommentsAdapter
import android.example.instagram.Adapter.PostsAdapter
import android.example.instagram.R
import android.example.instagram.databinding.ActivityCommentBinding
import android.example.instagram.models.Comments
import android.example.instagram.models.Post
import android.example.instagram.models.Users
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlin.collections.HashMap

class CommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentBinding
    private var postId: String? = null
    private var authorId: String? = null
    private var firebaseUser: FirebaseUser? = null
    private var commentsAdapter: CommentsAdapter? = null
    private var commentsList: MutableList<Comments>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater).also { setContentView(it.root) }

        setSupportActionBar(binding.myToolBar)
        supportActionBar?.setTitle(null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerViewComments.setHasFixedSize(true)
        binding.recyclerViewComments.layoutManager = linearLayoutManager
        commentsList = ArrayList()
        commentsAdapter = this.let { CommentsAdapter(it, commentsList as ArrayList<Comments>) }
        binding.recyclerViewComments.adapter = commentsAdapter

        val intent = getIntent()
        postId = intent.getStringExtra("postId")
        authorId = intent.getStringExtra("authorId")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        getUserImage()
        getComment()

        binding.textViewPublishTheComment.setOnClickListener {
            if (TextUtils.isEmpty(binding.editTextMessage.text.toString())){
                Toast.makeText(this, "Введите комментарий", Toast.LENGTH_SHORT).show()
            }
            else{
                putComment()
            }
        }
    }

    private fun getComment() {
        FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Comments")
            .child(postId!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentsList!!.clear()
                    for (snapshot in snapshot.children){
                        val comment = snapshot.getValue(Comments::class.java)
                        commentsList!!.add(comment!!)
                    }
                    commentsAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun putComment() {
        val commentMap = HashMap<String, Any>()
        commentMap ["comment"] = binding.editTextMessage.text.toString()
        commentMap["publisher"] = firebaseUser!!.uid

        FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Comments")
            .child(postId!!)
            .push()
            .setValue(commentMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast
                        .makeText(this, "Комментарий добавлен", Toast.LENGTH_SHORT)
                        .show()
                }
                else{
                    Toast
                        .makeText(this, task.exception?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun getUserImage() {
        FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Users")
            .child(firebaseUser!!.uid)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)
                    if (user!!.getImage().equals("default")){
                        binding.imageProfile.setImageResource(R.mipmap.ic_launcher)
                    }
                    else{
                        Picasso
                            .get()
                            .load(user!!.getImage())
                            .placeholder(R.drawable.default_ava)
                            .into(binding.imageProfile)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}