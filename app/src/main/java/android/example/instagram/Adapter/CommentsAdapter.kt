package android.example.instagram.Adapter

import android.content.Context
import android.example.instagram.R
import android.example.instagram.models.Comments
import android.example.instagram.models.Users
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentsAdapter(
    private val mContext: Context,
    private val mComment: List<Comments>
): RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    inner class ViewHolder(@NonNull itemView: View) :RecyclerView.ViewHolder(itemView){
        var commentatorAvatar = itemView.findViewById<CircleImageView>(R.id.itemCommentatorAvatar)!!
        var commentatorUserName = itemView.findViewById<TextView>(R.id.itemCommentatorUserName)!!
        var commentatorComment = itemView.findViewById<TextView>(R.id.itemCommentatorMessage)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_comments, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = mComment [position]
        holder.commentatorComment.text = comment.getComment()

        getCommentsAvatars(comment.getPublisher(), holder.commentatorAvatar, holder.commentatorUserName)
    }

    private fun getCommentsAvatars(comment: String, image: CircleImageView, userName: TextView) {
        FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Users")
            .child(comment)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)
                    userName.text = user!!.getUserName()
                    Picasso
                        .get()
                        .load(user.getImage())
                        .placeholder(R.drawable.default_ava)
                        .into(image)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    override fun getItemCount(): Int {
       return mComment.size
    }
}