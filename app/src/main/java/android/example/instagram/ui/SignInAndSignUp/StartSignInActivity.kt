package android.example.instagram.ui.SignInAndSignUp

import android.content.Intent
import android.example.instagram.MainActivity
import android.example.instagram.databinding.ActivityStartSignInBinding
import android.example.instagram.models.SampleSearchModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener

class StartSignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartSignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartSignInBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.tvSignUpViaEmailOrPhone.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.tvALreadyHaveAnAcc.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.textView3.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.signInViaFacebook.setOnClickListener {
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

    private fun initData(): ArrayList<SampleSearchModel> {
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
        items.add(SampleSearchModel("Portuguese"))
        return items
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}