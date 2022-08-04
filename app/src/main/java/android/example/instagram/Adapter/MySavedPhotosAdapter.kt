package android.example.instagram.Adapter

import android.content.Context
import android.example.instagram.R
import android.example.instagram.models.Post
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class MySavedPhotosAdapter(
    private val mContext: Context,
    private val mPostsSaved: List<Post>
): RecyclerView.Adapter<MySavedPhotosAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView) {
        var imageViewProfileSavedPhotos = itemView.findViewById<ImageView>(R.id.imageViewProfileSavedPhotos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.item_profile_saved_photos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val posts = mPostsSaved[position]
        Picasso
            .get()
            .load(posts.getPostImage())
            .into(holder.imageViewProfileSavedPhotos)
    }

    override fun getItemCount(): Int {
        return mPostsSaved.size
    }
}