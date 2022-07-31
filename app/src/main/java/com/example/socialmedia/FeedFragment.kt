package com.example.socialmedia

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.Adapter.FeedAdapter
import com.example.socialmedia.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class FeedFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab: FloatingActionButton=view.findViewById(R.id.floatingActionButton)

        fab.setOnClickListener{
            startActivity(Intent(activity,CreatePost::class.java))
        }
       recyclerView = view.findViewById(R.id.feed_r_v)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val fireStore= FirebaseFirestore.getInstance()
        val quary = fireStore.collection("Posts")

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(quary, Post::class.java).build()
        context?.let {
            adapter = FeedAdapter(recyclerViewOptions, requireContext())
        }

       if(this::adapter.isInitialized) {
           recyclerView.adapter = adapter
       }
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.startListening()
    }
}