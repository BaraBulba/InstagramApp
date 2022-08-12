package android.example.instagram.Fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.example.instagram.Adapter.UserAdapter
import android.example.instagram.Adapter.ViewPagerAdapter
import android.example.instagram.ui.Profile.AccountSettingsActivity
import android.example.instagram.R
import android.example.instagram.databinding.FragmentProfileBinding
import android.example.instagram.models.Post
import android.example.instagram.models.Users
import android.example.instagram.ui.Profile.ChangePasswordAndEmailActivity
import android.example.instagram.ui.SignInAndSignUp.StartSignInActivity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var profileId: String
    private var firebaseUser: FirebaseUser? = null
    private var userAdapter: UserAdapter? = null

    private val fragmentList = listOf(
        MyPostsProfileFragment.newInstance(),
        SavedPostsProfileFragment.newInstance()
    )
    private val fragmentListImages = listOf(
        R.drawable.ic_photo,
        R.drawable.ic_photos_with
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("profileId", "none")!!
        }
        if (profileId == firebaseUser!!.uid){
            binding.editProfile.text = "Редактировать профиль"
        }
        else if (profileId != firebaseUser!!.uid){
            checkFollowAndFollowing()
        }

        return binding.root
    }



    private fun checkFollowAndFollowing() {
        val followRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase
                .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                .reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }
        if (followRef != null){
            followRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileId).exists()){
                        binding.editProfile.text = "Подписки"
                        userAdapter?.notifyDataSetChanged()
                    }
                    else{
                        binding.editProfile.text = "Подписаться"
                        userAdapter?.notifyDataSetChanged()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editProfile.setOnClickListener {

            val getButtonText = binding.editProfile.text.toString()
            when{
                getButtonText == "Редактировать профиль" -> activity?.let {
                    val intent = Intent(it, AccountSettingsActivity::class.java)
                    it.startActivity(intent)}

                getButtonText == "Подписаться" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                            .reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                            .reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                    }

                }

                getButtonText == "Подписки" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                            .reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                            .reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    }
                }

            }
        }
        binding.menuProfile.setOnClickListener {
            showMenuDialog()
        }
        val adapter = ViewPagerAdapter((activity as AppCompatActivity),fragmentList)
        binding.viewPagerLayout.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPagerLayout){
            tab, pos -> tab.setIcon(fragmentListImages[pos])
        }.attach()

        getFollowers()
        getFollowing()
        userInfo()
        getPosts()
    }




    private fun showMenuDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.profile_menu_dialog)

        val settings = dialog.findViewById<FrameLayout>(R.id.settingsBox)
        val archive = dialog.findViewById<FrameLayout>(R.id.archiveBox)
        val yourAction = dialog.findViewById<FrameLayout>(R.id.yourActionsBox)
        val qrCode = dialog.findViewById<FrameLayout>(R.id.qrCodeBox)
        val saved = dialog.findViewById<FrameLayout>(R.id.savedBox)
        val closeFriends = dialog.findViewById<FrameLayout>(R.id.closeFriendsBox)
        val favorites = dialog.findViewById<FrameLayout>(R.id.favoritesBox)
        val covid = dialog.findViewById<FrameLayout>(R.id.covidBox)
        val exchangeMessage = dialog.findViewById<FrameLayout>(R.id.exchangeMessageBox)
        val logout = dialog.findViewById<FrameLayout>(R.id.logoutBox)

        settings.setOnClickListener {
            Toast.makeText(context,"Настройки", Toast.LENGTH_SHORT).show()
            activity?.let {
                val intent = Intent(it, ChangePasswordAndEmailActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        archive.setOnClickListener {
            Toast.makeText(context,"Архив", Toast.LENGTH_SHORT).show()
        }
        yourAction.setOnClickListener {
            Toast.makeText(context,"Ваши действия", Toast.LENGTH_SHORT).show()
        }
        qrCode.setOnClickListener {
            Toast.makeText(context,"QR-код", Toast.LENGTH_SHORT).show()
        }
        saved.setOnClickListener {
            Toast.makeText(context,"Сохраненное", Toast.LENGTH_SHORT).show()
        }
        closeFriends.setOnClickListener {
            Toast.makeText(context,"Близкие друзья", Toast.LENGTH_SHORT).show()
        }
        favorites.setOnClickListener {
            Toast.makeText(context,"Избранное", Toast.LENGTH_SHORT).show()
        }
        covid.setOnClickListener {
            Toast.makeText(context,"Центр информации о COVID-19", Toast.LENGTH_SHORT).show()
        }
        exchangeMessage.setOnClickListener {
            Toast.makeText(context,"Обновить функции обмена сообщениями", Toast.LENGTH_SHORT).show()
        }
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.let {
                val intent = Intent(it, StartSignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun getPosts() {
        val postsRef = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var i = 0
                for(snapshot in snapshot.children)
                {
                    val post = snapshot.getValue(Post::class.java)
                    if(post!!.getPublisher() == (profileId))
                    {
                        i += 1
                    }
                }
                binding.totalPosts.text = "" + i
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getFollowers(){
        val followersRef = FirebaseDatabase
                .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                .reference
                .child("Follow").child(profileId)
                .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    binding.totalFollowers.text = snapshot.childrenCount.toString()
                    userAdapter?.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowing(){
        val followersRef = FirebaseDatabase
                .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                .reference
                .child("Follow").child(profileId)
                .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    binding.totalFollowing.text = snapshot.childrenCount.toString()
                    userAdapter?.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun userInfo(){
        val userRef = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Users")
            .child(profileId)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val user = snapshot.getValue(Users::class.java)
                    Picasso
                        .get()
                        .load(user?.getImage())
                        .placeholder(R.drawable.default_ava)
                        .into(binding.profileImage)
                    binding.profileUserNameTV.text = user!!.getUserName()
                    binding.profileFullNameTV.text = user.getFullName()
                    binding.profileBioTV.text = user.getBio()
                    binding.profileWebSiteTV.text = user.getWebSite()
                    binding.profileWebSiteTV.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser?.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser?.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser?.uid)
        pref?.apply()
    }


}


