package android.example.instagram.ui.Profile

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.example.instagram.MainActivity
import android.example.instagram.R
import android.example.instagram.databinding.ActivityAccountSettingsBinding
import android.example.instagram.models.Users
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
            .also { setContentView(it.root)}

        binding.tvChangePhoto.setOnClickListener {
            showChangeAvatarDialog()
        }
        binding.ivChangePhoto.setOnClickListener {
            showChangeAvatarDialog()
        }
        binding.tvPersonalInfo.setOnClickListener {
            showChangePersonalInfo()
        }
        binding.ivApprove.setOnClickListener{
            if (checker == "clicked"){
                uploadImageAndInfo()
            }
            else{
                updateUserInfoOnly()
            }
        }
        binding.ivClose.setOnClickListener {
            val intent=Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage
            .getInstance()
            .reference
            .child("Profile Pictures")
        userInfo()
    }

    private fun uploadImageAndInfo() {
        when{
            imageUri == null ->
                Toast.makeText(this, "Выберите Аватар", Toast.LENGTH_SHORT).show()
            binding.etFullName.text.toString() == "" ->
                Toast.makeText(this, "Пожалуйста заполните поле 'Имя'", Toast.LENGTH_SHORT)
                    .show()
            binding.etUserName.text.toString() == "" ->
                Toast.makeText(this, "Пожалуйста заполните поле 'Имя пользователя'", Toast.LENGTH_SHORT)
                    .show()
            binding.etWebSite.text.toString() == "" ->
                Toast.makeText(this, "Пожалуйста заполните поле 'Сайт'", Toast.LENGTH_SHORT)
                    .show()
            binding.etBio.text.toString() == "" ->
                Toast.makeText(this, "Пожалуйста заполните поле 'Биография'", Toast.LENGTH_SHORT)
                    .show()
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Редактирование профиля")
                progressDialog.setMessage("Информация обновляется")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!
                    .child(firebaseUser.uid + ".jpg")
                val uploadTask:StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase
                            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                            .reference
                            .child("Users")

                        val userMap = mutableMapOf<String, Any>(
                            "fullName" to binding.etFullName.text.toString(),
                            "userName" to binding.etUserName.text.toString().lowercase(),
                            "website" to binding.etWebSite.text.toString().lowercase(),
                            "bio" to binding.etBio.text.toString(),
                            "image" to myUrl
                        )
                        ref.child(firebaseUser.uid).updateChildren(userMap)
                        Toast.makeText(
                                this,
                                "Информация аккаунта была удачно обновлена!",
                                Toast.LENGTH_LONG
                            ).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                }
            }
        }

    }

    private fun updateUserInfoOnly() {
        if (binding.etFullName.text.toString() == ""){
            Toast.makeText(this, "Пожалуйста заполните поле 'Имя'", Toast.LENGTH_SHORT)
                .show()
        }
        else if(binding.etUserName.text.toString() == ""){
            Toast.makeText(this, "Пожалуйста заполните поле 'Имя Пользователя'", Toast.LENGTH_SHORT)
                .show()
        }
        else if(binding.etWebSite.text.toString() == ""){
            Toast.makeText(this, "Пожалуйста заполните поле 'Сайт'", Toast.LENGTH_SHORT)
                .show()
        }
        else if(binding.etBio.text.toString() == ""){
            Toast.makeText(this, "Пожалуйста заполните поле 'Биография'", Toast.LENGTH_SHORT)
                .show()
        }
        else{
            val userRef = FirebaseDatabase
                .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                .reference
                .child("Users")
            val userMap = mutableMapOf<String, Any>(
                "fullName" to binding.etFullName.text.toString(),
                "userName" to binding.etUserName.text.toString().lowercase(),
                "website" to binding.etWebSite.text.toString().lowercase(),
                "bio" to binding.etBio.text.toString()
            )
            userRef.child(firebaseUser.uid).updateChildren(userMap)

            Toast.makeText(this, "Информация аккаунта была удачно обновлена!", Toast.LENGTH_LONG)
                .show()
            val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun userInfo(){
        val userRef = FirebaseDatabase
            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Users")
            .child(firebaseUser.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(Users::class.java)
                    Picasso
                        .get()
                        .load(user?.getImage())
                        .placeholder(R.drawable.default_ava)
                        .into(binding.ivChangePhoto)
                    binding.etUserName.setText(user!!.getUserName())
                    binding.etFullName.setText(user.getFullName())
                    binding.etBio.setText(user.getBio())
                    binding.etWebSite.setText(user.getWebSite())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun showChangePersonalInfo(){
        val intent = Intent(this, PersonalInformationActivity::class.java)
        startActivity(intent)
    }

    private fun showChangeAvatarDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_photo)

        val newProfilePhoto = dialog.findViewById<TextView>(R.id.tvNewPhotoProfile)
        val importFromFacebook = dialog.findViewById<View>(R.id.tvImportFromFacebook)
        val useAvatar = dialog.findViewById<View>(R.id.tvUseAvatar)
        val deleteProfilePhoto = dialog.findViewById<View>(R.id.tvDeleteProfilePhoto)

        newProfilePhoto.setOnClickListener {
            checker = "clicked"
            CropImage
                .activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingsActivity)
                dialog.dismiss()
        }
        importFromFacebook.setOnClickListener {
            Toast.makeText(this,"Импортирвоать из Facebook", Toast.LENGTH_LONG).show()
        }
        useAvatar.setOnClickListener {
            Toast.makeText(this,"Использовать аватар", Toast.LENGTH_LONG).show()
        }
        deleteProfilePhoto.setOnClickListener {
            Toast.makeText(this,"Удалить фото профиля", Toast.LENGTH_LONG).show()
        }
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            binding.ivChangePhoto.setImageURI(imageUri)
        }
    }
}
