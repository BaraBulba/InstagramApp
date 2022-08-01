package android.example.instagram.Fragments


import android.example.instagram.Adapter.UserAdapter
import android.example.instagram.databinding.FragmentSearchBinding
import android.example.instagram.models.Users
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private lateinit var binding: FragmentSearchBinding
    private var userAdapter: UserAdapter? = null
    private var mUsers: MutableList<Users>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        recyclerView = binding.searchRecyclerView
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUsers as ArrayList<Users>, true) }
        recyclerView?.adapter = userAdapter


        binding.editTextSearchView.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.editTextSearchView.text.toString() == "") {
                    mUsers?.clear()
                    userAdapter?.notifyDataSetChanged()
                } else {
                    recyclerView?.visibility = View.VISIBLE
                    retrieveUsers()
                    searchUser(s.toString().toLowerCase())
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
        return binding.root
    }

    private fun searchUser(input: String) {

        val query = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Users")
            .orderByChild("userName")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers?.clear()
                for (snapshot in dataSnapshot.children){
                    val users = snapshot.getValue(Users::class.java)
                    if (users != null){
                        mUsers?.add(users)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun retrieveUsers() {
        val usersSearchRef = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Users")
        usersSearchRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (binding.editTextSearchView.text.toString().equals("")){
                    mUsers?.clear()
                    for (snapshot in dataSnapshot.children){
                        val users = snapshot.getValue(Users::class.java)
                        if (!(users == null)){
                            mUsers?.add(users)
                        }
                        userAdapter?.notifyDataSetChanged()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Could not read from Database", Toast.LENGTH_LONG).show()
            }
        })
    }
}