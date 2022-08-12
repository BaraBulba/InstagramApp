package android.example.instagram.ui.Profile

import android.example.instagram.databinding.ActivityPersonalInformationBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PersonalInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalInformationBinding.inflate(layoutInflater).also { setContentView(it.root)}
        setSupportActionBar(binding.myToolBar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


}