package com.example.socialmedia

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.Adapter.SearchAdapter
import com.example.socialmedia.Utils.UserUtils
import com.example.socialmedia.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {

   lateinit var adapter: SearchAdapter
   lateinit var  recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar: Toolbar= view.findViewById(R.id.search_toolbar)
        toolBar.title = "Search User"
        (activity as? MainActivity)?.setSupportActionBar(toolBar)
        (activity as? MainActivity)?.supportActionBar?.show()

        setHasOptionsMenu(true)

        val fireStore = FirebaseFirestore.getInstance()
        val query = fireStore.collection("Users")
            .whereNotEqualTo("id",FirebaseAuth.getInstance().currentUser?.uid)

val recyclerViewOptions = FirestoreRecyclerOptions.Builder<User>().setQuery(query,User::class.java).build()
        adapter = SearchAdapter(recyclerViewOptions, requireContext())

        recyclerView = view.findViewById(R.id.search_recyclerView)
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu,menu)

        val searchView = SearchView(requireContext())
        menu.findItem(R.id.action_search).actionView=searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                recyclerView.visibility = View.VISIBLE
                val fireStore = FirebaseFirestore.getInstance()
                val newQuery= fireStore.collection("Users").whereEqualTo("name",query).whereNotEqualTo("id",UserUtils.user?.id)


                val newRecyclerViewOptions= FirestoreRecyclerOptions.Builder<User>().setQuery(newQuery,User::class.java).build()

                adapter.updateOptions(newRecyclerViewOptions)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                recyclerView.visibility = View.INVISIBLE
                return false
            }

        })
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}