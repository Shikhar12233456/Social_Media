package com.example.socialmedia


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmedia.models.Post
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class CreatePost : AppCompatActivity() {
    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var postImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        postImage = findViewById(R.id.user_img)
        val postText: TextView = findViewById(R.id.post_text)
        val postButton: Button = findViewById(R.id.post_button)

      postImage.setOnClickListener {
          ImagePicker.with(this)
              .crop()
              .compress(1024)
              .maxResultSize(1000,1080)
              .start()
      }
       postButton.setOnClickListener {
           val text = postText.text.toString()
           if(TextUtils.isEmpty(text)){
               Toast.makeText(this,"Description Can't be Empty",Toast.LENGTH_LONG).show()
               return@setOnClickListener
           }
           addPost(text)
       }
    }

    private fun addPost(text: String){
      val firestore = FirebaseFirestore.getInstance()
          firestore.collection("Users")
              .document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
              .addOnCompleteListener {
                  val uSer =it.result?.toObject(com.example.socialmedia.models.User::class.java)!!

                  val storageRef = FirebaseStorage.getInstance().reference.child("Images")
                      .child(FirebaseAuth.getInstance().currentUser?.email.toString()+"_"+System.currentTimeMillis()+".JPG")

                  val uploadTask = storageRef.putFile(imageUri!!)

                  uploadTask.continueWithTask{Task->
                         if(!Task.isSuccessful){
                             Log.d("Uploaded Task",Task.exception.toString())
                         }
                      storageRef.downloadUrl
                  }.addOnCompleteListener {utc->
                      if(utc.isSuccessful){
                          val downloadUri = utc.result
                          val post = Post(text,downloadUri.toString(), uSer,System.currentTimeMillis())

                          firestore.collection("Posts").document().set(post)
                              .addOnCompleteListener {posted->
                                  if(posted.isSuccessful){
                                      Toast.makeText(this,"Posted Successfully",Toast.LENGTH_LONG).show()
                                      finish()
                                  }else{
                                       Toast.makeText(this,"Something went Wrong",Toast.LENGTH_LONG).show()
                                  }
                              }
                      }else{
                          Log.d("CreatePostActivity",utc.exception.toString())
                      }
                  }
              }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val fileUri= data?.data
                postImage.setImageURI(fileUri)
                imageUri=fileUri
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this,ImagePicker.getError(data),Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this,"Task Cancelled",Toast.LENGTH_LONG).show()

            }
        }
    }

}