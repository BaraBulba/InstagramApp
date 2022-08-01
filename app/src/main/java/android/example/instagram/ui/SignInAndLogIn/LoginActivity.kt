package android.example.instagram.ui.SignInAndLogIn

import android.app.ProgressDialog
import android.content.Intent
import android.example.instagram.MainActivity
import android.example.instagram.databinding.ActivityLoginBinding
import android.example.instagram.models.SampleSearchModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.tvStillDontHaveAcc.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.facebookBox.setOnClickListener {
           Toast.makeText(this, "Facebook Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.tvForgotYourData.setOnClickListener {
            startActivity(Intent(this, HelpWithEnterActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {
            loginUser()
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

    private fun loginUser() {
        val email = binding.editTextEmailLogin.text.toString()
        val password = binding.editTextPasswordLogin.text.toString()

        when{
            TextUtils.isEmpty(email) ->
                Toast.makeText(this,"Заполните поля ввода", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) ->
                Toast.makeText(this,"Заполните поля ввода", Toast.LENGTH_LONG).show()
            else -> {
                val progressDialog = ProgressDialog(this@LoginActivity)
                progressDialog.setMessage("Вход...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful)
                        {
                            progressDialog.dismiss()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
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