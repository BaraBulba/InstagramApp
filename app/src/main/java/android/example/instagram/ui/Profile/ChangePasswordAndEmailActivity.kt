package android.example.instagram.ui.Profile

import android.content.Intent
import android.example.instagram.MainActivity
import android.example.instagram.databinding.ActivityChangePasswordAndEmailBinding
import android.example.instagram.models.Users
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChangePasswordAndEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordAndEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordAndEmailBinding.inflate(layoutInflater)
            .also { setContentView(it.root)}

        setSupportActionBar(binding.myToolBar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.ivBack.setOnClickListener {
            val intent= Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        binding.btnSaveSettings.setOnClickListener {
            changeEmail()
        }
        userInfo()
    }


    private fun userInfo(){
        val userRef = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(Users::class.java)
                    binding.editTextEmailSettings.setText(user!!.getEmail())
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun changeEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        val email = binding.editTextEmailSettings.text.toString()
        val pass = binding.editTextPasswordSettings.text.toString()

        if (email.isEmpty()){
            binding.editTextEmailSettings.error = "Email is needed!"
            binding.editTextEmailSettings.requestFocus()
            return
        }

        if (pass.isEmpty()){
            binding.editTextPasswordSettings.error = "Password is needed!"
            binding.editTextEmailSettings.requestFocus()
            return
        }

        user.let {
            it?.reauthenticate(
                EmailAuthProvider.getCredential(it.email!!,pass)
            )?.addOnCompleteListener { task ->
               if (task.isSuccessful){
                   user?.updateEmail(email)?.addOnCompleteListener { task ->
                       if (task.isSuccessful){
                           val userRef = FirebaseDatabase
                               .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                               .reference
                               .child("Users")
                           val userMap = HashMap<String, Any>()
                           userMap["email"] = binding.editTextEmailSettings.text.toString()
                           userRef.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(userMap)
                           Toast.makeText(this,
                               "Электронный адрес был успешно обновлен!",
                                Toast.LENGTH_SHORT).show()
                           val intent= Intent(this, MainActivity::class.java)
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                           startActivity(intent)
                           finish()
                       }
                       else{
                           Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT)
                               .show()
                       }
                   }
               }
                else{
                   Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT)
                       .show()
               }
            }
        }
    }
}