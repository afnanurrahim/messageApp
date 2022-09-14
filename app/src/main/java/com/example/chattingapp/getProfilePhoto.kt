package com.example.chattingapp

import android.app.Activity
import android.app.Notification
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.chattingapp.Chat_Room.ChatRoom
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.net.URI
import java.util.*

class getProfilePhoto : AppCompatActivity() {
    lateinit var buttonAddPic :Button
    lateinit var buttonNext :Button
    lateinit var imageViewProfilePhoto: ImageView
    var selectedPhotoUri: Uri?=null
    lateinit var firebaseStore : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_profile_photo)
        buttonAddPic =findViewById(R.id.buttonAddPic)
        buttonNext =findViewById(R.id.buttonNext)
        imageViewProfilePhoto= findViewById(R.id.imageViewProfilePic)
        selectedPhotoUri= Uri.parse("android.resource://com.example.chattingapp/"+R.drawable.empty_profile)
        firebaseStore= FirebaseFirestore.getInstance()

        buttonAddPic.setOnClickListener(){
            selectImage()
        }

        buttonNext.setOnClickListener(){
            saveImageInFirebase()
        }
    }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent,0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==0 && resultCode==Activity.RESULT_OK && data != null){
            selectedPhotoUri =data.data      // get location of pic`
            val bitmap=MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)

            imageViewProfilePhoto.setImageBitmap(bitmap)

        }

    }

    fun saveImageInFirebase(){
        if (selectedPhotoUri != null) {
            val filename = UUID.randomUUID().toString()
            val userUid = intent.getStringExtra("User's uid")
            val ref = FirebaseStorage.getInstance().getReference("/profilePicture/$filename")

            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Toast.makeText(this@getProfilePhoto,"Image uploaded",Toast.LENGTH_SHORT).show()

                    val intent= Intent(this@getProfilePhoto, NewMessages::class.java)
                    intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)     // to clear the previous (Log In) activity
                    startActivity(intent)
                }.addOnFailureListener(){
                    Toast.makeText(this@getProfilePhoto,"Try again",Toast.LENGTH_SHORT).show()

                }

            var Url : HashMap<String, Any> = HashMap<String, Any> ()
            Url.put("Filename",filename)
//            ref.downloadUrl.addOnSuccessListener {
//                Url.put("URL",it)
//                Log.d("IMAGE PATH",it.toString())
//            }
            firebaseStore.collection("profile_pictures_url")
                .document(userUid!!)
                .set(Url)
                .addOnSuccessListener {
                    Log.d("ANSWER","PROFILE PIC ADDED")
                }.addOnFailureListener {
                    Log.d("ANSWER","ERROR IN UPLOADING URL")
                }
        }
    }
}