package android.example.instagram.ui.SignInAndLogIn

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.example.instagram.MainActivity
import android.example.instagram.databinding.ActivitySignUpBinding
import android.example.instagram.models.SampleSearchModel
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.buttonSignUp.setOnClickListener {
            CreateAccount()
        }

        binding.tvForgotYourDataSignUp.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.facebookBox.setOnClickListener {
            Toast.makeText(this, "Facebook Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.tvChooseLanguage.setOnClickListener {
            SimpleSearchDialogCompat(this, "ВЫБЕРИТЕ СВОЙ ЯЗЫК",
                "Поиск", null, initData(), SearchResultListener { searchDialog, item, _ ->
                    Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
                    binding.tvChooseLanguage.text = item.title
                    searchDialog.dismiss()
                }).show()
        }
    }

    private fun CreateAccount() {
        val fullName = binding.editTextFullName.text.toString()
        val userName = binding.editTextUserName.text.toString()
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        when{
            TextUtils.isEmpty(fullName) ->
                Toast.makeText(this,"Заполните поля ввода", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) ->
                Toast.makeText(this,"Заполните поля ввода", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) ->
                Toast.makeText(this,"Заполните поля ввода", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) ->
                Toast.makeText(this,"Заполните поля ввода", Toast.LENGTH_LONG).show()
            else -> {

                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("Регистрация")
                progressDialog.setMessage("Нужно подождать, это может занять некоторое время...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful)
                        {
                            saveUserInfo(userName, fullName, email, progressDialog)
                        }
                        else{
                            val message = task.exception.toString()
                            Toast.makeText(this,"Ошибка: $message", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(userName: String, fullName: String, email: String, progressDialog: ProgressDialog) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullName"] = fullName
        userMap["userName"] = userName.toLowerCase()
        userMap["email"] = email
        userMap["image"] = "http://surl.li/cnmbi"
        userMap["bio"] = "Стандартное описание профиля."

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    Toast
                        .makeText(this, "Аккаунт был удачно создан!", Toast.LENGTH_LONG)
                        .show()
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(currentUserID)
                        .child("Following").child(currentUserID)
                        .setValue(true)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else{
                    val message = task.exception.toString()
                    Toast.makeText(this,"Ошибка: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }

    }


    private fun initData(): ArrayList<SampleSearchModel>? {
        val items = ArrayList<SampleSearchModel>()
        items.add(SampleSearchModel("Ukrainian"))
        items.add(SampleSearchModel("Russian"))
        items.add(SampleSearchModel("English"))
        items.add(SampleSearchModel("German"))
        items.add(SampleSearchModel("French"))
        items.add(SampleSearchModel("Turkish"))
        items.add(SampleSearchModel("Spanish"))
        items.add(SampleSearchModel("Italic"))
        items.add(SampleSearchModel("Chinese"))
        items.add(SampleSearchModel("Korean"))
        items.add(SampleSearchModel("Japanese"))
        items.add(SampleSearchModel("Hindi"))
        items.add(SampleSearchModel("Portugies"))
        return items
    }
}