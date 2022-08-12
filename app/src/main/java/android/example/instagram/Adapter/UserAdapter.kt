package android.example.instagram.Adapter


import android.content.Context
import android.example.instagram.Fragments.ProfileFragment
import android.example.instagram.R
import android.example.instagram.models.Users
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserAdapter (private var mContext: Context,
                   private var mUsers: List<Users>,
                   private var isFragment: Boolean = false)
    : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_user_search, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = mUsers[position]
        holder.userNameTV.text = user.getUserName()
        holder.fullNameTV.text = user.getFullName()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.default_ava)
            .into(holder.userProfileIV)

        checkFollowingStatus(user.getUid(), holder.followBtn)

        holder.followBtn.setOnClickListener{
            if (holder.followBtn.text.toString() == "Подписаться"){

                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                        .reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful){

                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                                        .reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }
            else{
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                        .reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){

                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                                        .reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful){
                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }

        holder.itemView.setOnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.getUid())
            pref.apply()
            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }
    }

    private fun checkFollowingStatus(uid: String, followBtn: Button) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase
                .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                .reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }
        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(uid).exists()){
                    followBtn.text = "Подписки"
                    followBtn.setBackgroundColor(Color.GRAY)
                }
                else{
                    followBtn.text = "Подписаться"
                    followBtn.setBackgroundColor(Color.argb(100, 44, 162, 216))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    class UserViewHolder(@NonNull itemView: View) :RecyclerView.ViewHolder(itemView){
        var userNameTV: TextView = itemView.findViewById(R.id.itemSearchUserName)
        var fullNameTV: TextView = itemView.findViewById(R.id.itemSearchFullName)
        var userProfileIV: ImageView = itemView.findViewById(R.id.itemSearchAvatar)
        var followBtn: Button = itemView.findViewById(R.id.followBtn)
    }


}