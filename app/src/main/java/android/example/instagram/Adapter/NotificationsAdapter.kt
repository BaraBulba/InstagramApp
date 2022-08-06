package android.example.instagram.Adapter

import android.content.Context
import android.example.instagram.R
import android.example.instagram.models.Notifications
import android.example.instagram.models.Post
import android.example.instagram.models.Users
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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

class NotificationsAdapter(
    private var mContext: Context,
    private var mNotifications: List<Notifications>)
    : RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>(){

    private var firebaseUser: FirebaseUser? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.item_notifications, parent, false)
        return NotificationsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val notification = mNotifications[position]

        userInfo(holder.imageViewAvatar, holder.userNameNotifications, notification.getUserId())
        holder.descriptionNotifications.text = notification.getText()
        if (notification.getIsPost()){
            holder.imageLikedPhoto.visibility = View.VISIBLE
            getPostedImg(holder.imageLikedPhoto, notification.getPostId())
        }
        else{
            holder.imageLikedPhoto.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return mNotifications.size
    }

    private fun userInfo(imageViewAvatar: CircleImageView, userNameNotifications: TextView, userId: String) {
        val userRef = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Users")
            .child(userId)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)
                    if (user!!.getImage().isEmpty()) {
                        imageViewAvatar.setImageResource(R.drawable.default_ava)
                    } else {
                        Picasso
                            .get()
                            .load(user!!.getImage())
                            .placeholder(R.drawable.default_ava)
                            .into(imageViewAvatar)
                        userNameNotifications.text = user!!.getUserName()
                    }
                }


            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun getPostedImg(postimg:ImageView, postid:String?) {

        val postRef= FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Posts")
            .child(postid!!)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot)
            {
                val post = snapshot.getValue(Post::class.java)
                Picasso.get().load(post!!.getPostImage()).into(postimg)
            }
        })
    }

    inner class NotificationsViewHolder(@NonNull itemView: View)
        : RecyclerView.ViewHolder(itemView){
        var imageViewAvatar: CircleImageView = itemView.findViewById(R.id.itemNotificationsAvatar)
        var imageLikedPhoto: ImageView = itemView.findViewById(R.id.imageViewNotificationsLiked)
        var followBtn: Button = itemView.findViewById(R.id.followNotificationsBtn)
        var userNameNotifications: TextView = itemView.findViewById(R.id.itemNotificationsUserName)
        var descriptionNotifications: TextView = itemView.findViewById(R.id.itemNotificationsDescription)
    }

}