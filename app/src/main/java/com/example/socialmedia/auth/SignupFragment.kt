package com.example.socialmedia.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.socialmedia.MainActivity
import com.example.socialmedia.R
import com.example.socialmedia.models.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupFragment : Fragment() {
    companion object{
    const val TAG = "RegisterFragment"
    }

    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailText: TextInputLayout = view.findViewById(R.id.email_Signup)
        val nameText: TextInputLayout = view.findViewById(R.id.name_Signup)
        val passText: TextInputLayout = view.findViewById(R.id.password_Signup)
        val confirmPasswordText: TextInputLayout = view.findViewById(R.id.confirm_pass_Signup)
        val signupButton: Button = view.findViewById(R.id.Signupbutton)
        val gotoLogin: TextView =view.findViewById(R.id.AlreadyAccount)
        val signupProgress: ProgressBar = view.findViewById(R.id.SignupProgressBar)

        signupButton.setOnClickListener {
            val email = emailText.editText?.text.toString()
            val name = nameText.editText?.text.toString()
            val password = passText.editText?.text.toString()
            val confirmPassword = confirmPasswordText.editText?.text.toString()

            emailText.error = null
            nameText.error = null
            passText.error = null
            confirmPasswordText.error = null
            var temp=0
            if(TextUtils.isEmpty(email)){
                emailText.error ="Email is required"
                temp++
            }
            if(TextUtils.isEmpty(name)){
                nameText.error = "Name is required"
                temp++
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailText.error = "please Enter Valid email address"
                temp++
            }
            if(TextUtils.isEmpty(password)){
                passText.error = "Password is required"
                temp++
            }
            if(!password.matches(passwordRegex)){
                passText.error="Password Should Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character"
                temp++
            }
            if(TextUtils.isEmpty(confirmPassword)){
                confirmPasswordText.error = "Confirm password is required"
                temp++
            }
            if(password !=confirmPassword){
                confirmPasswordText.error= "Password doesn't matches"
                temp++
            }
            if(temp>0){
                temp=0
                return@setOnClickListener
            }
            signupProgress.visibility = View.VISIBLE
            val auth = FirebaseAuth.getInstance()

            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                     val user= User(auth.currentUser?.uid!!,name,email)
                        val fireStore=FirebaseFirestore.getInstance().collection("Users")
                        fireStore.document(auth.currentUser?.uid!!).set(user)
                            .addOnCompleteListener { task->
                                if(task.isSuccessful){
                                     startActivity(Intent(activity, MainActivity::class.java))
                                }else{
                                    Log.d(TAG,task.exception.toString())
                                }
                            }
                        signupProgress.visibility =View.GONE
                    }else{
                        signupProgress.visibility =View.GONE
                        Toast.makeText(context,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
        }
       gotoLogin.setOnClickListener {
           fragmentManager?.beginTransaction()
               ?.replace(R.id.authfragcontainer, LoginFragment())
               ?.commit()
       }
    }
}