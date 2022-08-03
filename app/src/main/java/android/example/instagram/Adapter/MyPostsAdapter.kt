package android.example.instagram.Adapter

import android.content.Context
import android.example.instagram.R
import android.example.instagram.models.Post
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class MyPostsAdapter(
    private val mContext: Context,
    private val mPosts: List<Post>
): RecyclerView.Adapter<MyPostsAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView) {
        var imageViewProfilePhotos = itemView.findViewById<ImageView>(R.id.imageViewProfilePhotos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.item_profile_posts_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val posts = mPosts[position]
        Picasso
            .get()
            .load(posts.getPostImage())
            .into(holder.imageViewProfilePhotos)
    }

    override fun getItemCount(): Int {
       return mPosts.size
    }
}