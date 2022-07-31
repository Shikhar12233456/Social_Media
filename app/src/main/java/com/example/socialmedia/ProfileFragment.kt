package com.example.socialmedia

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.socialmedia.Utils.UserUtils
import com.example.socialmedia.auth.AuthenticationActivity
import com.example.socialmedia.models.User
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {
   private lateinit var  userImage: ImageView
    private var  imageUri: Uri?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userImage= view.findViewById(R.id.user_image)
        val userName: TextView= view.findViewById(R.id.user_name)
        val userBio: TextView= view.findViewById(R.id.user_bio)
        val saveButton: Button= view.findViewById(R.id.saveButton)
        val logOutButton: Button= view.findViewById(R.id.logoutButton)

        userName.text = UserUtils.user?.name
        userBio.text = UserUtils.user?.bio

        userImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080,1080)
                .start()
        }
        Glide.with(requireContext())
            .load(UserUtils.user?.imageUrl)
            .placeholder(R.drawable.profile_person_black)
            .centerCrop()
            .into(userImage)

        saveButton.setOnClickListener {
            val newUserName= userName.text.toString()
            val newBio = userBio.text.toString()
            if(newUserName.isEmpty()){
                Toast.makeText(context,"Name is required",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val userDocument= FirebaseFirestore.getInstance().collection("Users")
                .document(UserUtils.user?.id!!)

            val user= User(id= UserUtils.user?.id!!,name= newUserName, email = UserUtils.user?.email!!, following = UserUtils.user?.following!!
            , bio = newBio, imageUrl = UserUtils.user?.imageUrl!!)

            userDocument.set(user).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(context,"Details Updated",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context,"Something Went Wrong Please Try Again",Toast.LENGTH_LONG).show()
                }
            }

            UserUtils.getCurrentUser()
        }
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            context?.startActivity(Intent(activity,AuthenticationActivity::class.java))

            activity?.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Activity.RESULT_OK -> {
                val fileUri = data?.data
                userImage.setImageURI(fileUri)
                imageUri = fileUri
                addUserImage()
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(context,ImagePicker.getError(data),Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(context,"Something Went Wrong Please Try Again on Activity",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addUserImage() {
        val fireStore= FirebaseFirestore.getInstance()
        fireStore.collection("Users")
            .document(UserUtils.user?.id!!)
            .get().addOnCompleteListener {
                val storage = FirebaseStorage.getInstance().reference.child("Images")
                    .child(UserUtils.user?.email.toString()+"-"+System.currentTimeMillis()+".JPG")
                val uploadTask = storage.putFile(imageUri!!)
                uploadTask.continueWithTask { task->
                    if(!task.isSuccessful){
                        Log.d("Upload Task",task.exception.toString())
                    }
                    storage.downloadUrl
                }.addOnCompleteListener { urlTaskCompleted->
                    if(urlTaskCompleted.isSuccessful){
                        val downloadUri= urlTaskCompleted.result

                        val newUseR= User(id = UserUtils.user?.id!!, name = UserUtils.user?.name!!, email = UserUtils.user?.email!!,
                        following = UserUtils.user?.following!!, bio = UserUtils.user?.bio!!, imageUrl = downloadUri.toString())

                        fireStore.collection("Users").document(UserUtils.user?.id!!).set(newUseR)
                            .addOnCompleteListener { saved->
                                if(saved.isSuccessful){
                                    UserUtils.getCurrentUser()
                                    Toast.makeText(context,"Image Uploaded",Toast.LENGTH_LONG).show()
                                }else{
                                    Toast.makeText(context,"Error occurred Please Try Again",Toast.LENGTH_LONG).show()
                                }
                            }
                    }else{
                        Toast.makeText(context,"Something went Wrong",Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
}