package com.areeb.socialshare

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.areeb.socialshare.models.Post
import com.areeb.socialshare.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.FirestoreClient
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_post.*

private const val PICK_PHOTO_CODE = 1234
private lateinit var firebaseDb: FirebaseFirestore
private var signedInUser: User? = null
private lateinit var storageReference:StorageReference



class CreatePost : AppCompatActivity() {
    private var photoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        storageReference=FirebaseStorage.getInstance().reference
        firebaseDb= FirebaseFirestore.getInstance()
        firebaseDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)

            }
            .addOnFailureListener{
                Toast.makeText(this,"snapshot failed ",Toast.LENGTH_SHORT).show()


            }

        btnPickImage.setOnClickListener {
           val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type="image/*"
            if(imagePickerIntent.resolveActivity(packageManager) != null){
                startActivityForResult(imagePickerIntent,PICK_PHOTO_CODE)

            }
        }

        btnSubmit.setOnClickListener{
            handleSubmitButtonClick()
        }
    }

    private fun handleSubmitButtonClick() {
        if (photoUri ==  null){
            Toast.makeText(this,"No photo selected ",Toast.LENGTH_SHORT).show()

        }
        if(etDescription == null){
            Toast.makeText(this,"Description is empty",Toast.LENGTH_SHORT).show()
        }
        if (signedInUser == null){
            Toast.makeText(this,"Plz login ",Toast.LENGTH_SHORT).show()
            return
        }

        btnSubmit.isEnabled= false
        val photoUploadUri = photoUri as Uri
        val photoReference = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")

        //upload photo to Firebase storage

        photoReference.putFile(photoUploadUri)
            .continueWithTask { photoUploadTask ->
                // retrieve image url of uploaded image
                photoReference.downloadUrl
            }.continueWithTask { downloadUrlTask ->

                val post=Post(
                etDescription.text.toString(),
                downloadUrlTask.result.toString(),
                System.currentTimeMillis(),
                signedInUser)

                firebaseDb.collection("posts").add(post)

            }.addOnCompleteListener { postCreationTask ->
                btnSubmit.isEnabled=true
                if(!postCreationTask.isSuccessful){
                    Toast.makeText(this,"post created FAILED :| ",Toast.LENGTH_SHORT).show()
                }
                etDescription.text.clear()
                imageView.setImageResource(0)
                Toast.makeText(this,"post created Success :) ",Toast.LENGTH_SHORT).show()
                finish()



            }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_PHOTO_CODE){
            if (resultCode ==Activity.RESULT_OK){
           photoUri = data?.data
                imageView.setImageURI(photoUri)


            }else{
                Toast.makeText(this,"Image picker action canceled",Toast.LENGTH_SHORT).show()

            }

        }
    }


}