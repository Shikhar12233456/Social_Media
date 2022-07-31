package com.example.socialmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.Adapter.CommentsAdapter
import com.example.socialmedia.Utils.UserUtils
import com.example.socialmedia.models.Comment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class CommentsActivity : AppCompatActivity() {
    private  var postId: String? = null
      private var CommentAdapter:CommentsAdapter? = null
      lateinit var  commentsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        postId = intent.getStringExtra("postId")
         commentsRecyclerView= findViewById(R.id.commentsRecyclerView)

        setUpRecyclerView()
        val sendButton: ImageView = findViewById(R.id.send_comment)
        val commentEditText: TextView = findViewById(R.id.edit_comment)

        sendButton.setOnClickListener {
            val ctext= commentEditText.text.toString()
            val fireStore= FirebaseFirestore.getInstance()
            val comment = Comment(ctext,UserUtils.user!!,System.currentTimeMillis())

            fireStore.collection("Posts").document(postId!!)
                .collection("Comments").document().set(comment)

            commentEditText.text=""
        }
    }

    private fun setUpRecyclerView(){
        val fireStore = FirebaseFirestore.getInstance()
        val query = postId?.let {
            fireStore.collection("Posts").document(it).collection("Comments")

        }
        val recyclerViewOptions = query?.let {
            FirestoreRecyclerOptions.Builder<Comment>().setQuery(it,Comment::class.java).build()

        }
        CommentAdapter =  recyclerViewOptions?.let {
            CommentsAdapter(it,this)
        }
        commentsRecyclerView.adapter = CommentAdapter
        commentsRecyclerView.layoutManager= LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        CommentAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        CommentAdapter?.stopListening()
    }
}