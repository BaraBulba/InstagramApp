package android.example.instagram.ui.SignInAndLogIn

import android.content.Intent
import android.example.instagram.databinding.ActivityHelpWithEnterBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HelpWithEnterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpWithEnterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpWithEnterBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.buttonNext.setOnClickListener {
            startActivity(Intent(this, AccessToYourAccountActivity::class.java))
        }
    }
}