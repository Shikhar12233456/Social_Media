package com.example.socialmedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.Adapter.chatAdapter
import com.example.socialmedia.Adapter.chatroomAdapter
import com.example.socialmedia.Utils.UserUtils
import com.example.socialmedia.models.Chat
import com.example.socialmedia.models.ChatRoom
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatFragment : Fragment() {
    var chatroomId: String? = null
    lateinit var ChatAdapter: chatAdapter
    lateinit var chatRecycler: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle= this.arguments

        if(bundle!=null){
            chatroomId= bundle.getString("chatroomId")
        }

        chatRecycler= view.findViewById(R.id.chat_recyclerView)

        setUpRecyclerView()

        val enterMessage: EditText = view.findViewById(R.id.enter_message)
        val sendMessage: ImageView = view.findViewById(R.id.send_message)

        sendMessage.setOnClickListener {
            if(enterMessage.text.isNullOrEmpty()){
                return@setOnClickListener
            }
            val chatText= enterMessage.text.toString()
            val fireStore= FirebaseFirestore.getInstance().collection("Chatrooms")
                .document(chatroomId!!).collection("Messages")

            val chat = Chat(chatText,UserUtils.user!!,System.currentTimeMillis(),chatroomId!!)

            fireStore.document().set(chat).addOnCompleteListener {
                chatRecycler.scrollToPosition(chatRecycler.adapter?.itemCount!! - 1)
                enterMessage.text.clear()
            }
        }
    }

    private fun setUpRecyclerView() {
     val fireStore= FirebaseFirestore.getInstance()
     val query= fireStore.collection("Chatrooms").document(chatroomId!!).collection("Messages")
         .orderBy("time",Query.Direction.ASCENDING)


     val recyclerViewOptions= FirestoreRecyclerOptions.Builder<Chat>().setQuery(query,Chat::class.java).build()

        ChatAdapter = chatAdapter(recyclerViewOptions)
        chatRecycler.adapter = ChatAdapter
        chatRecycler.layoutManager= LinearLayoutManager(context)
    }

    override fun onStart() {
        super.onStart()
        ChatAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        ChatAdapter.stopListening()
    }
}