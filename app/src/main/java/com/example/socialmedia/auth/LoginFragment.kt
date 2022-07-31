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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    companion object{
        const val TAG = "LoginFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val register: TextView=view.findViewById(R.id.gotoregister)
        register.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.authfragcontainer, SignupFragment())
                ?.addToBackStack(null)
                ?.commit()
        }
        val etext: TextInputLayout = view.findViewById(R.id.email_edit_text)
        val passwordtext: TextInputLayout = view.findViewById(R.id.password_edit_text)
        val LoginButton: Button = view.findViewById(R.id.LoginButton)
        val LoginProgressBar: ProgressBar = view.findViewById(R.id.LoginProgressBar)
        LoginButton.setOnClickListener {
            val email = etext.editText?.text.toString()
            val password = passwordtext.editText?.text.toString()

            etext.error = null
            passwordtext.error = null

            var temp=0
            if(TextUtils.isEmpty(email)){
                etext.error ="Email is required"
                temp++
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etext.error = "please Enter Valid email address"
                temp++
            }
            if(TextUtils.isEmpty(password)){
                passwordtext.error = "Password is required"
                temp++
            }
            if(temp>0){
                temp=0
                return@setOnClickListener
            }
             LoginProgressBar.visibility =View.VISIBLE
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        startActivity(Intent(activity, MainActivity::class.java))
                    }else{
                        Toast.makeText(context,it.exception.toString(),Toast.LENGTH_LONG).show()
                        Log.d(TAG,it.exception.toString())
                    }
                }


        }
    }

}