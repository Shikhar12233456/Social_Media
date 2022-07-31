package com.example.socialmedia

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.Adapter.chatroomAdapter
import com.example.socialmedia.models.ChatRoom
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatRoomFragment : Fragment() {
    lateinit var  chatroomAdapter: chatroomAdapter
    lateinit var  chatRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatRecyclerView =view.findViewById(R.id.chatroom_recyclerView)
        setUpRecyclerView()

        val createChatRoom: FloatingActionButton= view.findViewById(R.id.addChatRoom)
        createChatRoom.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
            val editText= EditText(context)
            alertDialog.setTitle("Create ChatRoom")
            alertDialog.setMessage("Enter the name of new ChatRoom ")
            alertDialog.setView(editText)

            var textEntered=""
            alertDialog.setPositiveButton("Create"){dialogInterFace,j->
                textEntered = editText.text.toString()
                val document= FirebaseFirestore.getInstance().collection("Chatrooms").document()
                val chatroom= ChatRoom(textEntered,document.id)
                document.set(chatroom)
            }
            alertDialog.setNegativeButton("Cancel"){dialodInterface,i ->
                dialodInterface.dismiss()
            }
            alertDialog.show()
        }
    }

    private fun setUpRecyclerView() {
        val fireStore= FirebaseFirestore.getInstance()
        val query= fireStore.collection("Chatrooms")
            .orderBy("name", Query.Direction.ASCENDING)

        val recyclerViewOption= FirestoreRecyclerOptions.Builder<ChatRoom>().setQuery(query,
            ChatRoom::class.java).build()
        chatroomAdapter= chatroomAdapter(recyclerViewOption,requireActivity())
        chatRecyclerView.adapter =chatroomAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(context)
        chatRecyclerView.itemAnimator = null
    }

    override fun onStart() {
        super.onStart()
        chatroomAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatroomAdapter.stopListening()
    }



}