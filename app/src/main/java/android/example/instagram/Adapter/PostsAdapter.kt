package android.example.instagram.Adapter

import android.content.Context
import android.content.Intent
import android.example.instagram.R
import android.example.instagram.models.Post
import android.example.instagram.models.Users
import android.example.instagram.ui.CommentActivity
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostsAdapter
    (private val mContext: Context,
     private val mPost: List<Post>)
    : RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {

    private var firebaseUser: FirebaseUser? = null
    lateinit var avd: AnimatedVectorDrawableCompat
    lateinit var avd2: AnimatedVectorDrawable


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_posts_layout, parent, false)
        return PostsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post = mPost[position]
        var drawable = holder.likeAnimation.drawable
        holder.descriptionTV.text = post.getDescription()
        Picasso.get().load(post.getPostImage()).into(holder.postedIV)
        publisherInfo(holder.publisherIV, holder.userNameTV, holder.publisherTV, post.getPublisher())
        isLiked(post.getPostId(), holder.likeBtn, holder.postedIV)
        isSaved(post.getPostId(), holder.saveImageBtn)
        numberOfLikes(post.getPostId(), holder.likedTV)
        getComments(post.getPostId(), holder.commentsTV)


        holder.likeBtn.setOnClickListener{
            if (holder.likeBtn.tag.equals("like"))
            {
                FirebaseDatabase
                    .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                    .reference
                    .child("Likes").child(post.getPostId())
                    .child(firebaseUser!!.uid)
                    .setValue(true)
                pushNotification(post.getPostId(),post.getPublisher())
            }
            else
            {
                FirebaseDatabase
                    .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                    .reference
                    .child("Likes").child(post.getPostId())
                    .child(firebaseUser!!.uid)
                    .removeValue()
            }
        }

        holder.postedIV.setOnClickListener(object : DoubleClickListener(){
            override fun onDoubleClick(p0: View?) {
                if (holder.postedIV.tag.equals("like") ) {
                    FirebaseDatabase
                        .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                        .reference
                        .child("Likes").child(post.getPostId())
                        .child(firebaseUser!!.uid)
                        .setValue(true)
                    holder.likeAnimation.alpha = 0.90f
                    if (drawable is AnimatedVectorDrawableCompat){
                        avd = drawable
                        avd.start()
                    }
                    else if (drawable is AnimatedVectorDrawable){
                        avd2 = drawable
                        avd2.start()
                    }
                }
                else {
                    FirebaseDatabase
                        .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                        .reference
                        .child("Likes").child(post.getPostId())
                        .child(firebaseUser!!.uid)
                        .removeValue()
                }
            }
        })

        holder.saveImageBtn.setOnClickListener{
            if (holder.saveImageBtn.tag.toString() == "save")
            {
                FirebaseDatabase
                    .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                    .reference
                    .child("Saves").child(firebaseUser!!.uid)
                    .child(post.getPostId())
                    .setValue(true)
                Toast.makeText(mContext, " Post is Saved ", Toast.LENGTH_SHORT).show()
            }
            else
            {
                FirebaseDatabase
                    .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                    .reference
                    .child("Saves").child(firebaseUser!!.uid)
                    .child(post.getPostId())
                    .removeValue()
            }
        }

        holder.commentBtn.setOnClickListener {
            val intent = Intent(mContext, CommentActivity::class.java)
            intent.putExtra("postId", post.getPostId())
            intent.putExtra("authorId", post.getPublisher())
            mContext.startActivity(intent)
        }
        holder.commentsTV.setOnClickListener {
            val intent = Intent(mContext, CommentActivity::class.java)
            intent.putExtra("postId", post.getPostId())
            intent.putExtra("authorId", post.getPublisher())
            mContext.startActivity(intent)
        }
    }

    private fun pushNotification(postId:String, userid:String) {

        val notifyMap = HashMap<String, Any>()
        notifyMap["userId"] = FirebaseAuth.getInstance().currentUser!!.uid
        notifyMap["text"] = "нравится ваше фото"
        notifyMap["postId"] = postId
        notifyMap["isPost"] = true
        FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Notifications")
            .child(userid)
            .push()
            .setValue(notifyMap)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    abstract class DoubleClickListener: View.OnClickListener{
        private var lastClickTime: Long = 0
        override fun onClick(p0: View?) {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(p0)
            }
            lastClickTime = clickTime
        }

        abstract fun onDoubleClick(p0: View?)

        companion object{
            private const val DOUBLE_CLICK_TIME_DELTA: Long = 400
        }
    }

    inner class PostsViewHolder(@NonNull itemView: View) :RecyclerView.ViewHolder(itemView){
        var likeAnimation: ImageView = itemView.findViewById(R.id.imageViewLikeAnimation)
        var userNameTV: TextView
        var likedTV: TextView = itemView.findViewById(R.id.textViewPostLikes)
        var publisherTV: TextView
        var descriptionTV: TextView = itemView.findViewById(R.id.textViewPostDescription)
        var commentsTV: TextView = itemView.findViewById(R.id.textViewPostComments)
        var publisherIV: CircleImageView
        var postedIV: ImageView
        var likeBtn: ImageView = itemView.findViewById(R.id.imageViewPostLikeUnfilled)
        var commentBtn: ImageView = itemView.findViewById(R.id.imageViewPostLeaveComment)
        var sendMessageBtn: ImageView = itemView.findViewById(R.id.imageViewPostSendMessage)
        var saveImageBtn: ImageView = itemView.findViewById(R.id.imageViewPostSavePhoto)

        init {
            userNameTV = itemView.findViewById(R.id.textViewUserNamePost)
            publisherTV = itemView.findViewById(R.id.textViewPostPublisher)
            publisherIV = itemView.findViewById(R.id.circleImageViewSmallAvatar)
            postedIV = itemView.findViewById(R.id.imageViewPost)
            postedIV.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun getComments(postId: String, commentsTV: TextView) {
        FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Comments")
            .child(postId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentsTV.setText("Смотреть все комментарии (${snapshot.childrenCount})")
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun publisherInfo
                (publisherIV: CircleImageView,
                 userNameTV: TextView,
                 publisherTV: TextView,
                 publisherId: String, ) {
        val usersRef = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Users")
            .child(publisherId)
        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(Users::class.java)
                    Picasso
                        .get()
                        .load(user!!.getImage())
                        .placeholder(R.drawable.default_ava)
                        .into(publisherIV)
                    userNameTV.text = (user.getUserName())
                    publisherTV.text = (user.getUserName()) + " : "
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun isLiked (postid:String,imageView: ImageView, postedIV: ImageView) {
        val postRef =
            FirebaseDatabase
                .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                .reference
                .child("Likes")
                .child(postid)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.child(FirebaseAuth.getInstance().currentUser!!.uid).exists()) {
                    imageView.setImageResource(R.drawable.ic_heart_filled)
                    imageView.tag = "liked"
                    postedIV.tag = "liked"
                }
                else {
                    imageView.setImageResource(R.drawable.ic_heart_not_filled)
                    imageView.tag = "like"
                    postedIV.tag = "like"
                }
            }
        })
    }

    private fun isSaved (postid:String,imageView: ImageView) {
        val postRef =
            FirebaseDatabase
                .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                .reference
                .child("Saves")
                .child(firebaseUser!!.uid)
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.ic_bookmark_fill)
                    imageView.tag = "saved"
                }
                else {
                    imageView.setImageResource(R.drawable.ic_bookmark)
                    imageView.tag = "save"

                }
            }
        })
    }

    private fun numberOfLikes(postid: String, totalLikes: TextView){
        val postRef =
            FirebaseDatabase
                .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                .reference
                .child("Likes")
                .child(postid)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(datasnapshot: DataSnapshot) {
                totalLikes.text = "Нравится: " + datasnapshot.childrenCount.toString()
            }
        })
    }

}