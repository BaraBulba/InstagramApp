package android.example.instagram.ui.SignInAndLogIn

import android.example.instagram.databinding.ActivityAccessToYourAccountBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class AccessToYourAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccessToYourAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessToYourAccountBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        binding.sendAnEmailTextLayout.setOnClickListener {
            Toast.makeText(this, "Send an Email clicked", Toast.LENGTH_SHORT).show()
        }

        binding.sendSmsTextLayout.setOnClickListener {
            Toast.makeText(this, "Send SMS clicked", Toast.LENGTH_SHORT).show()
        }

        setSupportActionBar(binding.myToolBarAccessToYourAccount)
        supportActionBar?.setTitle(null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }
}