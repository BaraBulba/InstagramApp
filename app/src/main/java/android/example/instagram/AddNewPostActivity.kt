package android.example.instagram

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.example.instagram.databinding.ActivityAddNewPostBinding
import android.example.instagram.databinding.ActivityPersonalInformationBinding
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class AddNewPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewPostBinding
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewPostBinding.inflate(layoutInflater).also { setContentView(it.root)}
        setSupportActionBar(binding.myPostToolBar)
        supportActionBar?.setTitle(null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Posts Pictures")

        binding.ivPostApprove.setOnClickListener{
            uploadImage()
        }

        binding.ivPostClose.setOnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        CropImage
            .activity()
            .setAspectRatio(3,4)
            .start(this@AddNewPostActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            binding.ivNewPostPhoto.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {
        when {
            imageUri == null ->
                Toast.makeText(this, "Выберите Фото", Toast.LENGTH_SHORT).show()
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Информация обновляется")
                progressDialog.show()
                val fileRef = storagePostPicRef!!
                    .child(System.currentTimeMillis().toString() + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener (OnCompleteListener<Uri>{ task ->
                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()
                        val ref = FirebaseDatabase
                            .getInstance("https://instagram-clone-b8b4f-default-rtdb.europe-west1.firebasedatabase.app")
                            .reference
                            .child("Posts")
                        val postId = ref.push().key
                        val postMap = HashMap<String, Any>()
                        postMap["postid"] = postId!!
                        postMap["description"] = binding.etDescription.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postImage"] = myUrl
                        ref.child(postId).updateChildren(postMap)

                        Toast
                            .makeText(this, "Добавлена новая публикация", Toast.LENGTH_LONG)
                            .show()
                        val intent = Intent(this@AddNewPostActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }
                    else{
                        progressDialog.dismiss()
                    }
                })
        }

     }
    }
}